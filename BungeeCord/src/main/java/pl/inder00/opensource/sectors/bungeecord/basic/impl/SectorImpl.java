package pl.inder00.opensource.sectors.bungeecord.basic.impl;

import net.md_5.bungee.api.config.ServerInfo;
import pl.inder00.opensource.sectors.bungeecord.basic.ISector;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufGeneric;

import java.util.UUID;

public class SectorImpl implements ISector {

    /**
     * Sector data
     */
    private final UUID uniqueId;
    private final ServerInfo serverInfo;
    private final IInternalServer internalServer;
    private final String world;
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;
    private final ProtobufGeneric.ProtoSector protoSector;

    /**
     * Implementation
     */
    public SectorImpl(UUID uniqueId, ServerInfo serverInfo, IInternalServer internalServer, String world, int minX, int minZ, int maxX, int maxZ) {

        // set data
        this.uniqueId = uniqueId;
        this.serverInfo = serverInfo;
        this.internalServer = internalServer;
        this.world = world;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;

        // build protobuf implementation
        this.protoSector = ProtobufGeneric.ProtoSector.newBuilder().setUniqueId(ProtobufGeneric.ProtoUUID.newBuilder().setMostSig(this.uniqueId.getMostSignificantBits()).setLeastSig(this.uniqueId.getLeastSignificantBits()).build()).setInternalServer(ProtobufGeneric.ProtoInternalServer.newBuilder().setHostname(this.internalServer.getHostname()).setPort(this.internalServer.getPort()).build()).setWorldName(this.world).setMinX(this.minX).setMinZ(this.minZ).setMaxX(this.maxX).setMaxZ(this.maxZ).build();

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
    public IInternalServer getInternalServer() {
        return this.internalServer;
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
    public ProtobufGeneric.ProtoSector getProtobufSector() {
        return this.protoSector;
    }

}
