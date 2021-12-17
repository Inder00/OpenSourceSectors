package pl.inder00.opensource.sectors.bungeecord.communication.encryption;

import net.md_5.bungee.api.plugin.Plugin;
import pl.inder00.opensource.sectors.bungeecord.Sectors;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.protobuf.EncryptionPacket;
import pl.inder00.opensource.sectors.protocol.protobuf.HandshakePacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

public class EncryptionFinishPacket implements IPrototypeListener<EncryptionPacket.EncryptionFinish> {

    /**
     * Data
     */
    private Plugin plugin;

    /**
     * Implementation
     */
    public EncryptionFinishPacket(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceivedData(ISectorConnection connection, EncryptionPacket.EncryptionFinish message) throws Exception {

        // check status
        if (message.getCode() == EncryptionPacket.EncryptionCode.OK) {

            // enable encryption
            connection.getEncryptionProvider().setEncryptionEnabled(true);

        }

        // send handshake
        connection.sendData(HandshakePacket.ServerHandshake.newBuilder()
                .setVersion(this.plugin.getDescription().getVersion())
                .build());

        // fire ready event
        Sectors.getMasterServer().getServerListener().onServerClientReady(Sectors.getMasterServer(), connection);


    }
}
