package pl.inder00.opensource.sectors.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.ISectorUser;

public class PlayerTeleportListener extends AbstractListener {

    /**
     * Implementation
     * @param sectors
     */
    public PlayerTeleportListener(Sectors sectors) {
        super(sectors);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {

        // check event is cancelled
        if (event.isCancelled()) return;

        // values
        Player player = event.getPlayer();
        Location teleportLocation = event.getTo();

        // check teleport location is not in current sector
        if (this.sectors.sectorManager.getCurrentSector() != null && !this.sectors.sectorManager.getCurrentSector().isInLocation(teleportLocation)) {

            // sector user
            ISectorUser sectorUser = this.sectors.userManager.getByKey(player.getUniqueId());

            // get sector at teleport location
            ISector targetSector = this.sectors.sectorManager.getAtLocation(teleportLocation);
            if (targetSector != null && sectorUser.getTargetSector() == null) {

                // send player to target sector
                targetSector.send(this.sectors.masterServer, sectorUser, player, teleportLocation);

            }

        }
    }

}
