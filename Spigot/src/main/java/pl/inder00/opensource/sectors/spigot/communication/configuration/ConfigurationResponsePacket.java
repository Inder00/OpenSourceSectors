package pl.inder00.opensource.sectors.spigot.communication.configuration;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.commons.basic.impl.InternalServerImpl;
import pl.inder00.opensource.sectors.commons.encryption.IKeyExchangeProvider;
import pl.inder00.opensource.sectors.commons.encryption.impl.DefaultDiffieHellmanProvider;
import pl.inder00.opensource.sectors.protocol.ISectorServer;
import pl.inder00.opensource.sectors.protocol.exceptions.ProtocolException;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorClient;
import pl.inder00.opensource.sectors.protocol.impl.DefaultSectorServer;
import pl.inder00.opensource.sectors.protocol.protobuf.ConfigurationPacket;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufGeneric;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.basic.impl.SectorImpl;
import pl.inder00.opensource.sectors.spigot.communication.encryption.EncryptionClientHelloPacket;
import pl.inder00.opensource.sectors.spigot.communication.encryption.EncryptionResponsePacket;
import pl.inder00.opensource.sectors.spigot.communication.p2p.encryption.EncryptionFinishPacket;
import pl.inder00.opensource.sectors.spigot.communication.p2p.encryption.EncryptionServerHelloPacket;
import pl.inder00.opensource.sectors.spigot.communication.p2p.player.PlayerPositionPacket;
import pl.inder00.opensource.sectors.spigot.communication.p2p.player.PlayerTransferPacket;
import pl.inder00.opensource.sectors.spigot.i18n.I18nFactory;
import pl.inder00.opensource.sectors.spigot.protocol.DefaultInternalServerListener;
import pl.inder00.opensource.sectors.spigot.protocol.DefaultSectorEndpointListener;

import java.util.UUID;
import java.util.logging.Level;

public class ConfigurationResponsePacket implements IPrototypeListener<ConfigurationPacket.Response> {

    /**
     * Data
     */
    private JavaPlugin plugin;

    /**
     * Implementation
     */
    public ConfigurationResponsePacket(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceivedData(ConfigurationPacket.Response message) throws Exception {

        try {

            // cleanup sectors
            Sectors.getSectorManager().getDataCollection().forEach(sector -> {

                // disconnect with endpoint
                sector.getEndpoint().disconnect();

                // remove from list
                Sectors.getSectorManager().delete(sector.getUniqueId());

            });

            // load sectors
            for (ProtobufGeneric.ProtoSector protoSector : message.getSectorsList()) {

                // sector uuid
                UUID uniqueId = ProtocolSerializationUtils.deserialize(protoSector.getUniqueId());

                // is current iterating sector is self sector?
                if (uniqueId.equals(Sectors.getSectorManager().getCurrentSectorUniqueId())) {

                    // world implementation
                    World world = Bukkit.getWorld(protoSector.hasWorldName() ? protoSector.getWorldName() : "world");
                    if (world == null) {

                        // log
                        throw new ProtocolException("Failed to successfully start plugin (World not found). Stopping server.");

                    }

                    // create socket implementation
                    ISector sector = new SectorImpl(this.plugin, uniqueId, null, world, protoSector.getMinX(), protoSector.getMinZ(), protoSector.getMaxX(), protoSector.getMaxZ(), message.getProtectionDistance(), message.getChangeSectorCooldown());

                    // check does internal server exists
                    if (Sectors.getInternalServer() == null || !Sectors.getInternalServer().isActive()) {

                        // create internal server
                        IKeyExchangeProvider keyExchangeProvider = Sectors.getMasterServer().getEncryptionProvider().isEncryptionEnabled() ? new DefaultDiffieHellmanProvider(1024) : null;
                        ISectorServer internalServer = new DefaultSectorServer(new DefaultInternalServerListener(sector, this.plugin, keyExchangeProvider));

                        // register internal server prototypes
                        internalServer.getPrototypeManager().registerListener(new EncryptionServerHelloPacket(this.plugin, internalServer, keyExchangeProvider));
                        internalServer.getPrototypeManager().registerListener(new EncryptionFinishPacket(this.plugin, internalServer));
                        internalServer.getPrototypeManager().registerListener(new PlayerPositionPacket());
                        internalServer.getPrototypeManager().registerListener(new PlayerTransferPacket(this.plugin));

                        // bind internal server
                        internalServer.bind(new InternalServerImpl(protoSector.getInternalServer().getHostname(), protoSector.getInternalServer().getPort()));

                        // set internal server variable
                        Sectors.setInternalServer(internalServer);

                    }

                    // add sector to manager
                    Sectors.getSectorManager().save(sector, sector.getUniqueId());

                } else {

                    // create socket implementation
                    ISector sector = new SectorImpl(this.plugin, uniqueId, new DefaultSectorClient(new DefaultSectorEndpointListener(this.plugin)), null, protoSector.getMinX() - 3, protoSector.getMinZ() - 3, protoSector.getMaxX() + 3, protoSector.getMaxZ() + 3, message.getProtectionDistance(), message.getChangeSectorCooldown());

                    // register endpoints prototypes
                    sector.getEndpoint().getPrototypeManager().registerListener(new EncryptionClientHelloPacket(sector.getEndpoint()));
                    sector.getEndpoint().getPrototypeManager().registerListener(new EncryptionResponsePacket(sector.getEndpoint()));

                    // connect to sector endpoint
                    sector.getEndpoint().connect(new InternalServerImpl(protoSector.getInternalServer().getHostname(), protoSector.getInternalServer().getPort()));

                    // add sector to manager
                    Sectors.getSectorManager().save(sector, sector.getUniqueId());

                }

            }

            // default language
            Sectors.setLanguageProvider(new I18nFactory(message.getDefaultLanguage()));

            // aliases
            for (ConfigurationPacket.ConfigurationAlias langMap : message.getAliasesList()) {
                Sectors.getLanguageProvider().addLocaleAlias(langMap.getSource(), langMap.getTarget());
            }

            // message
            for (ConfigurationPacket.ConfigurationMessage msgMap : message.getMessagesList()) {
                Sectors.getLanguageProvider().addLocalizedMessage(msgMap.getKey(), msgMap.getValue());
            }

            // log
            this.plugin.getLogger().info(String.format("Loaded %d/%d sectors.", Sectors.getSectorManager().getDataCount(), message.getSectorsCount()));

            // check local sector is loaded
            if (Sectors.getSectorManager().getCurrentSector() != null) {

                // current sector
                ISector currentSector = Sectors.getSectorManager().getCurrentSector();

                // calculate border values
                double sectorSize = Math.abs(currentSector.getMaxX() - currentSector.getMinX());
                double sectorCenterX = (currentSector.getMaxX() + currentSector.getMinX()) / 2;
                double sectorCenterZ = (currentSector.getMaxZ() + currentSector.getMinZ()) / 2;

                // set world border
                Bukkit.getServer().getScheduler().runTask(this.plugin, () -> {
                    WorldBorder worldBorder = currentSector.getWorld().getWorldBorder();
                    worldBorder.setCenter(sectorCenterX, sectorCenterZ);
                    worldBorder.setSize(sectorSize + 6);
                    worldBorder.setDamageAmount(0);
                    worldBorder.setDamageBuffer(0);
                    worldBorder.setWarningDistance(0);
                });

            } else {

                // log
                throw new ProtocolException("Failed to successfully start plugin (Server sector configuration not found). Stopping server...");

            }

        } catch (Throwable e) {

            // log
            this.plugin.getLogger().log(Level.SEVERE, "Failed to process configuration response.", e);

            // stop server
            this.plugin.getServer().shutdown();

        }

    }
}
