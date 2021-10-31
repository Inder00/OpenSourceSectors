package pl.inder00.opensource.sectors.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.ISectorUser;
import pl.inder00.opensource.sectors.utils.SpigotPayloadUtils;

public class PlayerQuitListener extends AbstractListener {

    /**
     * Implementation
     * @param sectors
     */
    public PlayerQuitListener(Sectors sectors) {
        super(sectors);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {

        // values
        Player player = event.getPlayer();

        // sector user
        ISectorUser sectorUser = this.sectors.userManager.getByKey(player.getUniqueId());
        if (sectorUser != null) {

            // check does player is changing their sector
            ISector targetSector = sectorUser.getTargetSector();
            if (targetSector != null) {

                // send transfer data to target server
                targetSector.getEndpoint().getRSocket().fireAndForget(SpigotPayloadUtils.createTransferDataPayload(player, sectorUser.getTargetLocation())).subscribe();

            }

        }

        // remove user from memory
        this.sectors.userManager.delete(player.getUniqueId());

    }

}
