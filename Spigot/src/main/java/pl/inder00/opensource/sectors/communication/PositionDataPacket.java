package pl.inder00.opensource.sectors.communication;

import com.google.protobuf.Message;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.impl.PositionDataImpl;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;
import pl.inder00.opensource.sectors.utils.ProtobufUtils;

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
        this.sectors.positionDataManager.save(positionData, ProtobufUtils.deserialize(positionData.getData().getPlayerUniqueId()));

        // return null
        return null;

    }

}
