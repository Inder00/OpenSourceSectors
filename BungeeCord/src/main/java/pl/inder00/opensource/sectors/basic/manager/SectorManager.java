package pl.inder00.opensource.sectors.basic.manager;

import net.md_5.bungee.api.config.ServerInfo;
import pl.inder00.opensource.sectors.basic.ISector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SectorManager {

    /**
     * Map of all sectors
     */
    private final ConcurrentHashMap<UUID, ISector> sectors = new ConcurrentHashMap<>();

    /**
     * List of all sectors
     *
     * @return List of sectors
     */
    public List<ISector> getSectorsList() {
        return new ArrayList<>(sectors.values());
    }

    /**
     * Count of sectors
     *
     * @return int
     */
    public int getSectorsCount() {
        return sectors.size();
    }

    /**
     * Add sector to list of sectors
     *
     * @param sector Implementation of sector
     */
    public void addSectorToList(ISector sector) {
        sectors.put(sector.getUniqueId(), sector);
    }

    /**
     * Get sector by the unique id
     *
     * @param uniqueId UUIDv3
     * @return ISector / null
     */
    public ISector getSectorByUniqueId(UUID uniqueId) {
        return sectors.get(uniqueId);
    }

    /**
     * Get sector by the internal server data
     *
     * @param hostname Hostname / IP
     * @param port     Port
     * @return ISector / null
     */
    public ISector getSectorByInternalServer(String hostname, int port) {
        for (Map.Entry<UUID, ISector> entry : sectors.entrySet()) {
            if (entry.getValue().getInternalServerHostname().equalsIgnoreCase(hostname) && entry.getValue().getInternalServerPort() == port) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Get sector by the bungeecord server info
     *
     * @param serverInfo BungeeCord Server Info
     * @return ISector / null
     */
    public ISector getSectorByServerInfo(ServerInfo serverInfo) {
        for (Map.Entry<UUID, ISector> entry : sectors.entrySet()) {
            if (entry.getValue().getServerInfo() == serverInfo) {
                return entry.getValue();
            }
        }
        return null;
    }

}
