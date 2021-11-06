package pl.inder00.opensource.sectors.protocol;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;

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
     * @return Array of Channels
     */
    List<Channel> getConnectionsList();

    /**
     * Bind server
     * Locks thread until response.
     *
     * @throws ProtocolException
     */
    void bind(IInternalServer internalServer);

    /**
     * Shutdowns netty server
     */
    void shutdown();

}
