package pl.inder00.opensource.sectors.spigot.basic.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.commons.managers.IManager;
import pl.inder00.opensource.sectors.protocol.protobuf.PositionPacket;
import pl.inder00.opensource.sectors.protocol.IProtobufData;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PositionDataManagerImpl implements IManager<IProtobufData<PositionPacket.PlayerPositionPacket, Player>, UUID> {

    /**
     * Data
     */
    private final Cache<UUID, IProtobufData<PositionPacket.PlayerPositionPacket, Player>> positionDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build();

    @Override
    public Collection<IProtobufData<PositionPacket.PlayerPositionPacket, Player>> getDataCollection() {
        return this.positionDataCache.asMap().values();
    }

    @Override
    public int getDataCount() {
        return ((int) this.positionDataCache.size());
    }

    @Override
    public IProtobufData<PositionPacket.PlayerPositionPacket, Player> getByKey(UUID key) {
        return this.positionDataCache.getIfPresent(key);
    }

    @Override
    public void save(IProtobufData<PositionPacket.PlayerPositionPacket, Player> data, UUID key) {
        this.positionDataCache.put(key, data);
    }

    @Override
    public void delete(UUID key) {
        this.positionDataCache.invalidate(key);
    }
}
