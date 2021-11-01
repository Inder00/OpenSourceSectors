package pl.inder00.opensource.sectors.spigot.basic.manager;

import pl.inder00.opensource.sectors.spigot.basic.ISectorUser;
import pl.inder00.opensource.sectors.commons.managers.IManager;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SectorUserManagerImpl implements IManager<ISectorUser, UUID> {

    /**
     * Data
     */
    private final ConcurrentMap<UUID, ISectorUser> usersData = new ConcurrentHashMap<>();

    @Override
    public Collection<ISectorUser> getDataCollection() {
        return this.usersData.values();
    }

    @Override
    public int getDataCount() {
        return this.usersData.size();
    }

    @Override
    public ISectorUser getByKey(UUID key) {
        return this.usersData.get(key);
    }

    @Override
    public void save(ISectorUser data, UUID key) {
        this.usersData.put(key, data);
    }

    @Override
    public void delete(UUID key) {
        this.usersData.remove(key);
    }

}
