package pl.inder00.opensource.sectors.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.ISectorUser;
import pl.inder00.opensource.sectors.basic.manager.SectorUserManager;
import pl.inder00.opensource.sectors.utils.ActionbarUtils;

public class PlayerMoveListener implements Listener {

    /**
     * Main class
     */
    private final Sectors plugin;

    /**
     * Implementation
     */
    public PlayerMoveListener(Sectors plugin) {
        this.plugin = plugin;
    }

    @EventHandler ( priority = EventPriority.MONITOR )
    public void onMove(PlayerMoveEvent event) {

        // event checks
        if (event.isCancelled()) return;

        // event values
        Player player = event.getPlayer();
        Location locationFrom = event.getFrom();
        Location locationTo = event.getTo();

        // check does player moved
        if (locationFrom.getBlockX() == locationTo.getBlockX() && locationFrom.getBlockZ() == locationTo.getBlockZ())
            return;

        // check does current sector exists
        ISector currentSector = plugin.getSectorManager().getCurrentSector();
        if (currentSector != null && currentSector.getWorld() == locationTo.getWorld()) {

            // check is player inside protected area
            if (!(locationTo.getX() >= currentSector.getMinX() + currentSector.getProtectionDistance() && locationTo.getX() <= currentSector.getMaxX() - currentSector.getProtectionDistance() && locationTo.getZ() >= currentSector.getMinZ() + currentSector.getProtectionDistance() && locationTo.getZ() <= currentSector.getMaxZ() - currentSector.getProtectionDistance())) {

                // sector user
                ISectorUser sectorUser = plugin.getSectorUserManager().getUserByPlayerUniqueId(player.getUniqueId());

                // distance to the sector
                int distanceToSector = currentSector.getDistanceToBorder(locationTo);

                // send actionbar
                ActionbarUtils.sendActionbar(player, ChatColor.translateAlternateColorCodes('&', String.format(this.plugin.languageProvider.getLocalizedMessage(sectorUser.getLocale(), "border.distance"), distanceToSector)));

                // check is player outside sector area
                if (!(locationTo.getX() >= currentSector.getMinX() - 2 && locationTo.getX() <= currentSector.getMaxX() + 2 && locationTo.getZ() >= currentSector.getMinZ() - 2 && locationTo.getZ() <= currentSector.getMaxZ() + 2)) {

                    // get sector at location
                    ISector targetSector = plugin.getSectorManager().getSectorAtLocation(locationTo);
                    if (targetSector != null && targetSector != currentSector) {

                        // check does target server is online
                        if (sectorUser.getTargetSector() == null && targetSector.getEndpoint() != null && targetSector.getEndpoint().getRSocket() != null && !targetSector.getEndpoint().getRSocket().isDisposed()) {

                            // send player to target sector
                            targetSector.send(this.plugin.masterServer, sectorUser, player);

                        } else if (sectorUser.getTargetSector() == null) {

                            // send message
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.languageProvider.getLocalizedMessage(sectorUser.getLocale(), "sector.offline")));

                        }

                    }

                }

            }

        }

    }

}
