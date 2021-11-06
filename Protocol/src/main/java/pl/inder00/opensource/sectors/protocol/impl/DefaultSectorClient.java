package pl.inder00.opensource.sectors.protocol.impl;

import com.google.protobuf.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import pl.inder00.opensource.sectors.protocol.handlers.client.DefaultClientChannelInitializer;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorClientListener;

public class DefaultSectorClient implements ISectorClient {

    /**
     * Client properties
     */
    private Bootstrap clientBootstrap;
    private EventLoopGroup clientEventLoopGroup;
    private final ISectorClientListener clientListener;

    /**
     * Client data
     */
    private Channel channel;

    /**
     * Implementation
     */
    public DefaultSectorClient(ISectorClientListener clientListener) {
        this.clientListener = clientListener;
        this.clientListener.onClientCreated(this);
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
            if(this.isConnected()) throw new ProtocolException("Client is already connected");

            // client boostrap
            if(this.clientBootstrap == null){

                // create bootstrap
                this.clientEventLoopGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(0) : new NioEventLoopGroup(0);
                this.clientBootstrap = new Bootstrap();
                this.clientBootstrap.group(this.clientEventLoopGroup);
                this.clientBootstrap.channel(this.clientEventLoopGroup instanceof EpollEventLoopGroup ? EpollSocketChannel.class : NioSocketChannel.class);
                this.clientBootstrap.option(ChannelOption.IP_TOS, 0x18); //https://students.mimuw.edu.pl/SO/Linux/Kod/include/linux/socket.h.html
                this.clientBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
                this.clientBootstrap.handler(new DefaultClientChannelInitializer(this, this.clientListener));

            }

            // try connect to server
            try {

                // connect to server
                ChannelFuture channelFuture = this.clientBootstrap.connect(internalServer.getHostname(), internalServer.getPort()).syncUninterruptibly();
                if(channelFuture.isSuccess() && channelFuture.channel() != null){

                    // update channel
                    this.channel = channelFuture.channel();

                } else {

                    // reconnect
                    this.connect(internalServer);

                }

            } catch (Throwable ex){

                // trigger listener
                this.clientListener.onClientException(this, ex);

            }

        } catch (Throwable e) {

            // trigger listener
            this.clientListener.onClientException(this, e);

        }

    }

    @Override
    public synchronized void sendData(Message message) {

        try {

            // check does client is already connected
            if(!this.isConnected()) throw new ProtocolException("Client is not connected.");

            // send data
            synchronized (this){
                this.channel.writeAndFlush(message);
            }

        } catch (Throwable e){

            // trigger listener
            this.clientListener.onClientException(this, e);

        }

    }

    @Override
    public boolean isConnected() {
        return this.channel != null && this.channel.isActive() && this.channel.isOpen();
    }

}
