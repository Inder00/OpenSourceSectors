package pl.inder00.opensource.sectors.basic.impl;

import net.md_5.bungee.api.config.ServerInfo;
import pl.inder00.opensource.sectors.basic.ISector;

import java.util.UUID;

public class SectorImpl implements ISector {

    /**
     * Sector data
     */
    private UUID uniqueId;
    private ServerInfo serverInfo;
    private String hostname;
    private String world;
    private int port;
    private int xa;
    private int za;
    private int xb;
    private int zb;

    /**
     * Implementation
     */
    public SectorImpl(UUID uniqueId, ServerInfo serverInfo, String hostname, int port, String world, int xa, int za, int xb, int zb) {
        this.uniqueId = uniqueId;
        this.serverInfo = serverInfo;
        this.hostname = hostname;
        this.port = port;
        this.world = world;
        this.xa = xa;
        this.xb = xb;
        this.za = za;
        this.zb = zb;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }

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
        return this.xa;
    }

    @Override
    public int getMinZ() {
        return this.za;
    }

    @Override
    public int getMaxX() {
        return this.xb;
    }

    @Override
    public int getMaxZ() {
        return this.zb;
    }
}
