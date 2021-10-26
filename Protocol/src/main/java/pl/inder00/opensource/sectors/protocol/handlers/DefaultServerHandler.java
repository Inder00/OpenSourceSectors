package pl.inder00.opensource.sectors.protocol.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.ByteBufPayload;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.exceptions.PacketException;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;
import pl.inder00.opensource.sectors.protocol.packet.PacketManager;
import reactor.core.publisher.Mono;

public class DefaultServerHandler implements RSocket {

    /**
     * Socket data
     */
    private ISectorServer sectorServer;

    /**
     * Implementation
     */
    public DefaultServerHandler(ISectorServer sectorServer) {
        this.sectorServer = sectorServer;
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {

        // buffers
        ByteBuf bufferIn = payload.data();

        try {

            // packet id
            if( bufferIn.readableBytes() < 2 ) throw new PacketException("Unable to decode packet id");
            short packetId = bufferIn.readShort();

            // packet
            IPacket packet = PacketManager.getPacketById( packetId );
            if(packet == null) throw new PacketException("Invalid packet id");

            // execute packet
            packet.execute( bufferIn, null );

            // return output
            return Mono.empty();

        } catch (Throwable e){

            // throw error
            e.printStackTrace();

            // return error
            return Mono.error(e);

        } finally {

            // release
            payload.release();

        }

    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {

        // input buffer
        ByteBuf bufferIn = payload.data();

        try {

            // output buffer
            ByteBuf bufferOut = Unpooled.buffer( 16 );

            // packet id
            if( bufferIn.readableBytes() < 2 ) throw new PacketException("Unable to decode packet id");
            short packetId = bufferIn.readShort();

            // packet
            IPacket packet = PacketManager.getPacketById( packetId );
            if(packet == null) throw new PacketException("Invalid packet id");

            // execute packet
            packet.execute( bufferIn, bufferOut );

            // return output
            return Mono.just(ByteBufPayload.create(bufferOut));

        } catch (Throwable e){

            // throw error
            e.printStackTrace();

            // return error
            return Mono.error(e);

        } finally {

            // release
            payload.release();

        }

    }

    @Override
    public Mono<Void> onClose() {

        // unregister connection
        this.sectorServer.getListOfConnections().remove(this);

        // return
        return Mono.never();

    }
}
