package pl.inder00.opensource.sectors.protocol.listeners;

import io.netty.channel.Channel;
import pl.inder00.opensource.sectors.protocol.ISectorServer;

public interface ISectorServerListener {

    /**
     * Triggered when sector server has been created
     *
     * @param server ISectorServer
     */
    void onServerCreated(ISectorServer server);

    /**
     * Triggered when sector server bounded successfully on network
     *
     * @param server ISectorServer
     */
    void onServerBoundSuccessfully(ISectorServer server);

    /**
     * Triggered when sector server failed to bound on network
     *
     * @param server ISectorServer
     */
    void onServerBoundFailed(ISectorServer server);

    /**
     * Triggered when sector server closed
     *
     * @param server ISectorServer
     */
    void onServerClosed(ISectorServer server);

    /**
     * Triggered when sector server occurred exception
     *
     * @param server ISectorServer
     * @param throwable Throwable
     */
    void onServerException(ISectorServer server, Throwable throwable);

    /**
     * Triggered when occurred exception in client handler
     *
     * @param server ISectorServer
     * @param throwable Throwable
     */
    void onServerClientException(ISectorServer server, Channel channel, Throwable throwable);

}
