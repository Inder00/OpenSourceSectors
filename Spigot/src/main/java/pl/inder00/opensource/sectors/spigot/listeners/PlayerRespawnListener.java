package pl.inder00.opensource.sectors.spigot.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.basic.ISectorUser;

public class PlayerRespawnListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {

        // values
        Player player = event.getPlayer();
        Location respawnLocation = event.getRespawnLocation();

        // check respawn location is not in current sector
        if (Sectors.getSectorManager().getCurrentSector() != null && !Sectors.getSectorManager().getCurrentSector().isInLocation(respawnLocation)) {

            // sector user
            ISectorUser sectorUser = Sectors.getUserManager().getByKey(player.getUniqueId());

            // get sector at respawn location
            ISector targetSector = Sectors.getSectorManager().getAtLocation(respawnLocation);
            if (targetSector != null && sectorUser.getTargetSector() == null) {

                // send player to target sector
                sectorUser.send(targetSector, respawnLocation);

            }

        }

    }

}
