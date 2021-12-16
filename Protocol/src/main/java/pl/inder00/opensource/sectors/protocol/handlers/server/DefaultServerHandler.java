package pl.inder00.opensource.sectors.protocol.handlers.server;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorServerListener;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

import java.util.UUID;

public class DefaultServerHandler extends SimpleChannelInboundHandler<MessageLite> {

    /**
     * Socket data
     */
    private final ISectorConnection connection;
    private final ISectorServer sectorServer;
    private final ISectorServerListener serverListener;

    /**
     * Implementation
     */
    public DefaultServerHandler(ISectorConnection connection, ISectorServer sectorServer, ISectorServerListener serverListener) {
        this.connection = connection;
        this.sectorServer = sectorServer;
        this.serverListener = serverListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageLite msg) throws Exception {

        // push message to listeners
        for (IPrototypeListener<MessageLite> listener : this.sectorServer.getPrototypeManager().getListenersByPrototype(msg)) {
            listener.onReceivedData(this.connection,msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        // trigger exception
        this.serverListener.onServerClientException(this.sectorServer, this.connection, cause);

    }

}
