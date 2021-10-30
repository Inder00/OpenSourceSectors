package pl.inder00.opensource.sectors.basic.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.utils.ProtobufUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TransferDataManager {

    /**
     * Cached data
     */
    private final Cache<UUID, IProtobufData<ProtobufTransferData.TransferPacket, Player>> transferDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build();

    /**
     * Gets player transfer data by player uuid
     *
     * @param uuid Player unique id
     * @return Implementation of ITransferData if present
     */
    public IProtobufData<ProtobufTransferData.TransferPacket, Player> getTransferDataByPlayerUniqueId(UUID uuid) {
        return transferDataCache.getIfPresent(uuid);
    }

    /**
     * Insert transfer data into cache
     *
     * @param transferData Implementation of ITransferData
     */
    public void cacheTransferData(IProtobufData<ProtobufTransferData.TransferPacket, Player> transferData) {
        transferDataCache.put(ProtobufUtils.deserialize(transferData.getData().getPlayerUniqueId()), transferData);
    }

    /**
     * Deletes transfer data from cache
     *
     * @param transferData Implementation of ITransferData
     */
    public void clearTransferData(IProtobufData<ProtobufTransferData.TransferPacket, Player> transferData) {
        transferDataCache.invalidate(ProtobufUtils.deserialize(transferData.getData().getPlayerUniqueId()));
    }

}
