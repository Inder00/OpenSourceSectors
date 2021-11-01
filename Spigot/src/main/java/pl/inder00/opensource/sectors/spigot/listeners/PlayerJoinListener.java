package pl.inder00.opensource.sectors.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.impl.SectorUserImpl;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        // event data
        Player player = event.getPlayer();

        // allocate sector user to memory
        Sectors.getUserManager().save(new SectorUserImpl(player), player.getUniqueId());

        // check does position data exists
        IProtobufData<ProtobufPositionData.PositionPacket, Player> positionData = Sectors.getPositionDataManager().getByKey((player.getUniqueId()));
        if (positionData != null) positionData.execute(player);

        // check does transfer data exists
        IProtobufData<ProtobufTransferData.TransferPacket, Player> transferData = Sectors.getTransferDataManager().getByKey(player.getUniqueId());
        if (transferData != null) transferData.execute(player);


    }

}
