package pl.inder00.opensource.sectors.protocol.impl;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.reactivestreams.Subscriber;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.handlers.DefaultServerAcceptor;
import pl.inder00.opensource.sectors.protocol.IServerAcceptor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class DefaultSectorServer implements ISectorServer {

    /**
     * Server properties
     */
    private final String hostname;
    private final int port;
    private String password;

    /**
     * Server data
     */
    private final RSocketServer socketServer;
    private final List<RSocket> connectionList = new ArrayList<RSocket>();

    /**
     * Implementation
     */
    public DefaultSectorServer(String hostname, int port, IServerAcceptor serverAcceptor, Subscriber<Payload> payloadSubscriber) {
        this(hostname, port, null, serverAcceptor, payloadSubscriber);
    }

    /**
     * Implementation
     */
    public DefaultSectorServer(String hostname, int port, String password, IServerAcceptor serverAcceptor, Subscriber<Payload> payloadSubscriber) {

        // set server properties
        this.hostname = hostname;
        this.port = port;
        this.password = password;

        // create server implementation
        this.socketServer = RSocketServer.create(new DefaultServerAcceptor(this, serverAcceptor, payloadSubscriber));
        this.socketServer.payloadDecoder(PayloadDecoder.ZERO_COPY);
        this.socketServer.fragment(65535);

    }

    @Override
    public Mono<CloseableChannel> bind() {
        return this.socketServer.bind(TcpServerTransport.create(this.hostname, this.port));
    }

    @Override
    public RSocketServer getSocketServer() {
        return this.socketServer;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public List<RSocket> getListOfConnections() {
        return this.connectionList;
    }

}
