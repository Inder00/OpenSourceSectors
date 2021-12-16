package pl.inder00.opensource.sectors.spigot.communication.p2p.encryption;

import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.commons.encryption.IKeyExchangeProvider;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.protobuf.EncryptionPacket;
import pl.inder00.opensource.sectors.protocol.protobuf.HandshakePacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

public class EncryptionFinishPacket implements IPrototypeListener<EncryptionPacket.EncryptionFinish> {

    /**
     * Data
     */
    private final JavaPlugin plugin;
    private final ISectorServer server;

    /**
     * Implementation
     */
    public EncryptionFinishPacket(JavaPlugin plugin, ISectorServer server) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public void onReceivedData(ISectorConnection connection, EncryptionPacket.EncryptionFinish message) throws Exception {

        // check status
        if(message.getCode() == EncryptionPacket.EncryptionCode.OK)
        {

            // enable encryption
            connection.getEncryptionProvider().setEncryptionEnabled(true);

        }

        // send handshake
        connection.sendData(HandshakePacket.ServerHandshake.newBuilder()
                        .setVersion(this.plugin.getDescription().getVersion())
                .build());

        // fire ready event
        this.server.getServerListener().onServerClientReady( this.server, connection );


    }
}
