package pl.inder00.opensource.sectors.communication;

import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.protobuf.ProtobufConfigurationData;
import pl.inder00.opensource.sectors.protobuf.ProtobufGeneric;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

public class ConfigurationPacket implements IPacket<ProtobufGeneric.EmptyMessage> {

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
    public ProtobufGeneric.EmptyMessage.Builder getBuilder() {
        return ProtobufGeneric.EmptyMessage.newBuilder();
    }

    @Override
    public ProtobufConfigurationData.ConfigurationPacket execute(ProtobufGeneric.EmptyMessage emptyMessage) throws Throwable {

        // builder
        ProtobufConfigurationData.ConfigurationPacket.Builder configurationPacketOutput = ProtobufConfigurationData.ConfigurationPacket.newBuilder();

        // write data
        configurationPacketOutput.setVersion(this.plugin.getDescription().getVersion());
        configurationPacketOutput.setProtectionDistance(this.plugin.pluginConfiguration.protectionDistance);
        configurationPacketOutput.setChangeSectorCooldown(this.plugin.pluginConfiguration.sectorChangeCooldown);
        configurationPacketOutput.setDefaultLanguage(this.plugin.messagesConfiguration.defaultLocale);

        // aliases configuration
        this.plugin.messagesConfiguration.localeAliases.forEach((key, val) -> {
            configurationPacketOutput.addAliases(ProtobufConfigurationData.ConfigurationAlias.newBuilder()
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
