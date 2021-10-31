package pl.inder00.opensource.sectors.basic;

import net.md_5.bungee.api.config.ServerInfo;
import pl.inder00.opensource.sectors.protobuf.ProtobufGeneric;

import java.util.UUID;

public interface ISector {

    /**
     * Returns sector unique id
     *
     * @return UUIDv3
     */
    UUID getUniqueId();

    /**
     * Returns BungeeCord sector server info
     *
     * @return ServerInfo
     */
    ServerInfo getServerInfo();

    /**
     * Returns reference of internal server
     *
     * @return IInternalServer
     */
    IInternalServer getInternalServer();

    /**
     * World name
     *
     * @return
     */
    String getWorld();

    /**
     * Returns x coordinate of first corner
     *
     * @return int
     */
    int getMinX();

    /**
     * Returns z coordinate of first corner
     *
     * @return int
     */
    int getMinZ();

    /**
     * Returns x coordinate of second corner
     *
     * @return int
     */
    int getMaxX();

    /**
     * Returns z coordinate of second coordinate
     *
     * @return int
     */
    int getMaxZ();

    /**
     * Returns protobuf sector implementation
     *
     * @return ProtoSector
     */
    ProtobufGeneric.ProtoSector getProtobufSector();

}
