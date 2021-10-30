package pl.inder00.opensource.sectors.basic.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.utils.ProtobufUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PositionDataManager {

    /**
     * Cached data
     */
    private Cache<UUID, IProtobufData<ProtobufPositionData.PositionPacket, Player>> positionDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build();

    /**
     * Gets player position data by player uuid
     *
     * @param uuid Player unique id
     * @return Implementation of IPositionData if present
     */
    public IProtobufData<ProtobufPositionData.PositionPacket, Player> getPositionDataByPlayerUniqueId(UUID uuid) {
        return positionDataCache.getIfPresent(uuid);
    }

    /**
     * Insert position data into cache
     *
     * @param positionData Implementation of IPositionData
     */
    public void cachePositionData(IProtobufData<ProtobufPositionData.PositionPacket, Player> positionData) {
        positionDataCache.put(ProtobufUtils.deserialize(positionData.getData().getPlayerUniqueId()), positionData);
    }

    /**
     * Deletes position data from cache
     *
     * @param positionData Implementation of IPositionData
     */
    public void clearPositionData(IProtobufData<ProtobufPositionData.PositionPacket, Player> positionData) {
        positionDataCache.invalidate(ProtobufUtils.deserialize(positionData.getData().getPlayerUniqueId()));
    }

}
