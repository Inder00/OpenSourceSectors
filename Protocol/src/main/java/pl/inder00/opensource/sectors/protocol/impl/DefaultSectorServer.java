package pl.inder00.opensource.sectors.protocol.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorServerListener;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import pl.inder00.opensource.sectors.protocol.handlers.server.DefaultServerChannelInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultSectorServer implements ISectorServer {

    /**
     * Server data
     */
    private Channel serverChannel;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup serverEventLoopGroup;
    private final ISectorServerListener serverListener;

    /**
     * Implementation
     */
    public DefaultSectorServer(ISectorServerListener serverListener) {
        this.serverListener = serverListener;
        this.serverListener.onServerCreated(this);
    }

    /**
     * Connection list
     */
    private final List<Channel> connectionList = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Channel getServerChannel() {
        return this.serverChannel;
    }

    @Override
    public List<Channel> getConnectionsList() {
        return this.connectionList;
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
                this.serverBootstrap.childHandler(new DefaultServerChannelInitializer(this, this.serverListener));
                this.serverBootstrap.handler(new ChannelHandlerAdapter() {
                    @Override
                    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

                        // add channel to connections list
                        connectionList.add(ctx.channel());

                    }

                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

                        // remove channel from connections list
                        connectionList.remove(ctx.channel());

                    }
                });

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
}
