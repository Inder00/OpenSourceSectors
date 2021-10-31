package pl.inder00.opensource.sectors.basic;

import org.bukkit.Location;

import java.util.UUID;

public interface ISectorManager extends IManager<ISector, UUID> {

    /**
     * Returns current sector reference (may be null)
     *
     * @return ISector
     */
    ISector getCurrentSector();

    /**
     * Returns current sector unique id (always is set)
     *
     * @return UUID
     */
    UUID getCurrentSectorUniqueId();

    /**
     * Returns sector at specified location
     *
     * @param location Bukkit location
     * @return ISector
     */
    ISector getAtLocation(Location location);

}
