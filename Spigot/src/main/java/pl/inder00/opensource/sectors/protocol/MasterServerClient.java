package pl.inder00.opensource.sectors.protocol;

import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.impl.SectorImpl;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.i18n.I18nFactory;
import pl.inder00.opensource.sectors.protocol.buffer.ByteBufExtension;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorClient;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorServer;
import pl.inder00.opensource.sectors.protocol.packet.EPacket;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolPayloadUtils;

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
    public void connectToMasterServer(){
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
                                    ByteBuf responseData = response.data();

                                    // protection distance
                                    int protectionDistance = responseData.readInt();

                                    // change cooldown
                                    int sectorChangeCooldown = responseData.readInt();

                                    // default language
                                    String defaultLanguage = ByteBufExtension.readString(responseData, 255);
                                    this.plugin.languageProvider = new I18nFactory(defaultLanguage);

                                    // aliases
                                    int aliasesCount = responseData.readInt();
                                    for(int i=0;i<aliasesCount;i++){
                                        String targetLocalCode = ByteBufExtension.readString(responseData, 1024);
                                        String sourceLocalCode = ByteBufExtension.readString(responseData, 1024);
                                        this.plugin.languageProvider.addLocaleAlias( sourceLocalCode, targetLocalCode );
                                    }

                                    // message
                                    int messagesCount = responseData.readInt();
                                    for(int i=0;i<messagesCount;i++){
                                        String messageCode = ByteBufExtension.readString(responseData, 1024);
                                        String messageText = ByteBufExtension.readString(responseData, 1024);
                                        this.plugin.languageProvider.addLocalizedMessage(messageCode, messageText);
                                    }

                                    // sectors data
                                    int sectorsCount = responseData.readInt();
                                    for(int i=0;i<sectorsCount;i++){

                                        // sector data
                                        UUID uniqueId = UUID.fromString( ByteBufExtension.readString(responseData, 64) );
                                        String internalHostname = ByteBufExtension.readString(responseData, 255);
                                        int internalPort = responseData.readInt();
                                        String worldName = ByteBufExtension.readString(responseData, 255);
                                        int minX = responseData.readInt();
                                        int minZ = responseData.readInt();
                                        int maxX = responseData.readInt();
                                        int maxZ = responseData.readInt();

                                        // is current iterating sector is self sector?
                                        if( uniqueId.equals(SectorManager.getCurrentSectorUniqueId()) ){

                                            // world implementation
                                            World world = Bukkit.getWorld("world");
                                            if(world == null){

                                                // log
                                                this.plugin.getLogger().log(Level.SEVERE, String.format("Failed to successfully start plugin (World not found). Stopping server.", worldName));

                                                // stop
                                                this.plugin.getServer().shutdown();

                                            }

                                            // create socket implementation
                                            ISector sector = new SectorImpl(this.plugin,uniqueId,null,world,minX,minZ,maxX,maxZ,protectionDistance,sectorChangeCooldown);

                                            // create sector endpoint
                                            SectorManager.setCurrentSectorEndpoint(new DefaultSectorServer(internalHostname,internalPort,this.password));

                                            // add sector to manager
                                            SectorManager.addSectorToList( sector );

                                        } else {

                                            // create socket implementation
                                            ISector sector = new SectorImpl(this.plugin,uniqueId,new DefaultSectorClient(internalHostname,internalPort,this.password),null,minX - 3,minZ - 3,maxX + 3,maxZ + 3,protectionDistance,sectorChangeCooldown);

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
                                            SectorManager.addSectorToList( sector );

                                        }

                                    }

                                    // log
                                    this.plugin.getLogger().info(String.format("Loaded %d/%d sectors.", SectorManager.getSectorsCount(), sectorsCount));

                                    // check local sector is loaded
                                    if( SectorManager.getCurrentSector() != null ){

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
                                                    double sectorSize = Math.abs( currentSector.getMaxX() - currentSector.getMinX() );
                                                    double sectorCenterX = (currentSector.getMaxX() + currentSector.getMinX()) / 2;
                                                    double sectorCenterZ = (currentSector.getMaxZ() + currentSector.getMinZ()) / 2;

                                                    // set world border
                                                    WorldBorder worldBorder = currentSector.getWorld().getWorldBorder();
                                                    worldBorder.setCenter( sectorCenterX, sectorCenterZ );
                                                    worldBorder.setSize( sectorSize + 6 );
                                                    worldBorder.setDamageAmount( 0 );
                                                    worldBorder.setDamageBuffer( 0 );
                                                    worldBorder.setWarningDistance( 0 );

                                                })
                                                .subscribe();

                                    } else {


                                        // log
                                        this.plugin.getLogger().log(Level.SEVERE, String.format("Failed to successfully start plugin (Server sector configuration not found). Stopping server..."));

                                        // stop server
                                        this.plugin.getServer().shutdown();

                                    }

                                } catch (Throwable e){

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
