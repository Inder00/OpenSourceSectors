package pl.inder00.opensource.sectors.basic;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IPositionData {

    /**
     * Player unique id
     *
     * @return UUIDv3
     */
    UUID getPlayerUniqueId();

    /**
     * Gets X position
     *
     * @return double
     */
    double getX();

    /**
     * Gets Y position
     *
     * @return double
     */
    double getY();

    /**
     * Gets Z position
     *
     * @return double
     */
    double getZ();

    /**
     * Gets yaw position
     *
     * @return float
     */
    float getYaw();

    /**
     * Gets pitch position
     *
     * @return float
     */
    float getPitch();

    /**
     * Apply position data to player
     *
     * @param player A reference to player
     */
    void apply(Player player);

}
