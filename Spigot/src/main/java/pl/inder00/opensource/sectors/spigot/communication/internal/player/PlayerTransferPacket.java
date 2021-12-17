package pl.inder00.opensource.sectors.spigot.communication.internal.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.ISectorConnection;
import pl.inder00.opensource.sectors.protocol.protobuf.TransferPacket;
import pl.inder00.opensource.sectors.protocol.prototype.IPrototypeListener;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.impl.TransferDataImpl;

public class PlayerTransferPacket implements IPrototypeListener<TransferPacket.PlayerTransferPacket> {

    /**
     * Data
     */
    private JavaPlugin plugin;

    /**
     * Implementation
     */
    public PlayerTransferPacket(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceivedData(ISectorConnection connection, TransferPacket.PlayerTransferPacket message) throws Exception {

        // insert data into cache
        IProtobufData<TransferPacket.PlayerTransferPacket, Player> transferData = new TransferDataImpl(message);
        Sectors.getTransferDataManager().save(transferData, ProtocolSerializationUtils.deserialize(transferData.getData().getPlayerUniqueId()));

        // check does target player is online
        Player targetPlayer = Bukkit.getPlayer(ProtocolSerializationUtils.deserialize(message.getPlayerUniqueId()));
        if (targetPlayer != null) {

            // apply transfer data into player
            Bukkit.getServer().getScheduler().runTask(this.plugin, () -> transferData.execute(targetPlayer));

        }

    }
}
