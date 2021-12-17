package pl.inder00.opensource.sectors.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import org.bstats.bungeecord.Metrics;
import pl.inder00.opensource.sectors.bungeecord.basic.ISectorManager;
import pl.inder00.opensource.sectors.bungeecord.basic.manager.SectorManagerImpl;
import pl.inder00.opensource.sectors.bungeecord.communication.configuration.ConfigurationRequestPacket;
import pl.inder00.opensource.sectors.bungeecord.communication.encryption.EncryptionFinishPacket;
import pl.inder00.opensource.sectors.bungeecord.communication.encryption.EncryptionServerHelloPacket;
import pl.inder00.opensource.sectors.bungeecord.communication.server.ChangeServerPacket;
import pl.inder00.opensource.sectors.bungeecord.configuration.MessagesConfiguration;
import pl.inder00.opensource.sectors.bungeecord.configuration.PluginConfiguration;
import pl.inder00.opensource.sectors.bungeecord.plugin.AbstractPlugin;
import pl.inder00.opensource.sectors.bungeecord.protocol.AbstractMasterServerListener;
import pl.inder00.opensource.sectors.commons.basic.impl.InternalServerImpl;
import pl.inder00.opensource.sectors.commons.encryption.IKeyExchangeProvider;
import pl.inder00.opensource.sectors.commons.encryption.impl.DefaultDiffieHellmanProvider;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorServer;
import pl.inder00.opensource.sectors.protocol.protobuf.ConfigurationPacket;
import pl.inder00.opensource.sectors.protocol.protobuf.EncryptionPacket;
import pl.inder00.opensource.sectors.protocol.protobuf.ServerPacket;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.logging.Level;

public class Sectors extends AbstractPlugin {

    /**
     * Master server
     */
    private static ISectorServer masterServer;
    /**
     * Sector manager
     */
    private static ISectorManager sectorManager;
    /**
     * Encryption provider
     */
    private static IKeyExchangeProvider keyExchangeProvider;
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
     * Metrics provider
     */
    private Metrics metrics;

    /**
     * Returns bungeecord's master server
     *
     * @return ISectorServer
     */
    public static ISectorServer getMasterServer() {
        return masterServer;
    }

    /**
     * Returns sector manager
     *
     * @return ISectorManager
     */
    public static ISectorManager getSectorManager() {
        return sectorManager;
    }

    /**
     * Returns encryption provider
     *
     * @return IEncryptionProvider
     */
    public static IKeyExchangeProvider getKeyExchangeProvider() {
        return keyExchangeProvider;
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

            // create sector manager
            sectorManager = new SectorManagerImpl();

            // Load plugin configuration
            this.pluginConfiguration = new PluginConfiguration(this, this.configurationFile);
            this.pluginConfiguration.loadConfiguration();

            // Load messages configuration
            this.messagesConfiguration = new MessagesConfiguration(this, this.messagesFile);
            this.messagesConfiguration.loadConfiguration();

            // create metrics provider
            if(this.pluginConfiguration.metrics) this.metrics = new Metrics(this, 13144);

            // create encryption provider implementation
            if (this.pluginConfiguration.encryptTraffic) {

                // create key exchange provider
                keyExchangeProvider = new DefaultDiffieHellmanProvider(1024);

                // test
                try {

                    // test key exchange provider
                    assert keyExchangeProvider.generateKey(new BigInteger(1024, new SecureRandom())) != null;

                } catch (Throwable e) {

                    // log
                    this.getLogger().log(Level.SEVERE, "Failed to setup encryption provider. Stopping proxy...");

                    // stop proxy
                    this.getProxy().stop();

                }

            }

            // create master server implementation
            masterServer = new DefaultSectorServer(new AbstractMasterServerListener(this));

            // register required prototypes listeners
            masterServer.getPrototypeManager().registerPrototype(ConfigurationPacket.Request.class);
            masterServer.getPrototypeManager().registerPrototype(ServerPacket.ChangeServerPacket.class);
            masterServer.getPrototypeManager().registerListener(new EncryptionServerHelloPacket());
            masterServer.getPrototypeManager().registerListener(new EncryptionFinishPacket(this));
            masterServer.getPrototypeManager().registerListener(new ConfigurationRequestPacket(this));
            masterServer.getPrototypeManager().registerListener(new ChangeServerPacket());

            // bind over tcp
            masterServer.bind(new InternalServerImpl(this.pluginConfiguration.masterHostname, this.pluginConfiguration.masterPort));

        } catch (Throwable e) {

            // log
            this.getLogger().log(Level.SEVERE, "An error occurred during enabling plugin.", e);

            // stop proxy
            ProxyServer.getInstance().stop();

        }

    }
}
