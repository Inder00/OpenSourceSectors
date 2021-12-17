package pl.inder00.opensource.sectors.spigot.communication.internal.player;

import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.protobuf.PositionPacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.impl.PositionDataImpl;

public class PlayerPositionPacket implements IPrototypeListener<PositionPacket.PlayerPositionPacket> {

    @Override
    public void onReceivedData(ISectorConnection connection, PositionPacket.PlayerPositionPacket message) throws Exception {

        // insert data into cache
        IProtobufData<PositionPacket.PlayerPositionPacket, Player> positionData = new PositionDataImpl(message);
        Sectors.getPositionDataManager().save(positionData, ProtocolSerializationUtils.deserialize(positionData.getData().getPlayerUniqueId()));

    }
}
