package pl.inder00.opensource.sectors.bungeecord.basic.manager;

import net.md_5.bungee.api.config.ServerInfo;
import pl.inder00.opensource.sectors.bungeecord.basic.ISector;
import pl.inder00.opensource.sectors.bungeecord.basic.ISectorManager;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SectorManagerImpl implements ISectorManager {

    /**
     * Data
     */
    private final ConcurrentMap<UUID, ISector> sectorsData = new ConcurrentHashMap<>();

    @Override
    public ISector getByInternalServer(IInternalServer internalServer) {
        return null;
    }

    @Override
    public ISector getByInternalServer(String hostname, int port) {
        return null;
    }

    @Override
    public ISector getSectorByServerInfo(ServerInfo serverInfo) {
        return null;
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
        this.sectorsData.put(key, data);
    }

    @Override
    public void delete(UUID key) {
        this.sectorsData.remove(key);
    }

}
