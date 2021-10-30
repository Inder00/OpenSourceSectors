package pl.inder00.opensource.sectors.protocol;

import com.google.protobuf.ByteString;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.impl.SectorImpl;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.i18n.I18nFactory;
import pl.inder00.opensource.sectors.protobuf.ProtobufConfigurationData;
import pl.inder00.opensource.sectors.protobuf.ProtobufGeneric;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorClient;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorServer;
import pl.inder00.opensource.sectors.protocol.packet.EPacket;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolPayloadUtils;
import pl.inder00.opensource.sectors.utils.ProtobufUtils;

import java.util.UUID;
import java.util.logging.Level;

public class MasterServerClient extends DefaultSectorClient {

    /**
     * Data
     */
    private Sectors plugin;
    private String password;

    /**
     * Implementation
     */
    public MasterServerClient(Sectors plugin, String hostname, int port, String password) {
        super(hostname, port, password);
        this.plugin = plugin;
        this.password = password;
    }

    /**
     * Establish connection to master server and request configuration then process it
     */
    public void connectToMasterServer() {
        super.connect()
                .doOnError(error -> {

                    // log
                    this.plugin.getLogger().log(Level.SEVERE, "Failed connect to master server. Stopping server...");

                    // stop proxy
                    this.plugin.getServer().shutdown();

                })
                .doOnSuccess(success -> {

                    // log
                    this.plugin.getLogger().info("Successfully connected to master server.");

                    // request configuration
                    success.requestResponse(ProtocolPayloadUtils.createRequestPayload(EPacket.CONFIGURATION_REQUEST))
                            .doOnSuccess(response -> {

                                try {

                                    // parse response
                                    ProtobufConfigurationData.ConfigurationPacket configurationPacket = ProtobufConfigurationData.ConfigurationPacket.parseFrom(ByteString.copyFrom(response.getData()));

                                    // compare version
                                    if(!this.plugin.getDescription().getVersion().equals(configurationPacket.getVersion())){

                                        // log
                                        this.plugin.getLogger().log(Level.SEVERE, "Plugin version mismatch. Stopping server.");

                                        // stop
                                        this.plugin.getServer().shutdown();

                                    }

                                    // load sectors
                                    for(ProtobufGeneric.ProtoSector protoSector : configurationPacket.getSectorsList()){

                                        // sector uuid
                                        UUID uniqueId = ProtobufUtils.deserialize(protoSector.getUniqueId());

                                        // is current iterating sector is self sector?
                                        if (uniqueId.equals(SectorManager.getCurrentSectorUniqueId())) {

                                            // world implementation
                                            World world = Bukkit.getWorld(protoSector.hasWorldName() ? protoSector.getWorldName() : "world");
                                            if (world == null) {

                                                // log
                                                this.plugin.getLogger().log(Level.SEVERE, "Failed to successfully start plugin (World not found). Stopping server.");

                                                // stop
                                                this.plugin.getServer().shutdown();

                                            }

                                            // create socket implementation
                                            ISector sector = new SectorImpl(this.plugin, uniqueId, null, world, protoSector.getMinX(), protoSector.getMinZ(), protoSector.getMaxX(), protoSector.getMaxZ(), configurationPacket.getProtectionDistance(), configurationPacket.getChangeSectorCooldown());

                                            // create sector endpoint
                                            SectorManager.setCurrentSectorEndpoint(new DefaultSectorServer(protoSector.getInternalHostname(), protoSector.getInternalPort(), this.password));

                                            // add sector to manager
                                            SectorManager.addSectorToList(sector);

                                        } else {

                                            // create socket implementation
                                            ISector sector = new SectorImpl(this.plugin, uniqueId, new DefaultSectorClient(protoSector.getInternalHostname(), protoSector.getInternalPort(), this.password), null, protoSector.getMinX() - 3, protoSector.getMinZ() - 3, protoSector.getMaxX() + 3, protoSector.getMaxZ() + 3, configurationPacket.getProtectionDistance(), configurationPacket.getChangeSectorCooldown());

                                            // connect to sector endpoint
                                            sector.getEndpoint().connect()
                                                    .doOnError(sectorError -> {

                                                        // log
                                                        sector.getLogger().log(Level.SEVERE, String.format("Failed connect to sector endpoint (%s). Stopping server.", sectorError.getMessage()));

                                                        // stop
                                                        this.plugin.getServer().shutdown();

                                                    })
                                                    .doOnSuccess(sectorSuccess -> {

                                                        // log
                                                        sector.getLogger().info("Successfully connected to sector endpoint.");

                                                    })
                                                    .subscribe();

                                            // add sector to manager
                                            SectorManager.addSectorToList(sector);

                                        }

                                    }

                                    // default language
                                    this.plugin.languageProvider = new I18nFactory(configurationPacket.getDefaultLanguage());

                                    // aliases
                                    for(ProtobufConfigurationData.ConfigurationAlias langMap : configurationPacket.getAliasesList()){
                                        this.plugin.languageProvider.addLocaleAlias(langMap.getSource(), langMap.getTarget());
                                    }

                                    // message
                                    for(ProtobufConfigurationData.ConfigurationMessage msgMap : configurationPacket.getMessagesList()){
                                        this.plugin.languageProvider.addLocalizedMessage(msgMap.getKey(), msgMap.getValue());
                                    }

                                    // log
                                    this.plugin.getLogger().info(String.format("Loaded %d/%d sectors.", SectorManager.getSectorsCount(), configurationPacket.getSectorsCount()));

                                    // check local sector is loaded
                                    if (SectorManager.getCurrentSector() != null) {

                                        // current sector
                                        ISector currentSector = SectorManager.getCurrentSector();

                                        // bind internal sector endpoint
                                        SectorManager.getCurrentSectorEndpoint().bind()
                                                .doOnError(sectorError -> {

                                                    // log
                                                    currentSector.getLogger().log(Level.SEVERE, String.format("Failed to bind sector endpoint. Stopping server.", sectorError.getMessage()));

                                                    // stop
                                                    this.plugin.getServer().shutdown();

                                                })
                                                .doOnSuccess(sectorSuccess -> {

                                                    // log
                                                    currentSector.getLogger().info("Successfully bound sector endpoint.");

                                                    // calculate border values
                                                    double sectorSize = Math.abs(currentSector.getMaxX() - currentSector.getMinX());
                                                    double sectorCenterX = (currentSector.getMaxX() + currentSector.getMinX()) / 2;
                                                    double sectorCenterZ = (currentSector.getMaxZ() + currentSector.getMinZ()) / 2;

                                                    Bukkit.getServer().getScheduler().runTask(this.plugin, () -> {
                                                        // set world border
                                                        WorldBorder worldBorder = currentSector.getWorld().getWorldBorder();
                                                        worldBorder.setCenter(sectorCenterX, sectorCenterZ);
                                                        worldBorder.setSize(sectorSize + 6);
                                                        worldBorder.setDamageAmount(0);
                                                        worldBorder.setDamageBuffer(0);
                                                        worldBorder.setWarningDistance(0);
                                                    });

                                                })
                                                .subscribe();

                                    } else {


                                        // log
                                        this.plugin.getLogger().log(Level.SEVERE, String.format("Failed to successfully start plugin (Server sector configuration not found). Stopping server..."));

                                        // stop server
                                        this.plugin.getServer().shutdown();

                                    }

                                } catch (Throwable e) {

                                    // throw error
                                    e.printStackTrace();

                                    // log
                                    this.plugin.getLogger().log(Level.SEVERE, "Failed to process configuration response.", e);

                                    // stop server
                                    this.plugin.getServer().shutdown();

                                }

                            })
                            .doOnError(error -> {

                                // throw error
                                error.printStackTrace();

                                // log
                                this.plugin.getLogger().log(Level.SEVERE, String.format("Failed to gather sector configuration (%s). Stopping server...", error.getMessage()));

                                // stop server
                                this.plugin.getServer().shutdown();

                            })
                            .subscribe();

                }).subscribe();
    }
}
