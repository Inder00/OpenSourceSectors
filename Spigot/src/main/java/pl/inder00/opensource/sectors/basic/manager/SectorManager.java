package pl.inder00.opensource.sectors.basic.manager;

import org.bukkit.Location;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.protocol.ISectorServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SectorManager {

    /**
     * List of all available sectors
     */
    private final ConcurrentHashMap<UUID, ISector> sectors = new ConcurrentHashMap<>();
    /**
     * Current sector where plugin is runs
     */
    private UUID currentSectorUniqueId;
    private ISector currentSector;
    private ISectorServer currentSectorEndpoint;

    /**
     * List of all sectors
     *
     * @return List of sectors
     */
    public List<ISector> getSectorsList() {
        return new ArrayList<>(sectors.values());
    }

    /**
     * Gets sector implementation by their location
     *
     * @param location
     * @return ISector / null
     */
    public ISector getSectorAtLocation(Location location) {
        return sectors.values().stream().filter(sector -> sector.isInLocation(location)).findFirst().orElse(null);
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
     * Count of sectors
     *
     * @return int
     */
    public int getSectorsCount() {
        return sectors.size();
    }

    /**
     * Gets current sector may return null
     *
     * @return ISocket
     */
    public ISector getCurrentSector() {
        return currentSector;
    }

    /**
     * Sector endpoint using to handle players data packets between servers
     *
     * @return ISectorServer
     */
    public ISectorServer getCurrentSectorEndpoint() {
        return currentSectorEndpoint;
    }

    /**
     * Set current sector endpoint implementation
     *
     * @param endpoint A reference of sector server
     */
    public void setCurrentSectorEndpoint(ISectorServer endpoint) {
        currentSectorEndpoint = endpoint;
    }

    /**
     * Clear list of sectors
     */
    public void clearSectors() {
        sectors.clear();
    }

    /**
     * Adds sector to list of sectors
     *
     * @param sector Implementation of sector
     */
    public void addSectorToList(ISector sector) {

        // update current sector if necessary
        if (currentSectorUniqueId.equals(sector.getUniqueId())) currentSector = sector;

        // add sector to map
        sectors.put(sector.getUniqueId(), sector);

    }

    /**
     * Current sector unique id
     *
     * @return UUIDv3 / null
     */
    public UUID getCurrentSectorUniqueId() {
        return currentSectorUniqueId;
    }

    /**
     * Set current sector unique id for identification
     *
     * @param uuid UUIDv3 from sector name
     */
    public void setCurrentSectorUniqueId(UUID uuid) {
        currentSectorUniqueId = uuid;
    }
}
