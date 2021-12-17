package pl.inder00.opensource.sectors.protocol.handlers.client;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorClientListener;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufGeneric;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

import java.net.SocketException;

public class DefaultClientHandler extends SimpleChannelInboundHandler<MessageLite> {

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
    protected void channelRead0(ChannelHandlerContext ctx, MessageLite msg) throws Exception {

        // push message to listeners
        for (IPrototypeListener<MessageLite> listener : this.sectorClient.getPrototypeManager().getListenersByPrototype(msg)) {
            listener.onReceivedData(msg);
        }

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
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        // idle event
        if(evt instanceof IdleStateEvent)
        {

            // send ping to server
            this.sectorClient.sendData(ProtobufGeneric.Empty.getDefaultInstance());

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        // trigger exception
        if(!(cause instanceof SocketException) && !(cause instanceof ReadTimeoutException)){

            // log
            this.clientListener.onClientException(this.sectorClient, cause);

        } else {

            // close
            ctx.close();

        }

    }

}
