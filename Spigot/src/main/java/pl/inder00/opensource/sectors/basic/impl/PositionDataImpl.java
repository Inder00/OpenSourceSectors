package pl.inder00.opensource.sectors.basic.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.basic.IPositionData;
import pl.inder00.opensource.sectors.basic.manager.PositionDataManager;
import pl.inder00.opensource.sectors.basic.manager.TransferDataManager;

import java.util.UUID;

public class PositionDataImpl implements IPositionData {

    /**
     * Data
     */
    private UUID uniqueId;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    /**
     * Constructor
     */
    public PositionDataImpl(UUID uniqueId, double x, double y, double z, float yaw, float pitch) {
        this.uniqueId = uniqueId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public UUID getPlayerUniqueId() {
        return this.uniqueId;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public float getYaw() {
        return this.yaw;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void apply(Player player) {

        // update position
        player.teleport( new Location(player.getWorld(), this.x, this.y, this.z, this.yaw, this.pitch) );

        // remove inventory, enderchest, potions containts
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));

        // set player undamagable
        player.setNoDamageTicks( 600 );

        // remove data from cache
        PositionDataManager.clearPositionData( this );

    }
}
