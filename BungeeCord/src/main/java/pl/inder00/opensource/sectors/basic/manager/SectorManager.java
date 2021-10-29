package pl.inder00.opensource.sectors.basic.manager;

import net.md_5.bungee.api.config.ServerInfo;
import pl.inder00.opensource.sectors.basic.ISector;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SectorManager {

    /**
     * Map of all sectors
     */
    private static ConcurrentHashMap<UUID, ISector> listOfSectors = new ConcurrentHashMap<UUID, ISector>();

    /**
     * List of all sectors
     *
     * @return List of sectors
     */
    public static List<ISector> getSectorsList() {
        return listOfSectors.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    /**
     * Count of sectors
     *
     * @return int
     */
    public static int getSectorsCount() {
        return listOfSectors.size();
    }

    /**
     * Add sector to list of sectors
     *
     * @param sector Implementation of sector
     */
    public static void addSectorToList(ISector sector) {
        listOfSectors.put(sector.getUniqueId(), sector);
    }

    /**
     * Get sector by the unique id
     *
     * @param uniqueId UUIDv3
     * @return ISector / null
     */
    public static ISector getSectorByUniqueId(UUID uniqueId) {
        return listOfSectors.get(uniqueId);
    }

    /**
     * Get sector by the internal server data
     *
     * @param hostname Hostname / IP
     * @param port     Port
     * @return ISector / null
     */
    public static ISector getSectorByInternalServer(String hostname, int port) {
        for (Map.Entry<UUID, ISector> entry : listOfSectors.entrySet()) {
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
    public static ISector getSectorByServerInfo(ServerInfo serverInfo) {
        for (Map.Entry<UUID, ISector> entry : listOfSectors.entrySet()) {
            if (entry.getValue().getServerInfo() == serverInfo) {
                return entry.getValue();
            }
        }
        return null;
    }

}
