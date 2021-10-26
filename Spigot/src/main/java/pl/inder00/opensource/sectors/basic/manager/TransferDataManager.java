package pl.inder00.opensource.sectors.basic.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import pl.inder00.opensource.sectors.basic.ITransferData;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TransferDataManager {

    /**
     * Cached data
     */
    private static Cache<UUID, ITransferData> transferDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build();

    /**
     * Gets player transfer data by player uuid
     * @param uuid Player unique id
     * @return Implementation of ITransferData if present
     */
    public static ITransferData getTransferDataByPlayerUniqueId( UUID uuid ){
        return transferDataCache.getIfPresent(uuid);
    }

    /**
     * Insert transfer data into cache
     * @param transferData Implementation of ITransferData
     */
    public static void cacheTransferData( ITransferData transferData ){
        transferDataCache.put(transferData.getPlayerUniqueId(), transferData);
    }

    /**
     * Deletes transfer data from cache
     * @param transferData Implementation of ITransferData
     */
    public static void clearTransferData( ITransferData transferData ){
        transferDataCache.invalidate( transferData.getPlayerUniqueId() );
    }

}
