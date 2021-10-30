package pl.inder00.opensource.sectors.basic;

import org.bukkit.Location;

import java.util.UUID;

public interface ISectorUser {

    /**
     * Gets player unique id
     *
     * @return UUID
     */
    UUID getUniqueId();

    /**
     * Gets player locale
     *
     * @return String
     */
    String getLocale();

    /**
     * Updates player locale
     *
     * @param value String
     */
    void setLocale(String value);

    /**
     * Gets a target sector where player is moving
     *
     * @return ISector / null
     */
    ISector getTargetSector();

    /**
     * Gets a target location where player should be moved
     *
     * @return Location / null
     */
    Location getTargetLocation();

    /**
     * Sets a target sector where player is moving
     *
     * @param targetSector  ISector
     * @param target Location where player should be moved
     */
    void setTargetSector(ISector targetSector, Location target);

}
