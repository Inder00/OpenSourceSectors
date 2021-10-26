package pl.inder00.opensource.sectors.communication;

import io.netty.buffer.ByteBuf;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.protocol.buffer.ByteBufExtension;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

public class ConfigurationPacket implements IPacket {

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
    public void execute(ByteBuf bufferIn, ByteBuf bufferOut) {

        // protection distance
        bufferOut.writeInt(this.plugin.pluginConfiguration.protectionDistance);

        // sector change cooldown
        bufferOut.writeInt(this.plugin.pluginConfiguration.sectorChangeCooldown);

        // messages configuration
        ByteBufExtension.writeString(bufferOut, this.plugin.messagesConfiguration.defaultLocale);
        bufferOut.writeInt(this.plugin.messagesConfiguration.localeAliases.size());
        this.plugin.messagesConfiguration.localeAliases.forEach(( key, val ) -> {
            ByteBufExtension.writeString(bufferOut, key);
            ByteBufExtension.writeString(bufferOut, val);
        });
        bufferOut.writeInt(this.plugin.messagesConfiguration.messagesList.size());
        this.plugin.messagesConfiguration.messagesList.forEach(( key, val ) -> {
            ByteBufExtension.writeString(bufferOut, key);
            ByteBufExtension.writeString(bufferOut, val);
        });

        // sectors count
        bufferOut.writeInt(SectorManager.getSectorsCount());

        // loop sectors
        SectorManager.getSectorsList().forEach(sector -> {

            // write data
            ByteBufExtension.writeString(bufferOut, sector.getUniqueId().toString());
            ByteBufExtension.writeString(bufferOut, sector.getInternalServerHostname());
            bufferOut.writeInt(sector.getInternalServerPort());
            ByteBufExtension.writeString(bufferOut, sector.getWorld());
            bufferOut.writeInt(sector.getMinX());
            bufferOut.writeInt(sector.getMinZ());
            bufferOut.writeInt(sector.getMaxX());
            bufferOut.writeInt(sector.getMaxZ());

        });

    }

}
