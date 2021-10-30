package pl.inder00.opensource.sectors.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.ISectorUser;
import pl.inder00.opensource.sectors.basic.manager.SectorUserManager;

public class PlayerRespawnListener implements Listener {

    /**
     * Main class
     */
    private final Sectors plugin;

    /**
     * Implementation
     */
    public PlayerRespawnListener(Sectors plugin) {
        this.plugin = plugin;
    }

    @EventHandler ( priority = EventPriority.MONITOR )
    public void onRespawn(PlayerRespawnEvent event) {

        // values
        Player player = event.getPlayer();
        Location respawnLocation = event.getRespawnLocation();

        // check respawn location is not in current sector
        if (plugin.getSectorManager().getCurrentSector() != null && !plugin.getSectorManager().getCurrentSector().isInLocation(respawnLocation)) {

            // sector user
            ISectorUser sectorUser = plugin.getSectorUserManager().getUserByPlayerUniqueId(player.getUniqueId());

            // get sector at respawn location
            ISector targetSector = plugin.getSectorManager().getSectorAtLocation(respawnLocation);
            if (targetSector != null && sectorUser.getTargetSector() == null) {

                // send player to target sector
                targetSector.send(this.plugin.masterServer, sectorUser, player, respawnLocation);

            }

        }

    }

}
