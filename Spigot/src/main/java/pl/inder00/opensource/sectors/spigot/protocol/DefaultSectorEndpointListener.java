package pl.inder00.opensource.sectors.spigot.protocol;

import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.listeners.AbstractSectorClientListener;

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
        this.plugin.getLogger().log(Level.INFO, "Connection is ready @ " + client.getChannel().remoteAddress().toString());

    }

    @Override
    public void onClientException(ISectorClient client, Throwable throwable) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Connection to sector endpoint @ " + client.getChannel().remoteAddress().toString() + " occurred an error.", throwable);

    }
}
