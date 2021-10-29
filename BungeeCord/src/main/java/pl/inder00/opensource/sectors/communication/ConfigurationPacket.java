package pl.inder00.opensource.sectors.communication;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.protobuf.ProtobufConfigurationData;
import pl.inder00.opensource.sectors.protobuf.ProtobufGeneric;
import pl.inder00.opensource.sectors.protocol.buffer.ByteBufExtension;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

public class ConfigurationPacket implements IPacket<ProtobufConfigurationData.ConfiguractionPacket> {

    /**
     * Data
     */
    private Sectors plugin;

    /**
     * Implementation
     */
    public ConfigurationPacket(Sectors plugin) {
        this.plugin = plugin;
    }

    @Override
    public ProtobufConfigurationData.ConfiguractionPacket.Builder getBuilder() {
        return ProtobufConfigurationData.ConfiguractionPacket.newBuilder();
    }

    @Override
    public ProtobufConfigurationData.ConfiguractionPacket execute(ProtobufConfigurationData.ConfiguractionPacket configuractionPacket) throws Throwable {

        // builder
        ProtobufConfigurationData.ConfiguractionPacket.Builder configurationPacketOutput = ProtobufConfigurationData.ConfiguractionPacket.newBuilder();

        // write data
        configurationPacketOutput.setVersion(this.plugin.getDescription().getVersion());
        configurationPacketOutput.setProtectionDistance(this.plugin.pluginConfiguration.protectionDistance);
        configurationPacketOutput.setChangeSectorCooldown(this.plugin.pluginConfiguration.sectorChangeCooldown);
        configurationPacketOutput.setDefaultLanguage(this.plugin.messagesConfiguration.defaultLocale);

        // aliases configuration
        this.plugin.messagesConfiguration.localeAliases.forEach((key, val) -> {
            configurationPacketOutput.addAlises(ProtobufConfigurationData.ConfigurationAlias.newBuilder()
                    .setTarget(key)
                    .setSource(val)
                    .build());
        });

        // messages configuration
        this.plugin.messagesConfiguration.messagesList.forEach((key, val) -> {
            configurationPacketOutput.addMessages(ProtobufConfigurationData.ConfigurationMessage.newBuilder()
                    .setKey(key)
                    .setValue(val)
                    .build());
        });

        // sectors
        SectorManager.getSectorsList().forEach(sector -> {

            // write data
            configurationPacketOutput.addSectors(ProtobufGeneric.ProtoSector.newBuilder()
                            .setUniqueId(ProtobufGeneric.ProtoUUID.newBuilder()
                                    .setMostSig(sector.getUniqueId().getMostSignificantBits())
                                    .setLeastSig(sector.getUniqueId().getLeastSignificantBits())
                                    .build())
                            .setInternalHostname(sector.getInternalServerHostname())
                            .setInternalPort(sector.getInternalServerPort())
                            .setWorldName(sector.getWorld())
                            .setMinX(sector.getMinX())
                            .setMinZ(sector.getMinZ())
                            .setMaxX(sector.getMaxX())
                            .setMaxZ(sector.getMaxZ())
                    .build());

        });

        // return null
        return configurationPacketOutput.build();

    }

}
