package pl.inder00.opensource.sectors.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.impl.SectorUserImpl;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;

public class PlayerJoinListener extends AbstractListener {

    /**
     * Implementation
     * @param sectors
     */
    public PlayerJoinListener(Sectors sectors) {
        super(sectors);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        // event data
        Player player = event.getPlayer();

        // allocate sector user to memory
        this.sectors.userManager.save(new SectorUserImpl(player), player.getUniqueId());

        // check does position data exists
        IProtobufData<ProtobufPositionData.PositionPacket, Player> positionData = this.sectors.positionDataManager.getByKey((player.getUniqueId()));
        if (positionData != null) positionData.execute(player);

        // check does transfer data exists
        IProtobufData<ProtobufTransferData.TransferPacket, Player> transferData = this.sectors.transferDataManager.getByKey(player.getUniqueId());
        if (transferData != null) transferData.execute(player);


    }

}
