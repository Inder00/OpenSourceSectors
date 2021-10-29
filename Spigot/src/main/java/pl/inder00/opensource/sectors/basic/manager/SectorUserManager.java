package pl.inder00.opensource.sectors.basic.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import pl.inder00.opensource.sectors.basic.ISectorUser;
import pl.inder00.opensource.sectors.basic.impl.SectorUserImpl;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SectorUserManager {

    /**
     * Cached data
     */
    private static LoadingCache<UUID, ISectorUser> sectorUserCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build(new CacheLoader<UUID, ISectorUser>() {
                @Override
                public ISectorUser load(UUID key) throws Exception {
                    return new SectorUserImpl(Bukkit.getPlayer(key));
                }
            });
    private static HashMap<UUID, Long> userJoinTimeCache = new HashMap<>();

    /**
     * Gets sector uset by player uuid
     *
     * @param uuid Player unique id
     * @return Implementation of ISectorUser
     */
    public static ISectorUser getUserByPlayerUniqueId(UUID uuid) {
        try {
            return sectorUserCache.get(uuid);
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * Gets sector uset by player uuid
     *
     * @param uuid Player unique id
     * @return Implementation of ISectorUser
     */
    public static ISectorUser getUserByPlayerUniqueIdIfPresent(UUID uuid) {
        return sectorUserCache.getIfPresent(uuid);
    }

    /**
     * Deleting sector user from cache
     *
     * @param sectorUser Implementation of ISectorUser
     */
    public static void deleteSectorUser(ISectorUser sectorUser) {
        sectorUserCache.invalidate(sectorUser.getUniqueId());
    }

    /**
     * Deletes sector user from cache
     *
     * @param uniqueId UUID
     */
    public static void deleteSectorUser(UUID uniqueId) {
        sectorUserCache.invalidate(uniqueId);
    }

    /**
     * Gets player's join time unix time
     *
     * @param uniqueId Player unique id
     * @return long
     */
    public static long getUserJoinTime(UUID uniqueId) {
        return userJoinTimeCache.get(uniqueId);
    }

    /**
     * Adds player's join time unix time
     *
     * @param uniqueId Player unique id
     * @param unix     Unix time
     */
    public static void setUserJoinTime(UUID uniqueId, long unix) {
        userJoinTimeCache.put(uniqueId, unix);
    }

    /**
     * Deletes user join time unix
     *
     * @param uniqueId Player unique id
     */
    public static void deleteJoinTimeUser(UUID uniqueId) {
        userJoinTimeCache.remove(uniqueId);
    }

}
