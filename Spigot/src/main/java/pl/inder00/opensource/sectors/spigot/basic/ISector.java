package pl.inder00.opensource.sectors.spigot.basic;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.protocol.ISectorClient;

import java.util.UUID;
import java.util.logging.Logger;

public interface ISector {

    /**
     * The UUID of sector translated from string
     *
     * @return UUIDv3
     */
    UUID getUniqueId();

    /**
     * Sector endpoint
     *
     * @return ISectorClient
     */
    ISectorClient getEndpoint();

    /**
     * Reference of world
     *
     * @return World
     */
    World getWorld();

    /**
     * The x coordinate of first corner
     *
     * @return int
     */
    int getMinX();

    /**
     * The z coordinate of first corner
     *
     * @return int
     */
    int getMinZ();

    /**
     * The x coordinate of second corner
     *
     * @return int
     */
    int getMaxX();

    /**
     * The z coordinate of second coordinate
     *
     * @return int
     */
    int getMaxZ();

    /**
     * Returns protection distance
     *
     * @return int
     */
    int getProtectionDistance();

    /**
     * Returns cooldown to change sector
     *
     * @return int
     */
    int getSectorChangeCooldown();

    /**
     * Sector logging system
     *
     * @return Logger
     */
    Logger getLogger();

    /**
     * Send player to sector
     *
     * @param masterserver Connection to the master server
     * @param sectorUser   Sector user
     * @param player       A reference to the player
     */
    void send(ISectorClient masterserver, ISectorUser sectorUser, Player player);

    /**
     * Send player to sector with specified location
     *
     * @param masterserver Connection to the master server
     * @param location     Location
     * @param sectorUser   Sector user
     * @param player       A reference to the player
     */
    void send(ISectorClient masterserver, ISectorUser sectorUser, Player player, Location location);

    /**
     * Returning a boolean representing does location is in sector area
     *
     * @param location Location
     * @return boolean
     */
    boolean isInLocation(Location location);

    /**
     * Returning a distance to the area border
     *
     * @param location Location
     * @return
     */
    int getDistanceToBorder(Location location);
}
