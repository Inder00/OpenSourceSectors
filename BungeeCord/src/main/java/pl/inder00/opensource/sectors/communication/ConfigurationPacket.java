package pl.inder00.opensource.sectors.communication;

import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.protobuf.ProtobufConfigurationData;
import pl.inder00.opensource.sectors.protobuf.ProtobufGeneric;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

public class ConfigurationPacket implements IPacket<ProtobufGeneric.EmptyMessage> {

    /**
     * Data
     */
    private final Sectors sectors;

    /**
     * Implementation
     */
    public ConfigurationPacket(Sectors sectors) {
        this.sectors = sectors;
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
        configurationPacketOutput.setVersion(this.sectors.getDescription().getVersion());
        configurationPacketOutput.setProtectionDistance(this.sectors.pluginConfiguration.protectionDistance);
        configurationPacketOutput.setChangeSectorCooldown(this.sectors.pluginConfiguration.sectorChangeCooldown);
        configurationPacketOutput.setDefaultLanguage(this.sectors.messagesConfiguration.defaultLocale);

        // aliases configuration
        this.sectors.messagesConfiguration.localeAliases.forEach((key, val) -> {
            configurationPacketOutput.addAliases(ProtobufConfigurationData.ConfigurationAlias.newBuilder()
                    .setTarget(key)
                    .setSource(val)
                    .build());
        });

        // messages configuration
        this.sectors.messagesConfiguration.messagesList.forEach((key, val) -> {
            configurationPacketOutput.addMessages(ProtobufConfigurationData.ConfigurationMessage.newBuilder()
                    .setKey(key)
                    .setValue(val)
                    .build());
        });

        // sectors
        this.sectors.sectorManager.getDataCollection().forEach(sector -> {

            // write data
            configurationPacketOutput.addSectors(sector.getProtobufSector());

        });

        // return null
        return configurationPacketOutput.build();

    }

}
