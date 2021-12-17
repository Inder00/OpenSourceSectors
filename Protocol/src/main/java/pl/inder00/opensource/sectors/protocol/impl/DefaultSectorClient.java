package pl.inder00.opensource.sectors.protocol.impl;

import com.google.protobuf.MessageLite;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.commons.concurrent.FutureCallback;
import pl.inder00.opensource.sectors.commons.encryption.IEncryptionProvider;
import pl.inder00.opensource.sectors.commons.encryption.impl.DefaultEncryptionProvider;
import pl.inder00.opensource.sectors.protocol.IPacketStatus;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import pl.inder00.opensource.sectors.protocol.handlers.client.DefaultClientHandler;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorClientListener;
import pl.inder00.opensource.sectors.protocol.pipelines.EncryptionDecoder;
import pl.inder00.opensource.sectors.protocol.pipelines.EncryptionEncoder;
import pl.inder00.opensource.sectors.protocol.pipelines.ProtobufDecoder;
import pl.inder00.opensource.sectors.protocol.pipelines.ProtobufEncoder;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeManager;
import pl.inder00.opensource.sectors.protocol.prototype.impl.PrototypeManagerImpl;

import java.util.concurrent.TimeUnit;

public class DefaultSectorClient implements ISectorClient {

    /**
     * Client properties
     */
    private final ISectorClientListener clientListener;
    private IEncryptionProvider encryptionProvider;
    private Bootstrap clientBootstrap;
    private EventLoopGroup clientEventLoopGroup;
    private IPrototypeManager prototypeManager;

    /**
     * Client data
     */
    private Channel channel;

    /**
     * Implementation
     */
    public DefaultSectorClient(ISectorClientListener clientListener) {
        this.encryptionProvider = new DefaultEncryptionProvider();
        this.prototypeManager = new PrototypeManagerImpl();
        this.clientListener = clientListener;
        this.clientListener.onClientCreated(this);
    }

    @Override
    public IPrototypeManager getPrototypeManager() {
        return this.prototypeManager;
    }

    @Override
    public ChannelPipeline getChannelPipeline() {
        return this.channel.pipeline();
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void connect(IInternalServer internalServer) {

        try {

            // check does client is already connected
            if (this.isConnected()) throw new ProtocolException("Client is already connected");

            // client boostrap
            if (this.clientBootstrap == null) {

                // create bootstrap
                this.clientEventLoopGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(0) : new NioEventLoopGroup(0);
                this.clientBootstrap = new Bootstrap();
                this.clientBootstrap.group(this.clientEventLoopGroup);
                this.clientBootstrap.channel(this.clientEventLoopGroup instanceof EpollEventLoopGroup ? EpollSocketChannel.class : NioSocketChannel.class);
                this.clientBootstrap.option(ChannelOption.IP_TOS, 0x18); //https://students.mimuw.edu.pl/SO/Linux/Kod/include/linux/socket.h.html
                this.clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        // add pipelines
                        ch.pipeline().addLast("p-idleHandler", new IdleStateHandler(0,0,4000, TimeUnit.MILLISECONDS));
                        ch.pipeline().addLast("p-frameEncoder", new LengthFieldPrepender(8));
                        ch.pipeline().addLast("p-frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 8, 0, 8));
                        ch.pipeline().addLast("p-encryptionEncoder", new EncryptionEncoder(encryptionProvider));
                        ch.pipeline().addLast("p-encryptionDecoder", new EncryptionDecoder(encryptionProvider));
                        ch.pipeline().addLast("p-protoEncoder", new ProtobufEncoder(DefaultSectorClient.this.getPrototypeManager()));
                        ch.pipeline().addLast("p-protoDecoder", new ProtobufDecoder(DefaultSectorClient.this.getPrototypeManager()));
                        ch.pipeline().addLast("p-handler", new DefaultClientHandler(DefaultSectorClient.this, clientListener));

                    }

                });

            }

            // try connect to server
            try {

                // connect to server
                this.clientBootstrap.connect(internalServer.getHostname(), internalServer.getPort()).addListener((GenericFutureListener<ChannelFuture>) callback ->
                {

                    // check does connected successfully
                    if(callback.isSuccess())
                    {

                        // update channel
                        this.channel = callback.channel();

                    }
                    else
                    {

                        // reconnect after 100 millis
                        callback.channel().eventLoop().schedule(() -> this.connect(internalServer), 100, TimeUnit.MILLISECONDS);

                    }

                });

            } catch (Throwable ex) {

                throw ex;

            }

        } catch (Throwable e) {

            // trigger listener
            this.clientListener.onClientException(this, e);

        }

    }

    @Override
    public IEncryptionProvider getEncryptionProvider() {
        return this.encryptionProvider;
    }

    @Override
    public void setEncryptionProvider(IEncryptionProvider encryptionProvider) {
        this.encryptionProvider = encryptionProvider;
    }

    @Override
    public ISectorClientListener getClientListener() {
        return this.clientListener;
    }

    @Override
    public synchronized void sendData(MessageLite message) {

        // send synchronized data
        synchronized (this) {
            try {

                // check does client is already connected
                if (!this.isConnected()) throw new ProtocolException("Client is not connected.");

                // write and flush data
                this.channel.writeAndFlush(message);

            } catch (Throwable e) {

                // trigger listener
                this.clientListener.onClientException(this, e);

            }
        }

    }

    @Override
    public synchronized void sendData(MessageLite message, FutureCallback<IPacketStatus> packetStatus) {

        // send synchronized data
        synchronized (this) {

            // check does client is already connected
            if (!this.isConnected()) packetStatus.execute(IPacketStatus.ERROR);

            // write and flush data
            this.channel.writeAndFlush(message).addListener(status -> packetStatus.execute(status.isSuccess() ? IPacketStatus.OK : IPacketStatus.ERROR));

        }

    }

    @Override
    public boolean isConnected() {
        return this.channel != null && this.channel.isActive();
    }

    @Override
    public void disconnect() {

        // check does client is connected
        if(this.channel == null || this.clientEventLoopGroup == null) return;

        try {

            // close channel
            this.channel.close().syncUninterruptibly();

            // shutdown client
            this.clientEventLoopGroup.shutdownGracefully().syncUninterruptibly();

            // trigger event
            this.clientListener.onClientDisconnected(this);

        } catch (Throwable e) {

            // trigger listener
            this.clientListener.onClientException(this, e);

        }

    }
}
