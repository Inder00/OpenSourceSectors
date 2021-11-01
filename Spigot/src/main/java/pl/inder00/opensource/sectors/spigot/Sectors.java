package pl.inder00.opensource.sectors.spigot;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.commons.managers.IManager;
import pl.inder00.opensource.sectors.spigot.communication.PositionDataPacket;
import pl.inder00.opensource.sectors.spigot.communication.TransferDataPacket;
import pl.inder00.opensource.sectors.spigot.configuration.PluginConfiguration;
import pl.inder00.opensource.sectors.spigot.listeners.*;
import pl.inder00.opensource.sectors.spigot.basic.ISectorManager;
import pl.inder00.opensource.sectors.spigot.basic.ISectorUser;
import pl.inder00.opensource.sectors.spigot.basic.manager.PositionDataManagerImpl;
import pl.inder00.opensource.sectors.spigot.basic.manager.SectorManagerImpl;
import pl.inder00.opensource.sectors.spigot.basic.manager.SectorUserManagerImpl;
import pl.inder00.opensource.sectors.spigot.basic.manager.TransferDataManagerImpl;
import pl.inder00.opensource.sectors.spigot.i18n.I18n;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.spigot.protocol.MasterServerClient;
import pl.inder00.opensource.sectors.protocol.packet.EPacket;
import pl.inder00.opensource.sectors.protocol.packet.PacketManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Sectors extends JavaPlugin {

    /**
     * Language provider
     */
    private static I18n languageProvider;

    /**
     * Master server
     */
    private static MasterServerClient masterServer;
    /**
     * Managers
     */
    private static ISectorManager sectorManager;
    private static IManager<ISectorUser, UUID> userManager;
    private static IManager<IProtobufData<ProtobufPositionData.PositionPacket, Player>, UUID> positionDataManager;
    private static IManager<IProtobufData<ProtobufTransferData.TransferPacket, Player>, UUID> transferDataManager;

    /**
     * Configuration
     */
    private File configurationFile;
    private PluginConfiguration pluginConfiguration;

    @Override
    public void onEnable() {

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

        // Connect to master server and send request for configuration
        masterServer = new MasterServerClient(this, sectorManager.getCurrentSectorUniqueId(), this.pluginConfiguration.masterHostname, this.pluginConfiguration.masterPort, this.pluginConfiguration.masterPassword != null ? (this.pluginConfiguration.masterPassword.length() > 0 ? this.pluginConfiguration.masterPassword : null) : null);
        masterServer.connectToMasterServer();

        // register endpoints packets
        PacketManager.registerPacket(EPacket.DATA_EXCHANGE, new TransferDataPacket(this));
        PacketManager.registerPacket(EPacket.POSITION_DATA_EXCHANGE, new PositionDataPacket(this));

        // block server until load all sectors and connects to them
        boolean serverReady = false;
        while(!serverReady){
            serverReady = sectorManager.getDataCollection().stream().allMatch(check -> check.getEndpoint() != null && check.getEndpoint().getRSocket().isDisposed());
            try {
                Thread.sleep(50L);
            } catch (Throwable ignored) {}
        }

        // register plugin listeners
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
     * Returns I18n language provider
     *
     * @return I18n
     */
    public static I18n getLanguageProvider() {
        return languageProvider;
    }

    /**
     * Returns socket connections to master server
     *
     * @return MasterServerClient
     */
    public static ISectorClient getMasterServer() {
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
     * @return IManager<IProtobufData<ProtobufPositionData.PositionPacket, Player>, UUID>
     */
    public static IManager<IProtobufData<ProtobufPositionData.PositionPacket, Player>, UUID> getPositionDataManager() {
        return positionDataManager;
    }

    /**
     * Returns transfer data manager
     *
     * @return IManager<IProtobufData<ProtobufTransferData.TransferPacket, Player>, UUID>
     */
    public static IManager<IProtobufData<ProtobufTransferData.TransferPacket, Player>, UUID> getTransferDataManager() {
        return transferDataManager;
    }

}
