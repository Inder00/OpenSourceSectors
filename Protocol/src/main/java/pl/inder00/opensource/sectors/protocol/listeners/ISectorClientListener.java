package pl.inder00.opensource.sectors.protocol.listeners;

import pl.inder00.opensource.sectors.protocol.ISectorClient;

public interface ISectorClientListener {

    /**
     * Triggered when sector client has been created
     *
     * @param client ISectorClient
     */
    void onClientCreated(ISectorClient client);

    /**
     * Triggered when sector client connects to server
     *
     * @param client ISectorClient
     */
    void onClientConnected(ISectorClient client);

    /**
     * Triggered when sector client disconnects from server
     *
     * @param client ISectorClient
     */
    void onClientDisconnected(ISectorClient client);

    /**
     * Triggered when client occurred exception
     *
     * @param client ISectorClient
     * @param throwable Throwable
     */
    void onClientException(ISectorClient client, Throwable throwable);

}
