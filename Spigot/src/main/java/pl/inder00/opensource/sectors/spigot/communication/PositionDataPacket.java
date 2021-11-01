package pl.inder00.opensource.sectors.spigot.communication;

import com.google.protobuf.Message;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.impl.PositionDataImpl;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

public class PositionDataPacket implements IPacket<ProtobufPositionData.PositionPacket> {

    /**
     * Data
     */
    private final Sectors sectors;

    /**
     * Implementation
     */
    public PositionDataPacket(Sectors sectors) {
        this.sectors = sectors;
    }

    @Override
    public ProtobufPositionData.PositionPacket.Builder getBuilder() {
        return ProtobufPositionData.PositionPacket.newBuilder();
    }

    @Override
    public <Y extends Message> Y execute(ProtobufPositionData.PositionPacket positionPacket) throws Throwable {

        // insert data into cache
        IProtobufData<ProtobufPositionData.PositionPacket, Player> positionData = new PositionDataImpl(this.sectors, positionPacket);
        Sectors.getPositionDataManager().save(positionData, ProtocolSerializationUtils.deserialize(positionData.getData().getPlayerUniqueId()));

        // return null
        return null;

    }

}
