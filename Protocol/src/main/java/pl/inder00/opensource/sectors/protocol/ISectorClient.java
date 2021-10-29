package pl.inder00.opensource.sectors.protocol;

import io.rsocket.RSocket;
import reactor.core.publisher.Mono;

public interface ISectorClient {

    /**
     * RSocket client implementation
     *
     * @return RSocketServer
     */
    RSocket getRSocket();

    /**
     * Connect to server
     *
     * @return Mono of CloseableChannel
     */
    Mono<RSocket> connect();

}
