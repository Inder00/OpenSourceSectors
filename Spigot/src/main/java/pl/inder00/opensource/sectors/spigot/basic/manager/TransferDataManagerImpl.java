package pl.inder00.opensource.sectors.spigot.basic.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.commons.managers.IManager;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.protobuf.TransferPacket;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TransferDataManagerImpl implements IManager<IProtobufData<TransferPacket.PlayerTransferPacket, Player>, UUID> {

    /**
     * Cached data
     */
    private final Cache<UUID, IProtobufData<TransferPacket.PlayerTransferPacket, Player>> transferDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build();

    @Override
    public Collection<IProtobufData<TransferPacket.PlayerTransferPacket, Player>> getDataCollection() {
        return this.transferDataCache.asMap().values();
    }

    @Override
    public int getDataCount() {
        return ((int) this.transferDataCache.size());
    }

    @Override
    public IProtobufData<TransferPacket.PlayerTransferPacket, Player> getByKey(UUID key) {
        return this.transferDataCache.getIfPresent(key);
    }

    @Override
    public void save(IProtobufData<TransferPacket.PlayerTransferPacket, Player> data, UUID key) {
        this.transferDataCache.put(key, data);
    }

    @Override
    public void delete(UUID key) {
        this.transferDataCache.invalidate(key);
    }
}
