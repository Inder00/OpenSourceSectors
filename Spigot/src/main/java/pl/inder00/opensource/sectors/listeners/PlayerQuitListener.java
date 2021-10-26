package pl.inder00.opensource.sectors.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.ISectorUser;
import pl.inder00.opensource.sectors.basic.manager.SectorUserManager;
import pl.inder00.opensource.sectors.utils.SpigotPayloadUtils;

public class PlayerQuitListener implements Listener {

    @EventHandler( priority = EventPriority.MONITOR )
    public void onQuit(PlayerQuitEvent event){

        // values
        Player player = event.getPlayer();

        // remove join time time
        SectorUserManager.deleteJoinTimeUser( player.getUniqueId() );

        // sector user
        ISectorUser sectorUser = SectorUserManager.getUserByPlayerUniqueIdIfPresent( player.getUniqueId() );
        if(sectorUser != null){

            // check does player is changing their sector
            ISector targetSector = sectorUser.getTargetSector();
            if(targetSector != null){

                // send transfer data to target server
                targetSector.getEndpoint().getRSocket().fireAndForget(SpigotPayloadUtils.createTransferDataPayload(player,sectorUser.getTargetLocation())).subscribe();

            }

            // clear sector user
            SectorUserManager.deleteSectorUser(sectorUser);

        }

    }

}
