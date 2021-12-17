package pl.inder00.opensource.sectors.bungeecord.communication.configuration;

import pl.inder00.opensource.sectors.bungeecord.Sectors;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.protobuf.ConfigurationPacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;

public class ConfigurationRequestPacket implements IPrototypeListener<ConfigurationPacket.Request> {

    /**
     * Data
     */
    private Sectors plugin;

    /**
     * Implementation
     */
    public ConfigurationRequestPacket(Sectors plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceivedData(ISectorConnection connection, ConfigurationPacket.Request message) throws Exception {

        // builder
        ConfigurationPacket.Response.Builder configurationPacketOutput = ConfigurationPacket.Response.newBuilder();

        // write data
        configurationPacketOutput.setProtectionDistance(this.plugin.pluginConfiguration.protectionDistance);
        configurationPacketOutput.setChangeSectorCooldown(this.plugin.pluginConfiguration.sectorChangeCooldown);
        configurationPacketOutput.setDefaultLanguage(this.plugin.messagesConfiguration.defaultLocale);

        // aliases configuration
        this.plugin.messagesConfiguration.localeAliases.forEach((key, val) -> {
            configurationPacketOutput.addAliases(ConfigurationPacket.ConfigurationAlias.newBuilder()
                    .setTarget(key)
                    .setSource(val)
                    .build());
        });

        // messages configuration
        this.plugin.messagesConfiguration.messagesList.forEach((key, val) -> {
            configurationPacketOutput.addMessages(ConfigurationPacket.ConfigurationMessage.newBuilder()
                    .setKey(key)
                    .setValue(val)
                    .build());
        });

        // sectors
        Sectors.getSectorManager().getDataCollection().forEach(sector -> {

            // write data
            configurationPacketOutput.addSectors(sector.getProtobufSector());

        });

        // send data to client
        connection.sendData(configurationPacketOutput.build());

    }
}
