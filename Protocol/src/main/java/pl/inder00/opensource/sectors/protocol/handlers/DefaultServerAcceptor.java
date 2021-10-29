package pl.inder00.opensource.sectors.protocol.handlers;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import pl.inder00.opensource.sectors.protobuf.ProtobufSectorSetup;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import reactor.core.publisher.Mono;

public class DefaultServerAcceptor implements SocketAcceptor {

    /**
     * Socket server implementation
     */
    private ISectorServer sectorServer;

    /**
     * Implementation
     */
    public DefaultServerAcceptor(ISectorServer sectorServer) {
        this.sectorServer = sectorServer;
    }

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setupPayload, RSocket socket) {

        // payload
        ByteBuf payload = setupPayload.data();

        try {

            // process data
            ProtobufSectorSetup.SectorSetupPacket.Builder setupSectorData = ProtobufSectorSetup.SectorSetupPacket.newBuilder().mergeFrom(ByteString.copyFrom(payload.nioBuffer()));
            String password = setupSectorData.hasPassword() ? setupSectorData.getPassword() : null;

            // compare data
            if (this.sectorServer.getPassword() == null || (this.sectorServer.getPassword() != null && password != null && this.sectorServer.getPassword().equals(password))) {

                // connection
                RSocket socketConnection = new DefaultServerHandler(this.sectorServer);

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
