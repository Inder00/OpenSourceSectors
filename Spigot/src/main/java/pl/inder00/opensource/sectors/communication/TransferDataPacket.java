package pl.inder00.opensource.sectors.communication;

import com.google.protobuf.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.impl.TransferDataImpl;
import pl.inder00.opensource.sectors.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;
import pl.inder00.opensource.sectors.utils.ProtobufUtils;

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
        this.sectors.transferDataManager.save(transferData, ProtobufUtils.deserialize(transferData.getData().getPlayerUniqueId()));

        // check does target player is online
        Player targetPlayer = Bukkit.getPlayer(ProtobufUtils.deserialize(transferPacket.getPlayerUniqueId()));
        if (targetPlayer != null && targetPlayer.isOnline()) {

            // apply transfer data into player
            Bukkit.getServer().getScheduler().runTask(this.sectors, () -> transferData.execute(targetPlayer));

        }

        // return null
        return null;

    }

}
