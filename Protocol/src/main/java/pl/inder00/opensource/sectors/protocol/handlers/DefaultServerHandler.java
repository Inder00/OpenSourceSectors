package pl.inder00.opensource.sectors.protocol.handlers;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.ByteBufPayload;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.exceptions.PacketException;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;
import pl.inder00.opensource.sectors.protocol.packet.PacketManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DefaultServerHandler implements RSocket {

    /**
     * Socket data
     */
    private final ISectorServer sectorServer;
    private Subscriber<Payload> payloadSubscriber;

    /**
     * Implementation
     */
    public DefaultServerHandler(ISectorServer sectorServer, Subscriber<Payload> payloadSubscriber) {
        this.sectorServer = sectorServer;
        this.payloadSubscriber = payloadSubscriber;
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {

        // buffers
        ByteBuf bufferIn = payload.data();

        try {

            // packet id
            if (bufferIn.readableBytes() < 2) throw new PacketException("Unable to decode packet id");
            short packetId = bufferIn.readShort();

            // packet
            IPacket packet = PacketManager.getPacketById(packetId);
            if (packet == null) throw new PacketException("Invalid packet id");

            // create protobuf message
            Message protobufMessage = packet.getBuilder().mergeFrom(ByteString.copyFrom(bufferIn.nioBuffer())).build();

            // execute packet
            packet.execute(protobufMessage);

            // return output
            return Mono.empty();

        } catch (Throwable e) {

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

            // packet id
            if (bufferIn.readableBytes() < 2) throw new PacketException("Unable to decode packet id");
            short packetId = bufferIn.readShort();

            // packet
            IPacket packet = PacketManager.getPacketById(packetId);
            if (packet == null) throw new PacketException("Invalid packet id");

            // create protobuf message
            Message protobufMessage = packet.getBuilder().mergeFrom(ByteString.copyFrom(bufferIn.nioBuffer())).build();

            // execute packet
            Message outputData = packet.execute(protobufMessage);

            // return output
            return Mono.just(ByteBufPayload.create(outputData.toByteArray()));

        } catch (Throwable e) {

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
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {

        // add payloads subscriber
        payloads.subscribe(this.payloadSubscriber);

        // return empty flux
        return Flux.empty();

    }

    @Override
    public Mono<Void> onClose() {

        // unregister connection
        this.sectorServer.getListOfConnections().remove(this);

        // return
        return Mono.never();

    }
}
