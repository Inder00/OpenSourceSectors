import io.netty.channel.Channel;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.commons.basic.impl.InternalServerImpl;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorClient;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorServer;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorClientListener;
import pl.inder00.opensource.sectors.protocol.listeners.ISectorServerListener;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufGeneric;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufHandshake;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class NettyTest {

    public static void main(String[] args) {
        IInternalServer internalServer = new InternalServerImpl("localhost", ThreadLocalRandom.current().nextInt(1024,65535));
        ISectorServer sectorServer = new DefaultSectorServer(new ISectorServerListener() {
            @Override
            public void onServerCreated(ISectorServer server) {
                System.out.println("Server has been created");
            }

            @Override
            public void onServerBoundSuccessfully(ISectorServer server) {
                System.out.println("Server bound successfully");
                ISectorClient sectorClient = new DefaultSectorClient(new ISectorClientListener() {
                    @Override
                    public void onClientCreated(ISectorClient client) {
                        System.out.println("Client has been created.");
                    }

                    @Override
                    public void onClientConnected(ISectorClient client) {
                        System.out.println("Client connected to server.");
                        UUID randomUUID = UUID.randomUUID();
                        client.sendData(ProtobufHandshake.HandshakePacket.Client.newBuilder().setUniqueId(ProtobufGeneric.ProtoUUID.newBuilder()
                                .setLeastSig(randomUUID.getLeastSignificantBits())
                                .setMostSig(randomUUID.getMostSignificantBits())
                                .build())
                                .setVersion("Test").build());
                        client.sendData(ProtobufGeneric.EmptyMessage.newBuilder().build());
                    }

                    @Override
                    public void onClientDisconnected(ISectorClient client) {
                        System.out.println("Client disconnected from server.");
                    }

                    @Override
                    public void onClientException(ISectorClient client, Throwable throwable) {
                        throwable.printStackTrace();
                        System.out.println("Client exception - " + throwable.getMessage());
                        System.exit(1);
                    }
                });
                sectorClient.connect(internalServer);
            }

            @Override
            public void onServerBoundFailed(ISectorServer server) {
                System.out.println("Server failed to bound");
            }

            @Override
            public void onServerClosed(ISectorServer server) {
                System.out.println("Server closed");
            }

            @Override
            public void onServerException(ISectorServer server, Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("Server exception - " + throwable.getMessage());
                System.exit(1);
            }

            @Override
            public void onServerClientException(ISectorServer server, Channel channel, Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("Server exception in " + channel.toString() + " - " + throwable.getMessage());
                System.exit(1);
            }
        });
        sectorServer.bind(internalServer);
    }

}
