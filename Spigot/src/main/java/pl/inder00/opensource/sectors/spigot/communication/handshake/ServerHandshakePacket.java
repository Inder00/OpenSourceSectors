package pl.inder00.opensource.sectors.spigot.communication.handshake;

import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.protobuf.HandshakePacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

import java.util.logging.Level;

public class ServerHandshakePacket implements IPrototypeListener<HandshakePacket.ServerHandshake> {

    /**
     * Data
     */
    private ISectorClient client;
    private JavaPlugin plugin;

    /**
     * Implementation
     */
    public ServerHandshakePacket(ISectorClient client, JavaPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public void onReceivedData(HandshakePacket.ServerHandshake message) throws Exception {

        // compare versions
        if (!message.getVersion().equals(this.plugin.getDescription().getVersion())) {

            // log
            this.plugin.getLogger().log(Level.SEVERE, "Plugin version mismatch. Stopping server.");

            // stop server
            this.plugin.getServer().shutdown();
            return;

        }

    }
}
