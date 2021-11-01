package pl.inder00.opensource.sectors.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import pl.inder00.opensource.sectors.bungeecord.basic.ISectorManager;
import pl.inder00.opensource.sectors.bungeecord.basic.manager.SectorManagerImpl;
import pl.inder00.opensource.sectors.bungeecord.communication.ChangeServerPacket;
import pl.inder00.opensource.sectors.bungeecord.communication.ConfigurationPacket;
import pl.inder00.opensource.sectors.bungeecord.configuration.MessagesConfiguration;
import pl.inder00.opensource.sectors.bungeecord.configuration.PluginConfiguration;
import pl.inder00.opensource.sectors.bungeecord.plugin.AbstractPlugin;
import pl.inder00.opensource.sectors.bungeecord.protocol.DefaultServerAcceptor;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.handlers.stream.ServerStreamSubscriber;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorServer;
import pl.inder00.opensource.sectors.protocol.packet.EPacket;
import pl.inder00.opensource.sectors.protocol.packet.PacketManager;

import java.io.File;
import java.util.logging.Level;

public class Sectors extends AbstractPlugin {

    /**
     * Plugin configurations
     */
    public PluginConfiguration pluginConfiguration;
    public MessagesConfiguration messagesConfiguration;

    /**
     * Configuration file
     */
    private File configurationFile;

    /**
     * Messages file
     */
    private File messagesFile;

    /**
     * Master server
     */
    private static ISectorServer masterServer;

    /**
     * Sector manager
     */
    private static ISectorManager sectorManager;

    @Override
    public void onEnable() {

        try {

            // Save default plugin configuration locally
            this.configurationFile = new File(this.getDataFolder(), "configuration.yml");
            if (!this.configurationFile.exists()) {
                this.saveResource("configuration.yml", true);
            }

            // Save default messages configuration locally
            this.messagesFile = new File(this.getDataFolder(), "messages.yml");
            if (!this.messagesFile.exists()) {
                this.saveResource("messages.yml", true);
            }

            // create sector manager
            sectorManager = new SectorManagerImpl();

            // Load plugin configuration
            this.pluginConfiguration = new PluginConfiguration(this, this.configurationFile);
            this.pluginConfiguration.loadConfiguration();

            // Load messages configuration
            this.messagesConfiguration = new MessagesConfiguration(this, this.messagesFile);
            this.messagesConfiguration.loadConfiguration();

            // create master server implementation and bind over tcp
            masterServer = new DefaultSectorServer(this.pluginConfiguration.masterHostname, this.pluginConfiguration.masterPort, this.pluginConfiguration.masterPassword != null ? (this.pluginConfiguration.masterPassword.length() > 0 ? this.pluginConfiguration.masterPassword : null) : null, new DefaultServerAcceptor(), new ServerStreamSubscriber());
            masterServer.bind()
                    .doOnError(error -> {

                        // log
                        this.getLogger().log(Level.SEVERE, "Failed to bind master server. Stopping proxy...");

                        // stop proxy
                        this.getProxy().stop();

                    })
                    .doOnSuccess(success -> {

                        // log
                        this.getLogger().info("Successfully bound master server.");

                    })
                    .subscribe();

            // register masterserver packets
            PacketManager.registerPacket(EPacket.CONFIGURATION_REQUEST, new ConfigurationPacket(this));
            PacketManager.registerPacket(EPacket.SERVER_CHANGE, new ChangeServerPacket(this));

        } catch (Throwable e) {

            // log
            this.getLogger().log(Level.SEVERE, "An error occurred during enabling plugin.", e);

            // stop proxy
            ProxyServer.getInstance().stop();

        }

    }

    /**
     * Returns bungeecord's master server
     * @return ISectorServer
     */
    public static ISectorServer getMasterServer() {
        return masterServer;
    }

    /**
     * Returns sector manager
     * @return ISectorManager
     */
    public static ISectorManager getSectorManager() {
        return sectorManager;
    }

}
