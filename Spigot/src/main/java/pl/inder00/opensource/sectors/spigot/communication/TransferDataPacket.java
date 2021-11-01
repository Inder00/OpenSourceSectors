package pl.inder00.opensource.sectors.spigot.communication;

import com.google.protobuf.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.commons.serialization.CommonsSerializationUtils;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.impl.TransferDataImpl;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;
import pl.inder00.opensource.sectors.spigot.utils.SpigotSerializationUtils;

public class TransferDataPacket implements IPacket<ProtobufTransferData.TransferPacket> {

    /**
     * Data
     */
    private final Sectors sectors;

    /**
     * Implementation
     */
    public TransferDataPacket(Sectors sectors) {
        this.sectors = sectors;
    }

    @Override
    public ProtobufTransferData.TransferPacket.Builder getBuilder() {
        return ProtobufTransferData.TransferPacket.newBuilder();
    }

    @Override
    public <Y extends Message> Y execute(ProtobufTransferData.TransferPacket transferPacket) throws Throwable {

        // insert data into cache
        IProtobufData<ProtobufTransferData.TransferPacket, Player> transferData = new TransferDataImpl(this.sectors, transferPacket);
        Sectors.getTransferDataManager().save(transferData, ProtocolSerializationUtils.deserialize(transferData.getData().getPlayerUniqueId()));

        // check does target player is online
        Player targetPlayer = Bukkit.getPlayer(ProtocolSerializationUtils.deserialize(transferPacket.getPlayerUniqueId()));
        if (targetPlayer != null && targetPlayer.isOnline()) {

            // apply transfer data into player
            Bukkit.getServer().getScheduler().runTask(this.sectors, () -> transferData.execute(targetPlayer));

        }

        // return null
        return null;

    }

}
