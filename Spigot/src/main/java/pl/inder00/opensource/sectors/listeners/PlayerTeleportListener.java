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
import pl.inder00.opensource.sectors.basic.manager.SectorUserManager;

public class PlayerTeleportListener implements Listener {

    /**
     * Main class
     */
    private final Sectors plugin;

    /**
     * Implementation
     */
    public PlayerTeleportListener(Sectors plugin) {
        this.plugin = plugin;
    }

    @EventHandler ( priority = EventPriority.MONITOR )
    public void onTeleport(PlayerTeleportEvent event) {

        // check event is cancelled
        if (event.isCancelled()) return;

        // values
        Player player = event.getPlayer();
        Location teleportLocation = event.getTo();

        // check respawn location is not in current sector
        if (plugin.getSectorManager().getCurrentSector() != null && !plugin.getSectorManager().getCurrentSector().isInLocation(teleportLocation)) {

            // sector user
            ISectorUser sectorUser = plugin.getSectorUserManager().getUserByPlayerUniqueId(player.getUniqueId());

            // get sector at respawn location
            ISector targetSector = plugin.getSectorManager().getSectorAtLocation(teleportLocation);
            if (targetSector != null && sectorUser.getTargetSector() == null) {

                // send player to target sector
                targetSector.send(this.plugin.masterServer, sectorUser, player, teleportLocation);

            }

        }
    }

}
