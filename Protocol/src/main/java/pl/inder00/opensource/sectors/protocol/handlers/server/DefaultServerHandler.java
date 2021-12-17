package pl.inder00.opensource.sectors.protocol.handlers.server;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorServerListener;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

import java.net.SocketException;

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
            listener.onReceivedData(this.connection, msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        // trigger exception
        if(!(cause instanceof SocketException) && !(cause instanceof ReadTimeoutException)){

            // log
            this.serverListener.onServerClientException(this.sectorServer, this.connection, cause);

        } else {

            // close
            ctx.close();

        }

    }

}
