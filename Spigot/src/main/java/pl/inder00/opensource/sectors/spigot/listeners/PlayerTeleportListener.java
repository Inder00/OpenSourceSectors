package pl.inder00.opensource.sectors.spigot.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.basic.ISectorUser;

public class PlayerTeleportListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {

        // check event is cancelled
        if (event.isCancelled()) return;

        // values
        Player player = event.getPlayer();
        Location teleportLocation = event.getTo();

        // check teleport location is not in current sector
        if (Sectors.getSectorManager().getCurrentSector() != null && !Sectors.getSectorManager().getCurrentSector().isInLocation(teleportLocation)) {

            // sector user
            ISectorUser sectorUser = Sectors.getUserManager().getByKey(player.getUniqueId());

            // get sector at teleport location
            ISector targetSector = Sectors.getSectorManager().getAtLocation(teleportLocation);
            if (targetSector != null && sectorUser.getTargetSector() == null) {

                // send player to target sector
                sectorUser.send(targetSector, teleportLocation);

            }

        }
    }

}
