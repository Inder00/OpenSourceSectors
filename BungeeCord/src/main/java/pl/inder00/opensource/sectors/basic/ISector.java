package pl.inder00.opensource.sectors.basic;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.UUID;

public interface ISector {

    /**
     * Server unique id
     *
     * @return UUIDv3
     */
    UUID getUniqueId();

    /**
     * BungeeCord sector server info
     *
     * @return ServerInfo
     */
    ServerInfo getServerInfo();

    /**
     * Internal server hostname / ip
     *
     * @return String
     */
    String getInternalServerHostname();

    /**
     * Internal server port
     *
     * @return int
     */
    int getInternalServerPort();

    /**
     * World name
     *
     * @return
     */
    String getWorld();

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

}
