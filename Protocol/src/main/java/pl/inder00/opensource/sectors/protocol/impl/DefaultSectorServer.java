package pl.inder00.opensource.sectors.protocol.impl;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.handlers.DefaultServerAcceptor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class DefaultSectorServer implements ISectorServer {

    /**
     * Server properties
     */
    private String hostname;
    private int port;
    private String password;

    /**
     * Server data
     */
    private RSocketServer socketServer;
    private List<RSocket> connectionList = new ArrayList<RSocket>();

    /**
     * Implementation
     */
    public DefaultSectorServer(String hostname, int port) {
        this(hostname, port, null);
    }

    /**
     * Implementation
     */
    public DefaultSectorServer(String hostname, int port, String password) {

        // set server properties
        this.hostname = hostname;
        this.port = port;
        this.password = password;

        // create server implementation
        this.socketServer = RSocketServer.create(new DefaultServerAcceptor(this));
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
