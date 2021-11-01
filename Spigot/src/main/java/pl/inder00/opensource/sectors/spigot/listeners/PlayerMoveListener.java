package pl.inder00.opensource.sectors.spigot.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.basic.ISectorUser;
import pl.inder00.opensource.sectors.spigot.utils.ActionbarUtils;

public class PlayerMoveListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
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
        ISector currentSector = Sectors.getSectorManager().getCurrentSector();
        if (currentSector != null && currentSector.getWorld() == locationTo.getWorld()) {

            // check is player inside protected area
            if (!(locationTo.getX() >= currentSector.getMinX() + currentSector.getProtectionDistance() && locationTo.getX() <= currentSector.getMaxX() - currentSector.getProtectionDistance() && locationTo.getZ() >= currentSector.getMinZ() + currentSector.getProtectionDistance() && locationTo.getZ() <= currentSector.getMaxZ() - currentSector.getProtectionDistance())) {

                // sector user
                ISectorUser sectorUser = Sectors.getUserManager().getByKey(player.getUniqueId());

                // distance to the sector
                int distanceToSector = currentSector.getDistanceToBorder(locationTo);

                // send actionbar
                ActionbarUtils.sendActionbar(player, ChatColor.translateAlternateColorCodes('&', String.format(Sectors.getLanguageProvider().getLocalizedMessage(sectorUser.getLocale(), "border.distance"), distanceToSector)));

                // check is player outside sector area
                if (!(locationTo.getX() >= currentSector.getMinX() - 2 && locationTo.getX() <= currentSector.getMaxX() + 2 && locationTo.getZ() >= currentSector.getMinZ() - 2 && locationTo.getZ() <= currentSector.getMaxZ() + 2)) {

                    // get sector at location
                    ISector targetSector = Sectors.getSectorManager().getAtLocation(locationTo);
                    if (targetSector != null && targetSector != currentSector) {

                        // check does target server is online
                        if (sectorUser.getTargetSector() == null && targetSector.getEndpoint() != null && targetSector.getEndpoint().getRSocket() != null && !targetSector.getEndpoint().getRSocket().isDisposed()) {

                            // send player to target sector
                            targetSector.send(Sectors.getMasterServer(), sectorUser, player);

                        } else if (sectorUser.getTargetSector() == null) {

                            // send message
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Sectors.getLanguageProvider().getLocalizedMessage(sectorUser.getLocale(), "sector.offline")));

                        }

                    }

                }

            }

        }

    }

}
