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
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.basic.manager.SectorUserManager;

public class PlayerRespawnListener implements Listener {

    /**
     * Main class
     */
    private Sectors sectors;

    /**
     * Implementation
     */
    public PlayerRespawnListener(Sectors sectors) {
        this.sectors = sectors;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event){

        // values
        Player player = event.getPlayer();
        Location respawnLocation = event.getRespawnLocation();

        // check respawn location is not in current sector
        if( SectorManager.getCurrentSector() != null && !SectorManager.getCurrentSector().isInLocation(respawnLocation)){

            // sector user
            ISectorUser sectorUser = SectorUserManager.getUserByPlayerUniqueId( player.getUniqueId() );

            // get sector at respawn location
            ISector targetSector = SectorManager.getSectorAtLocation( respawnLocation );
            if(targetSector != null && sectorUser.getTargetSector() == null){

                // send player to target sector
                targetSector.send(this.sectors.masterServer, sectorUser, player, respawnLocation);

            }

        }

    }

}
