package pl.inder00.opensource.sectors.spigot.basic.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.logging.SectorLogger;

import java.util.UUID;
import java.util.logging.Logger;

public class SectorImpl implements ISector {

    /**
     * Sector data
     */
    private final UUID uniqueId;
    private final ISectorClient sectorClient;
    private final Logger logger;
    private final World world;
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;
    private final int protectionDistance;
    private final int sectorChangeCooldown;

    /**
     * Implementation
     */
    public SectorImpl(JavaPlugin plugin, UUID uniqueId, ISectorClient sectorClient, World world, int minX, int minZ, int maxX, int maxZ, int protectionDistance, int changeCooldown) {
        this.uniqueId = uniqueId;
        this.sectorClient = sectorClient;
        this.world = world;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.protectionDistance = protectionDistance;
        this.sectorChangeCooldown = changeCooldown * 1000; // convert to millis
        this.logger = new SectorLogger(plugin, this);
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public ISectorClient getEndpoint() {
        return this.sectorClient;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public int getMinX() {
        return this.minX;
    }

    @Override
    public int getMinZ() {
        return this.minZ;
    }

    @Override
    public int getMaxX() {
        return this.maxX;
    }

    @Override
    public int getMaxZ() {
        return this.maxZ;
    }

    @Override
    public int getProtectionDistance() {
        return this.protectionDistance;
    }

    @Override
    public int getSectorChangeCooldown() {
        return this.sectorChangeCooldown;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public boolean isInLocation(Location location) {
        return location.getX() >= this.minX && location.getX() <= this.maxX && location.getZ() >= this.minZ && location.getZ() <= this.maxZ;
    }

    @Override
    public int getDistanceToBorder(Location location) {
        return (int) Math.min(Math.min(Math.abs(location.getX() - this.minX + 3), Math.abs(location.getX() - this.maxX - 3)), Math.min(Math.abs(location.getZ() - this.maxZ - 3), Math.abs(location.getZ() + this.minZ - 3)));
    }

}
