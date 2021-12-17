package pl.inder00.opensource.sectors.spigot.basic.manager;

import org.bukkit.Location;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.basic.ISectorManager;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SectorManagerImpl implements ISectorManager {

    /**
     * Current sector unique id (always is set)
     */
    private final UUID currentSectorUniqueId;
    /**
     * List of sectors
     */
    private final ConcurrentMap<UUID, ISector> sectorsData = new ConcurrentHashMap<>();
    /**
     * Current sector object refernece (may be null)
     */
    private ISector currentSector;

    /**
     * Implementation
     */
    public SectorManagerImpl(UUID sectorUniqueId) {
        this.currentSectorUniqueId = sectorUniqueId;
    }

    @Override
    public Collection<ISector> getDataCollection() {
        return this.sectorsData.values();
    }

    @Override
    public int getDataCount() {
        return this.sectorsData.size();
    }

    @Override
    public ISector getByKey(UUID key) {
        return this.sectorsData.get(key);
    }

    @Override
    public void save(ISector data, UUID key) {
        if (key.equals(this.currentSectorUniqueId)) {
            this.currentSector = data;
        } else {
            this.sectorsData.put(key, data);
        }
    }

    @Override
    public void delete(UUID key) {
        this.sectorsData.remove(key);
    }

    @Override
    public ISector getAtLocation(Location location) {
        return this.sectorsData.values().stream().filter(sector -> sector.isInLocation(location)).findFirst().orElse(null);
    }

    @Override
    public ISector getCurrentSector() {
        return this.currentSector;
    }

    @Override
    public UUID getCurrentSectorUniqueId() {
        return this.currentSectorUniqueId;
    }

}
