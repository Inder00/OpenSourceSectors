package pl.inder00.opensource.sectors.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;

public class EntityExplodeListener implements Listener {

    /**
     * Main class
     */
    private final Sectors plugin;

    /**
     * Implementation
     */
    public EntityExplodeListener(Sectors plugin) {
        this.plugin = plugin;
    }

    @EventHandler ( priority = EventPriority.MONITOR )
    public void onExplode(EntityExplodeEvent e) {
        if (e.isCancelled()) return;

        // block location
        Location blockLocation = e.getLocation();

        // current sector
        ISector currentSector = plugin.getSectorManager().getCurrentSector();
        if (currentSector == null) return;

        // check is block inside protected area
        if (!(blockLocation.getX() >= currentSector.getMinX() + currentSector.getProtectionDistance() && blockLocation.getX() <= currentSector.getMaxX() - currentSector.getProtectionDistance() && blockLocation.getZ() >= currentSector.getMinZ() + currentSector.getProtectionDistance() && blockLocation.getZ() <= currentSector.getMaxZ() - currentSector.getProtectionDistance()))
            e.setCancelled(true);

    }

}
