package pl.inder00.opensource.sectors.protocol.handlers;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import org.reactivestreams.Subscriber;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufSectorSetup;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.IServerAcceptor;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class DefaultServerAcceptor implements SocketAcceptor {

    /**
     * Socket server implementation
     */
    private final ISectorServer sectorServer;
    private final IServerAcceptor serverAcceptor;
    private final Subscriber<Payload> payloadSubscriber;

    /**
     * Implementation
     */
    public DefaultServerAcceptor(ISectorServer sectorServer, IServerAcceptor serverAcceptor, Subscriber<Payload> payloadSubscriber) {
        this.sectorServer = sectorServer;
        this.serverAcceptor = serverAcceptor;
        this.payloadSubscriber = payloadSubscriber;
    }

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setupPayload, RSocket socket) {

        // payload
        ByteBuf payload = setupPayload.data();

        try {

            // process data
            ProtobufSectorSetup.SectorSetupPacket.Builder setupSectorData = ProtobufSectorSetup.SectorSetupPacket.newBuilder().mergeFrom(ByteString.copyFrom(payload.nioBuffer()));
            String password = setupSectorData.hasPassword() ? setupSectorData.getPassword() : null;
            UUID uniqueId = new UUID(setupSectorData.getUniqueId().getMostSig(), setupSectorData.getUniqueId().getLeastSig());

            // compare data
            if (this.sectorServer.getPassword() == null || (this.sectorServer.getPassword() != null && password != null && this.sectorServer.getPassword().equals(password))) {

                // Check does connection has been accepted
                if(!this.serverAcceptor.acceptConnection(uniqueId)) throw new ProtocolException("Connection has been cancelled.");

                // connection
                RSocket socketConnection = new DefaultServerHandler(this.sectorServer, this.payloadSubscriber);

                // register connection
                this.sectorServer.getListOfConnections().add(socketConnection);

                // accept connection
                return Mono.just(socketConnection);

            } else {

                // throw error
                throw new ProtocolException("Invalid credentials data.");

            }


        } catch (Throwable e) {

            // deny connection
            return Mono.error(e);

        }

    }
}
