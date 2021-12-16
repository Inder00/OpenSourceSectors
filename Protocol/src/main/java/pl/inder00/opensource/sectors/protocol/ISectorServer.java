package pl.inder00.opensource.sectors.protocol;

import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.commons.managers.IManager;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorServerListener;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeManager;

import java.util.List;

public interface ISectorServer {

    /**
     * Server channel implementation
     *
     * @return Channel
     */
    Channel getServerChannel();

    /**
     * List of connections to server
     *
     * @return Array of Connections
     */
    List<ISectorConnection> getConnectionsList();

    /**
     * Returns sector connection by channel
     *
     * @param channel Channel
     * @return ISectorConnection
     */
    ISectorConnection getConnectionByChannel(Channel channel);

    /**
     * Prototype manager implementation
     *
     * @return Prototype manager
     */
    IPrototypeManager getPrototypeManager();

    /**
     * Returns server listener implementation
     *
     * @return ISectorServerListener
     */
    ISectorServerListener getServerListener();

    /**
     * Bind server
     * Locks thread until response.
     *
     * @throws ProtocolException
     */
    void bind(IInternalServer internalServer);

    /**
     * Returns boolean representing does server is currently bound and active
     *
     * @return boolean
     */
    boolean isActive();

    /**
     * Shutdowns netty server
     */
    void shutdown();

}
