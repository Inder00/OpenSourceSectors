package pl.inder00.opensource.sectors.spigot;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.commons.basic.impl.InternalServerImpl;
import pl.inder00.opensource.sectors.commons.managers.IManager;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorClient;
import pl.inder00.opensource.sectors.protocol.protobuf.*;
import pl.inder00.opensource.sectors.spigot.basic.ISectorManager;
import pl.inder00.opensource.sectors.spigot.basic.ISectorUser;
import pl.inder00.opensource.sectors.spigot.basic.manager.PositionDataManagerImpl;
import pl.inder00.opensource.sectors.spigot.basic.manager.SectorManagerImpl;
import pl.inder00.opensource.sectors.spigot.basic.manager.SectorUserManagerImpl;
import pl.inder00.opensource.sectors.spigot.basic.manager.TransferDataManagerImpl;
import pl.inder00.opensource.sectors.spigot.communication.configuration.ConfigurationResponsePacket;
import pl.inder00.opensource.sectors.spigot.communication.encryption.EncryptionClientHelloPacket;
import pl.inder00.opensource.sectors.spigot.communication.encryption.EncryptionResponsePacket;
import pl.inder00.opensource.sectors.spigot.communication.handshake.ServerHandshakePacket;
import pl.inder00.opensource.sectors.spigot.configuration.PluginConfiguration;
import pl.inder00.opensource.sectors.spigot.i18n.I18n;
import pl.inder00.opensource.sectors.spigot.listeners.*;
import pl.inder00.opensource.sectors.spigot.protocol.DefaultMasterServerListener;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;

public class Sectors extends JavaPlugin {

    /**
     * Language provider
     */
    private static I18n languageProvider;

    /**
     * Master server
     */
    private static IInternalServer masterServerInternalServer;
    private static ISectorClient masterServer;

    /**
     * Internal server
     */
    private static ISectorServer internalServer;

    /**
     * Managers
     */
    private static ISectorManager sectorManager;
    private static IManager<ISectorUser, UUID> userManager;
    private static IManager<IProtobufData<PositionPacket.PlayerPositionPacket, Player>, UUID> positionDataManager;
    private static IManager<IProtobufData<TransferPacket.PlayerTransferPacket, Player>, UUID> transferDataManager;

    /**
     * Configuration
     */
    private File configurationFile;
    private PluginConfiguration pluginConfiguration;

    /**
     * Returns I18n language provider
     *
     * @return I18n
     */
    public static I18n getLanguageProvider() {
        return languageProvider;
    }

    /**
     * Sets I18n language provider
     *
     * @param languageProvider I18n
     */
    public static void setLanguageProvider(I18n languageProvider) {
        Sectors.languageProvider = languageProvider;
    }

    /**
     * Returns socket connection to master server
     *
     * @return ISectorClient
     */
    public static ISectorClient getMasterServer() {
        return masterServer;
    }

    /**
     * Returns master server internal server implementation
     *
     * @return IInternalServer
     */
    public static IInternalServer getMasterServerInternalServer(){
        return masterServerInternalServer;
    }

    /**
     * Returns internal server socket
     *
     * @return ISectorServer
     */
    public static ISectorServer getInternalServer() {
        return internalServer;
    }

    /**
     * Sets internal server socket
     *
     * @param server ISectorServer
     */
    public static void setInternalServer(ISectorServer server) {
        internalServer = server;
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
     * Returns user manager
     *
     * @return IManager<ISectorUser, UUID>
     */
    public static IManager<ISectorUser, UUID> getUserManager() {
        return userManager;
    }

    /**
     * Returns position data manager
     *
     * @return IManager<IProtobufData < PositionPacket.PlayerPositionPacket, Player>, UUID>
     */
    public static IManager<IProtobufData<PositionPacket.PlayerPositionPacket, Player>, UUID> getPositionDataManager() {
        return positionDataManager;
    }

    /**
     * Returns transfer data manager
     *
     * @return IManager<IProtobufData < ProtobufTransferData.TransferPacket, Player>, UUID>
     */
    public static IManager<IProtobufData<TransferPacket.PlayerTransferPacket, Player>, UUID> getTransferDataManager() {
        return transferDataManager;
    }

    @Override
    public void onEnable() {

        try {

            // Save default configuration locally
            this.configurationFile = new File(this.getDataFolder(), "configuration.yml");
            if (!this.configurationFile.exists()) {
                this.saveResource("configuration.yml", true);
            }

            // Load plugin configuration
            this.pluginConfiguration = new PluginConfiguration(this.configurationFile);
            this.pluginConfiguration.loadConfiguration();

            // Create managers
            sectorManager = new SectorManagerImpl(UUID.nameUUIDFromBytes(this.pluginConfiguration.sectorId.getBytes(StandardCharsets.UTF_8)));
            positionDataManager = new PositionDataManagerImpl();
            transferDataManager = new TransferDataManagerImpl();
            userManager = new SectorUserManagerImpl();

            // Create master server implementation
            masterServer = new DefaultSectorClient(new DefaultMasterServerListener(this));
            masterServerInternalServer = new InternalServerImpl(this.pluginConfiguration.masterHostname, this.pluginConfiguration.masterPort);

            // Register master server prototypes
            masterServer.getPrototypeManager().registerPrototype(ConfigurationPacket.Response.class);
            masterServer.getPrototypeManager().registerPrototype(HandshakePacket.ServerHandshake.class);
            masterServer.getPrototypeManager().registerListener(new EncryptionClientHelloPacket(masterServer));
            masterServer.getPrototypeManager().registerListener(new EncryptionResponsePacket(masterServer));
            masterServer.getPrototypeManager().registerListener(new ServerHandshakePacket(masterServer,this));
            masterServer.getPrototypeManager().registerListener(new ConfigurationResponsePacket(this));

            // Connect to master server
            masterServer.connect(masterServerInternalServer);

            // block server until load all sectors and connects to them
            boolean serverReady = false;
            while (!serverReady) {
                serverReady = sectorManager.getDataCollection().stream().allMatch(check -> check.getEndpoint() != null && check.getEndpoint().isConnected());
                try {
                    Thread.sleep(50L);
                } catch (Throwable ignored) {
                }
            }

            // register plugin listeners
            this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLoginListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerTeleportListener(), this);
            this.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
            this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
            this.getServer().getPluginManager().registerEvents(new BlockFlowListener(), this);
            this.getServer().getPluginManager().registerEvents(new BlockIgniteListener(), this);
            this.getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
            this.getServer().getPluginManager().registerEvents(new BlockPhysicsListener(), this);
            this.getServer().getPluginManager().registerEvents(new EntityChangeBlockListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerBucketEmptyListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerBucketFillListener(), this);

        } catch (Throwable e) {

            // log
            this.getLogger().log(Level.SEVERE, "Failed to start sectors plugin.", e);

            // stop server
            this.getServer().shutdown();

        }

    }

    @Override
    public void onDisable() {

        // stop internal server
        if (internalServer != null) internalServer.shutdown();

        // cleanup sectors
        sectorManager.getDataCollection().forEach(sector -> {

            // disconnect with endpoint
            sector.getEndpoint().disconnect();

            // remove from list
            Sectors.getSectorManager().delete(sector.getUniqueId());

        });

        // disconnect from master server
        masterServer.disconnect();

    }

}
