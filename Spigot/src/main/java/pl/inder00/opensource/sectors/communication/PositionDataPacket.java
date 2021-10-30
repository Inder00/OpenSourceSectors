package pl.inder00.opensource.sectors.communication;

import com.google.protobuf.Message;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.impl.PositionDataImpl;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

public class PositionDataPacket implements IPacket<ProtobufPositionData.PositionPacket> {

    /**
     * Data
     */
    private final Sectors plugin;

    /**
     * Implementation
     */
    public PositionDataPacket(Sectors plugin) {
        this.plugin = plugin;
    }

    @Override
    public ProtobufPositionData.PositionPacket.Builder getBuilder() {
        return ProtobufPositionData.PositionPacket.newBuilder();
    }

    @Override
    public <Y extends Message> Y execute(ProtobufPositionData.PositionPacket positionPacket) throws Throwable {

        // insert data into cache
        IProtobufData<ProtobufPositionData.PositionPacket, Player> positionData = new PositionDataImpl(this.plugin, positionPacket);
        plugin.getPositionDataManager().cachePositionData(positionData);

        // return null
        return null;

    }

}
