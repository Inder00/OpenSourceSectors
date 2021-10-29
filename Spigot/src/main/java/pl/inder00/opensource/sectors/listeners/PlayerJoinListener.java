package pl.inder00.opensource.sectors.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.inder00.opensource.sectors.basic.manager.PositionDataManager;
import pl.inder00.opensource.sectors.basic.manager.SectorUserManager;
import pl.inder00.opensource.sectors.basic.manager.TransferDataManager;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        // event data
        Player player = event.getPlayer();

        // set player join time unix
        SectorUserManager.setUserJoinTime(player.getUniqueId(), System.currentTimeMillis());

        // check does position data exists
        IProtobufData<ProtobufPositionData.PositionPacket, Player> positionData = PositionDataManager.getPositionDataByPlayerUniqueId(player.getUniqueId());
        if (positionData != null) positionData.execute(player);

        // check does transfer data exists
        IProtobufData<ProtobufTransferData.TransferPacket, Player> transferData = TransferDataManager.getTransferDataByPlayerUniqueId(player.getUniqueId());
        if (transferData != null) transferData.execute(player);


    }

}
