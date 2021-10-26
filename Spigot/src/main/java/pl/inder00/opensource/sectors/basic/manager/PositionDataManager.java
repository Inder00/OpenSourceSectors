package pl.inder00.opensource.sectors.basic.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import pl.inder00.opensource.sectors.basic.IPositionData;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PositionDataManager {

    /**
     * Cached data
     */
    private static Cache<UUID, IPositionData> positionDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build();

    /**
     * Gets player position data by player uuid
     * @param uuid Player unique id
     * @return Implementation of IPositionData if present
     */
    public static IPositionData getPositionDataByPlayerUniqueId( UUID uuid ){
        return positionDataCache.getIfPresent(uuid);
    }

    /**
     * Insert position data into cache
     * @param positionData Implementation of IPositionData
     */
    public static void cachePositionData( IPositionData positionData ){
        positionDataCache.put(positionData.getPlayerUniqueId(), positionData);
    }

    /**
     * Deletes position data from cache
     * @param positionData Implementation of IPositionData
     */
    public static void clearPositionData( IPositionData positionData ){
        positionDataCache.invalidate( positionData.getPlayerUniqueId() );
    }

}
