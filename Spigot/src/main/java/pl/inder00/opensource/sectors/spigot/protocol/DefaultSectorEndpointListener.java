package pl.inder00.opensource.sectors.spigot.protocol;

import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.listeners.AbstractSectorClientListener;
import pl.inder00.opensource.sectors.spigot.Sectors;

import java.util.logging.Level;

public class DefaultSectorEndpointListener extends AbstractSectorClientListener {

    /**
     * Data
     */
    private final JavaPlugin plugin;

    /**
     * Implementation
     */
    public DefaultSectorEndpointListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onClientReady(ISectorClient client) {

        // log
        this.plugin.getLogger().log(Level.INFO, "Endpoint Connection is ready @ " + client.getChannel().remoteAddress().toString());

    }

    @Override
    public void onClientDisconnected(ISectorClient client) {

        // disable encryption
        client.getEncryptionProvider().setEncryptionEnabled( false );

    }

    @Override
    public void onClientException(ISectorClient client, Throwable throwable) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Endpoint Connection to sector endpoint occurred an error.", throwable);

    }
}
