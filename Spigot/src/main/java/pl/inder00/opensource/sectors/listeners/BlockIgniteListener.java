package pl.inder00.opensource.sectors.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockIgniteEvent;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;

public class BlockIgniteListener extends AbstractListener {

    /**
     * Implementation
     * @param sectors
     */
    public BlockIgniteListener(Sectors sectors) {
        super(sectors);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onIgnite(BlockIgniteEvent e) {
        if (e.isCancelled()) return;

        // block location
        Location blockLocation = e.getBlock().getLocation();

        // current sector
        ISector currentSector = this.sectors.sectorManager.getCurrentSector();
        if (currentSector == null) return;

        // check is block inside protected area
        if (!(blockLocation.getX() >= currentSector.getMinX() + currentSector.getProtectionDistance() && blockLocation.getX() <= currentSector.getMaxX() - currentSector.getProtectionDistance() && blockLocation.getZ() >= currentSector.getMinZ() + currentSector.getProtectionDistance() && blockLocation.getZ() <= currentSector.getMaxZ() - currentSector.getProtectionDistance()))
            e.setCancelled(true);

    }

}