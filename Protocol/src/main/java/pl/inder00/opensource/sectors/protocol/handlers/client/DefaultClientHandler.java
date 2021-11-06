package pl.inder00.opensource.sectors.protocol.handlers.client;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorClientListener;

public class DefaultClientHandler extends SimpleChannelInboundHandler<Message> {

    /**
     * Socket data
     */
    private final ISectorClient sectorClient;
    private final ISectorClientListener clientListener;

    /**
     * Implementation
     */
    public DefaultClientHandler(ISectorClient sectorClient, ISectorClientListener clientListener) {
        this.sectorClient = sectorClient;
        this.clientListener = clientListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // trigger listener
        this.clientListener.onClientConnected(this.sectorClient);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        // trigger listener
        this.clientListener.onClientDisconnected(this.sectorClient);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        // trigger listener
        this.clientListener.onClientException(this.sectorClient, cause);

    }

}
