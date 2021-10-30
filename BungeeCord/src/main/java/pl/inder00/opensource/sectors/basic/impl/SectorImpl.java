package pl.inder00.opensource.sectors.basic.impl;

import net.md_5.bungee.api.config.ServerInfo;
import pl.inder00.opensource.sectors.basic.ISector;

import java.util.UUID;

public class SectorImpl implements ISector {

    /**
     * Sector data
     */
    private final UUID uniqueId;
    private final ServerInfo serverInfo;
    private final String hostname;
    private final String world;
    private final int port;
    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;

    /**
     * Implementation
     */
    public SectorImpl(UUID uniqueId, ServerInfo serverInfo, String hostname, int port, String world, int minX, int maxX, int minZ, int maxZ) {
        this.uniqueId = uniqueId;
        this.serverInfo = serverInfo;
        this.hostname = hostname;
        this.port = port;
        this.world = world;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }

    /**
     * UniqueID
     *
     * @return uuid
     */
    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    /**
     * ServerInfo
     *
     * @return serverinfo
     */
    @Override
    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }

    /**
     * InternalServerHostname
     *
     * @return string
     */
    @Override
    public String getInternalServerHostname() {
        return this.hostname;
    }

    @Override
    public int getInternalServerPort() {
        return this.port;
    }

    @Override
    public String getWorld() {
        return this.world;
    }

    @Override
    public int getMinX() {
        return this.minX;
    }

    @Override
    public int getMinZ() {
        return this.maxX;
    }

    @Override
    public int getMaxX() {
        return this.minZ;
    }

    @Override
    public int getMaxZ() {
        return this.maxZ;
    }
}
