package pl.inder00.opensource.sectors.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.basic.ISectorUser;
import pl.inder00.opensource.sectors.spigot.utils.SpigotPacketUtils;

public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {

        // values
        Player player = event.getPlayer();

        // sector user
        ISectorUser sectorUser = Sectors.getUserManager().getByKey(player.getUniqueId());
        if (sectorUser != null) {

            // check does player is changing their sector
            ISector targetSector = sectorUser.getTargetSector();
            if (targetSector != null && targetSector.getEndpoint() != null && targetSector.getEndpoint().isConnected()) {

                // send transfer data to target server
                targetSector.getEndpoint().sendData(SpigotPacketUtils.createPlayerTransferPacket(player, sectorUser.getTargetLocation()));

            }

        }

        // remove user from memory
        Sectors.getUserManager().delete(player.getUniqueId());

    }

}
