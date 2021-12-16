package pl.inder00.opensource.sectors.protocol.impl;

import com.google.protobuf.MessageLite;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.handlers.server.DefaultServerHandler;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorServerListener;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import pl.inder00.opensource.sectors.protocol.pipelines.EncryptionDecoder;
import pl.inder00.opensource.sectors.protocol.pipelines.EncryptionEncoder;
import pl.inder00.opensource.sectors.protocol.pipelines.ProtobufDecoder;
import pl.inder00.opensource.sectors.protocol.pipelines.ProtobufEncoder;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeManager;
import pl.inder00.opensource.sectors.protocol.prototype.impl.PrototypeManagerImpl;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSectorServer implements ISectorServer {

    /**
     * Server data
     */
    private Channel serverChannel;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup serverEventLoopGroup;
    private final ISectorServerListener serverListener;
    private IPrototypeManager prototypeManager;

    /**
     * Connection list
     */
    private final Map<Channel, ISectorConnection> connectionList = new ConcurrentHashMap();

    /**
     * Implementation
     */
    public DefaultSectorServer(ISectorServerListener serverListener) {

        // data
        this.prototypeManager = new PrototypeManagerImpl();
        this.serverListener = serverListener;
        this.serverListener.onServerCreated(this);
    }

    @Override
    public IPrototypeManager getPrototypeManager() {
        return this.prototypeManager;
    }

    @Override
    public Channel getServerChannel() {
        return this.serverChannel;
    }

    @Override
    public List<ISectorConnection> getConnectionsList() {
        return new ArrayList<>(this.connectionList.values());
    }

    @Override
    public ISectorConnection getConnectionByChannel(Channel channel) {
        return this.connectionList.get(channel);
    }

    @Override
    public ISectorServerListener getServerListener() {
        return this.serverListener;
    }

    @Override
    public void bind(IInternalServer internalServer) {

        try {

            // check does server is already bound
            if(this.serverChannel != null && this.serverChannel.isActive()) throw new ProtocolException("Server is already bound");

            // server boostrap
            if(this.serverBootstrap == null){

                // create bootstrap
                this.serverEventLoopGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(0) : new NioEventLoopGroup(0);
                this.serverBootstrap = new ServerBootstrap();
                this.serverBootstrap.group(this.serverEventLoopGroup);
                this.serverBootstrap.channel(this.serverEventLoopGroup instanceof EpollEventLoopGroup ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
                this.serverBootstrap.option(ChannelOption.SO_BACKLOG, 128); //https://man7.org/linux/man-pages/man2/listen.2.html
                this.serverBootstrap.childOption(ChannelOption.IP_TOS, 0x18); //https://students.mimuw.edu.pl/SO/Linux/Kod/include/linux/socket.h.html
                this.serverBootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
                this.serverBootstrap.childHandler(new BossChildHandler());

            }

            // try to bind server
            try {

                // bind server
                ChannelFuture channelFuture = this.serverBootstrap.bind(internalServer.getHostname(), internalServer.getPort()).syncUninterruptibly();
                if(channelFuture.isSuccess() && channelFuture.channel() != null){

                    // update server channel
                    this.serverChannel = channelFuture.channel();

                    // trigger listener
                    this.serverListener.onServerBoundSuccessfully(this);

                } else {

                    // trigger listener
                    this.serverListener.onServerBoundFailed(this);

                }

            } catch (Throwable ex){

                // trigger listeners
                this.serverListener.onServerBoundFailed(this);
                this.serverListener.onServerException(this, ex);

            }

        } catch (Throwable e){

            // trigger listener
            this.serverListener.onServerException(this, e);

        }

    }

    @Override
    public void shutdown() {

        try {

            // shutdown server
            this.serverEventLoopGroup.shutdownGracefully().syncUninterruptibly();

            // trigger event
            this.serverListener.onServerClosed(this);

        } catch (Throwable e){

            // trigger listener
            this.serverListener.onServerException(this, e);

        }

    }

    @Override
    public boolean isActive() {
        return this.serverChannel != null && this.serverChannel.isActive();
    }

    @ChannelHandler.Sharable
    private class BossChildHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

            // create sector connection implementation
            ISectorConnection sectorConnection = new DefaultSectorConnection(UUID.randomUUID(),ctx.channel());

            // add pipelines
            ctx.channel().pipeline().addLast("p-frameEncoder", new LengthFieldPrepender(8));
            ctx.channel().pipeline().addLast("p-frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 8, 0, 8));
            ctx.channel().pipeline().addLast("p-encryptionEncoder", new EncryptionEncoder(sectorConnection.getEncryptionProvider()));
            ctx.channel().pipeline().addLast("p-encryptionDecoder", new EncryptionDecoder(sectorConnection.getEncryptionProvider()));
            ctx.channel().pipeline().addLast("p-protoEncoder", new ProtobufEncoder(DefaultSectorServer.this.getPrototypeManager()));
            ctx.channel().pipeline().addLast("p-protoDecoder", new ProtobufDecoder(DefaultSectorServer.this.getPrototypeManager()));
            ctx.channel().pipeline().addLast("p-handler", new DefaultServerHandler(sectorConnection,DefaultSectorServer.this,serverListener));

            // add connection to list
            connectionList.put( ctx.channel(), sectorConnection );

            // fire listener event
            serverListener.onServerClientConnect(DefaultSectorServer.this, sectorConnection);

            // fire event
            ctx.fireChannelRegistered();

        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

            // fire listener event
            serverListener.onServerClientDisconnect(DefaultSectorServer.this, connectionList.get(ctx.channel()));

            // remove connection from list
            connectionList.remove( ctx.channel() );

            // fire event
            ctx.fireChannelUnregistered();

        }

    }

}
