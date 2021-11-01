package pl.inder00.opensource.sectors.bungeecord.basic;

import net.md_5.bungee.api.config.ServerInfo;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.commons.managers.IManager;

import java.util.UUID;

public interface ISectorManager extends IManager<ISector, UUID> {

    /**
     * Gets sector by internal server data
     *
     * @param hostname Internal server's hostname
     * @param port Internal server's port
     * @return ISector
     */
    ISector getByInternalServer(String hostname, int port);

    /**
     * Gets sector by internal server
     *
     * @param internalServer IInternalServer
     * @return ISector
     */
    ISector getByInternalServer(IInternalServer internalServer);

    /**
     * Gets sector by BungeeCord's server info
     * @param serverInfo ServerInfo
     * @return ISector
     */
    ISector getSectorByServerInfo(ServerInfo serverInfo);

}
