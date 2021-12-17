package pl.inder00.opensource.sectors.spigot.protocol;

import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.listeners.AbstractSectorClientListener;
import pl.inder00.opensource.sectors.spigot.Sectors;

import java.util.logging.Level;

public class DefaultSectorEndpointListener extends AbstractSectorClientListener {

    /**
     * Data
     */
    private final JavaPlugin plugin;
    private final IInternalServer internalServer;

    /**
     * Implementation
     */
    public DefaultSectorEndpointListener(JavaPlugin plugin, IInternalServer internalServer) {
        this.plugin = plugin;
        this.internalServer = internalServer;
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

        // reconnect to endpoint
        client.connect(this.internalServer);

    }

    @Override
    public void onClientException(ISectorClient client, Throwable throwable) {

        // log
        this.plugin.getLogger().log(Level.SEVERE, "Endpoint Connection to sector endpoint occurred an error.", throwable);

    }
}
