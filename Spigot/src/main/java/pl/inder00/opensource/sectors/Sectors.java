package pl.inder00.opensource.sectors;

import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.basic.IManager;
import pl.inder00.opensource.sectors.basic.ISectorManager;
import pl.inder00.opensource.sectors.basic.ISectorUser;
import pl.inder00.opensource.sectors.basic.manager.PositionDataManagerImpl;
import pl.inder00.opensource.sectors.basic.manager.SectorManagerImpl;
import pl.inder00.opensource.sectors.basic.manager.SectorUserManagerImpl;
import pl.inder00.opensource.sectors.basic.manager.TransferDataManagerImpl;
import pl.inder00.opensource.sectors.communication.PositionDataPacket;
import pl.inder00.opensource.sectors.communication.TransferDataPacket;
import pl.inder00.opensource.sectors.configuration.PluginConfiguration;
import pl.inder00.opensource.sectors.i18n.I18n;
import pl.inder00.opensource.sectors.listeners.*;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
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
     * Managers
     */
    public ISectorManager sectorManager;
    public IManager<IProtobufData<ProtobufPositionData.PositionPacket, Player>, UUID> positionDataManager;
    public IManager<IProtobufData<ProtobufTransferData.TransferPacket, Player>, UUID> transferDataManager;
    public IManager<ISectorUser, UUID> userManager;

    /**
     * Configuration
     */
    private File configurationFile;
    private PluginConfiguration pluginConfiguration;

    /**
     * bStats metrics
     */
    public Metrics metrics;

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
        this.sectorManager = new SectorManagerImpl(UUID.nameUUIDFromBytes(this.pluginConfiguration.sectorId.getBytes(StandardCharsets.UTF_8)));
        this.positionDataManager = new PositionDataManagerImpl();
        this.transferDataManager = new TransferDataManagerImpl();
        this.userManager = new SectorUserManagerImpl();

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

}
