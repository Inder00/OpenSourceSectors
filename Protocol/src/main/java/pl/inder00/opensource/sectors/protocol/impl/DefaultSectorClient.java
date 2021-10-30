package pl.inder00.opensource.sectors.protocol.impl;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolPayloadUtils;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

public class DefaultSectorClient implements ISectorClient {

    /**
     * Client properties
     */
    private final String hostname;
    private final String password;
    private final int port;

    /**
     * Client data
     */
    private final RSocketConnector socketConnector;
    private RSocket socketClient;

    /**
     * Implementation
     */
    public DefaultSectorClient(String hostname, int port) {
        this(hostname, port, null);
    }

    /**
     * Implementation
     */
    public DefaultSectorClient(String hostname, int port, String password) {

        // set server properties
        this.hostname = hostname;
        this.port = port;
        this.password = password;

        // create socket connector implementation
        this.socketConnector = RSocketConnector.create()
                .keepAlive(Duration.ofSeconds(5), Duration.ofSeconds(10))
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .setupPayload(ProtocolPayloadUtils.createSetupPayload(password))
                .fragment(65535)
                .reconnect(Retry.fixedDelay(Integer.MAX_VALUE, Duration.ofSeconds(1)));

    }

    @Override
    public RSocket getRSocket() {
        return this.socketClient;
    }

    @Override
    public Mono<RSocket> connect() {
        return this.socketConnector.connect(TcpClientTransport.create(this.hostname, this.port)).doOnSuccess(socket -> this.socketClient = socket);
    }
}
