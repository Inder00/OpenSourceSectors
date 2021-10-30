package pl.inder00.opensource.sectors;

import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.basic.manager.PositionDataManager;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.basic.manager.SectorUserManager;
import pl.inder00.opensource.sectors.basic.manager.TransferDataManager;
import pl.inder00.opensource.sectors.communication.PositionDataPacket;
import pl.inder00.opensource.sectors.communication.TransferDataPacket;
import pl.inder00.opensource.sectors.configuration.PluginConfiguration;
import pl.inder00.opensource.sectors.i18n.I18n;
import pl.inder00.opensource.sectors.listeners.*;
import pl.inder00.opensource.sectors.protocol.MasterServerClient;
import pl.inder00.opensource.sectors.protocol.packet.EPacket;
import pl.inder00.opensource.sectors.protocol.packet.PacketManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Sectors extends JavaPlugin {

    /**
     * Language provider
     */
    public I18n languageProvider;
    /**
     * Master server
     */
    public MasterServerClient masterServer;
    /**
     * Manager of sectors
     */
    private SectorManager sectorManager;
    /**
     * Manager of sector user
     */
    private SectorUserManager sectorUserManager;
    /**
     * Manager of player position data
     */
    private PositionDataManager positionDataManager;
    /**
     * Manager of player transfer data
     */
    private TransferDataManager transferDataManager;
    /**
     * Configuration file
     */
    private File configurationFile;
    private PluginConfiguration pluginConfiguration;

    public SectorManager getSectorManager() {
        return sectorManager;
    }

    public SectorUserManager getSectorUserManager() {
        return sectorUserManager;
    }

    public PositionDataManager getPositionDataManager() {
        return positionDataManager;
    }

    public TransferDataManager getTransferDataManager() {
        return transferDataManager;
    }

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

        // Load sector manager
        this.sectorManager = new SectorManager();

        // Load sector user manager
        this.sectorUserManager = new SectorUserManager();

        // Load player position data manager
        this.positionDataManager = new PositionDataManager();

        // Load player transfer data manager
        this.transferDataManager = new TransferDataManager();

        // Update current sector unique id
        this.sectorManager.setCurrentSectorUniqueId(UUID.nameUUIDFromBytes(this.pluginConfiguration.sectorId.getBytes(StandardCharsets.UTF_8)));

        // Connect to master server and send request for configuration
        this.masterServer = new MasterServerClient(this, this.pluginConfiguration.masterHostname, this.pluginConfiguration.masterPort, this.pluginConfiguration.masterPassword != null ? (this.pluginConfiguration.masterPassword.length() > 0 ? this.pluginConfiguration.masterPassword : null) : null);
        this.masterServer.connectToMasterServer();

        // register endpoints packets
        PacketManager.registerPacket(EPacket.DATA_EXCHANGE, new TransferDataPacket(this));
        PacketManager.registerPacket(EPacket.POSITION_DATA_EXCHANGE, new PositionDataPacket(this));

        // register plugin listeners
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerTeleportListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockFlowListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockIgniteListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityExplodeListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockPhysicsListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityChangeBlockListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerBucketEmptyListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerBucketFillListener(this), this);

    }

    @Override
    public void onDisable() {

    }
}
