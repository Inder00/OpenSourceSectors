package pl.inder00.opensource.sectors.protocol;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ISectorServer {

    /**
     * RSocket server implementation
     *
     * @return RSocketServer
     */
    RSocketServer getSocketServer();

    /**
     * Server password
     *
     * @return String
     */
    String getPassword();

    /**
     * Update server password
     *
     * @param password String / null
     */
    void setPassword(String password);

    /**
     * List of connections to server
     *
     * @return Array of RSocket
     */
    List<RSocket> getListOfConnections();

    /**
     * Bind server over tcp
     *
     * @return Mono of CloseableChannel
     */
    Mono<CloseableChannel> bind();

}
