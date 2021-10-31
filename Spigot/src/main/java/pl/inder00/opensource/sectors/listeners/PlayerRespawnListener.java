package pl.inder00.opensource.sectors.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.ISectorUser;

public class PlayerRespawnListener extends AbstractListener {

    /**
     * Implementation
     * @param sectors
     */
    public PlayerRespawnListener(Sectors sectors) {
        super(sectors);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {

        // values
        Player player = event.getPlayer();
        Location respawnLocation = event.getRespawnLocation();

        // check respawn location is not in current sector
        if (this.sectors.sectorManager.getCurrentSector() != null && !this.sectors.sectorManager.getCurrentSector().isInLocation(respawnLocation)) {

            // sector user
            ISectorUser sectorUser = this.sectors.userManager.getByKey(player.getUniqueId());

            // get sector at respawn location
            ISector targetSector = this.sectors.sectorManager.getAtLocation(respawnLocation);
            if (targetSector != null && sectorUser.getTargetSector() == null) {

                // send player to target sector
                targetSector.send(this.sectors.masterServer, sectorUser, player, respawnLocation);

            }

        }

    }

}
