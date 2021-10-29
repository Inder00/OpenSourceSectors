package pl.inder00.opensource.sectors.communication;

import com.google.protobuf.Message;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.basic.impl.PositionDataImpl;
import pl.inder00.opensource.sectors.basic.manager.PositionDataManager;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;

public class PositionDataPacket implements IPacket<ProtobufPositionData.PositionPacket> {

    /**
     * Data
     */
    private JavaPlugin plugin;

    /**
     * Implementation
     */
    public PositionDataPacket(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ProtobufPositionData.PositionPacket.Builder getBuilder() {
        return ProtobufPositionData.PositionPacket.newBuilder();
    }

    @Override
    public <Y extends Message> Y execute(ProtobufPositionData.PositionPacket positionPacket) throws Throwable {

        // insert data into cache
        IProtobufData<ProtobufPositionData.PositionPacket, Player> positionData = new PositionDataImpl(positionPacket);
        PositionDataManager.cachePositionData(positionData);

        // return null
        return null;

    }

}
