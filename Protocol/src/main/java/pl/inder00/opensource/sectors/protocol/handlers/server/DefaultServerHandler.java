package pl.inder00.opensource.sectors.protocol.handlers.server;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorServerListener;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufHandshake;

public class DefaultServerHandler extends SimpleChannelInboundHandler<Message> {

    /**
     * Socket data
     */
    private final ISectorServer sectorServer;
    private final ISectorServerListener serverListener;

    /**
     * Implementation
     */
    public DefaultServerHandler(ISectorServer sectorServer,  ISectorServerListener serverListener) {
        this.sectorServer = sectorServer;
        this.serverListener = serverListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        // trigger exception
        this.serverListener.onServerClientException(this.sectorServer, ctx.channel(), cause);

    }

}
