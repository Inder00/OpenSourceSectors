package pl.inder00.opensource.sectors.spigot.protocol;

import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.protocol.IPacketStatus;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.listeners.AbstractSectorClientListener;
import pl.inder00.opensource.sectors.protocol.protobuf.ConfigurationPacket;
import pl.inder00.opensource.sectors.spigot.Sectors;

import java.util.logging.Level;

public class DefaultMasterServerListener extends AbstractSectorClientListener {

    /**
     * Data
     */
    private final Sectors plugin;

    /**
     * Implementation
     */
    public DefaultMasterServerListener(Sectors plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onClientReady(ISectorClient client) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Connection is ready @ " + client.getChannel().remoteAddress().toString());

        // request configuration
        client.sendData(ConfigurationPacket.Request.getDefaultInstance(), data -> this.plugin.getLogger().log(data.equals(IPacketStatus.OK) ? Level.INFO : Level.SEVERE, data.equals(IPacketStatus.OK) ? "Successfully requested master server for configuration." : "Failed to request master server for configuration."));

    }

    @Override
    public void onClientDisconnected(ISectorClient client) {

        // disable encryption
        client.getEncryptionProvider().setEncryptionEnabled( false );

        // reconnect
        client.connect(Sectors.getMasterServerInternalServer());


    }

    @Override
    public void onClientException(ISectorClient client, Throwable throwable) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Connection to master server occurred an error.", throwable);

    }
}
