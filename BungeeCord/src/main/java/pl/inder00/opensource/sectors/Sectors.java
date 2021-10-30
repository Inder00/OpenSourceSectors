package pl.inder00.opensource.sectors;

import net.md_5.bungee.api.ProxyServer;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.communication.ChangeServerPacket;
import pl.inder00.opensource.sectors.communication.ConfigurationPacket;
import pl.inder00.opensource.sectors.configuration.MessagesConfiguration;
import pl.inder00.opensource.sectors.configuration.PluginConfiguration;
import pl.inder00.opensource.sectors.plugin.AbstractPlugin;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorServer;
import pl.inder00.opensource.sectors.protocol.packet.EPacket;
import pl.inder00.opensource.sectors.protocol.packet.PacketManager;

import java.io.File;
import java.util.logging.Level;

public class Sectors extends AbstractPlugin {

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
     * Manager of sectors
     */
    private SectorManager sectorManager;
    /**
     * Master server
     */
    private ISectorServer masterServer;

    public SectorManager getSectorManager() {
        return sectorManager;
    }

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

            // Load plugin configuration
            this.pluginConfiguration = new PluginConfiguration(this, this.configurationFile);
            this.pluginConfiguration.loadConfiguration();

            // Load messages configuration
            this.messagesConfiguration = new MessagesConfiguration(this, this.messagesFile);
            this.messagesConfiguration.loadConfiguration();

            // Load sector manager
            sectorManager = new SectorManager();

            // create master server implementation and bind over tcp
            this.masterServer = new DefaultSectorServer(this.pluginConfiguration.masterHostname, this.pluginConfiguration.masterPort, this.pluginConfiguration.masterPassword != null ? (this.pluginConfiguration.masterPassword.length() > 0 ? this.pluginConfiguration.masterPassword : null) : null);
            this.masterServer.bind()
                    .doOnError(error -> {

                        // log
                        this.getLogger().log(Level.SEVERE, "Failed to bind master server. Stopping proxy...");

                        // stop proxy
                        this.getProxy().stop();

                    })
                    .doOnSuccess(success -> {

                        // log
                        this.getLogger().info("Successfully bound master server.");

                    }).subscribe();

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

    @Override
    public void onDisable() {

    }
}
