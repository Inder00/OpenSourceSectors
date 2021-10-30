package pl.inder00.opensource.sectors.basic.impl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.ISectorUser;
import pl.inder00.opensource.sectors.basic.manager.SectorUserManager;
import pl.inder00.opensource.sectors.events.PlayerChangeSectorEvent;
import pl.inder00.opensource.sectors.logging.SectorLogger;
import pl.inder00.opensource.sectors.protocol.ISectorClient;
import pl.inder00.opensource.sectors.utils.SpigotPayloadUtils;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;
import java.util.logging.Logger;

public class SectorImpl implements ISector {

    /**
     * Sector data
     */
    private Sectors sectors;
    private UUID uniqueId;
    private ISectorClient sectorClient;
    private Logger logger;
    private World world;
    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;
    private int protectionDistance;
    private int sectorChangeCooldown;

    /**
     * Implementation
     */
    public SectorImpl(Sectors plugin, UUID uniqueId, ISectorClient sectorClient, World world, int minX, int minZ, int maxX, int maxZ, int protectionDistance, int changeCooldown) {
        this.sectors = plugin;
        this.uniqueId = uniqueId;
        this.sectorClient = sectorClient;
        this.world = world;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.protectionDistance = protectionDistance;
        this.sectorChangeCooldown = changeCooldown * 1000; // convert to millis
        this.logger = new SectorLogger(plugin, this);
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public ISectorClient getEndpoint() {
        return this.sectorClient;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public int getMinX() {
        return this.minX;
    }

    @Override
    public int getMinZ() {
        return this.minZ;
    }

    @Override
    public int getMaxX() {
        return this.maxX;
    }

    @Override
    public int getMaxZ() {
        return this.maxZ;
    }

    @Override
    public int getProtectionDistance() {
        return this.protectionDistance;
    }

    @Override
    public int getSectorChangeCooldown() {
        return this.sectorChangeCooldown;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public boolean isInLocation(Location location) {
        return location.getX() >= this.minX && location.getX() <= this.maxX && location.getZ() >= this.minZ && location.getZ() <= this.maxZ;
    }

    @Override
    public int getDistanceToBorder(Location location) {
        return (int) Math.min(Math.min(Math.abs(location.getX() - this.minX + 3), Math.abs(location.getX() - this.maxX - 3)), Math.min(Math.abs(location.getZ() - this.maxZ - 3), Math.abs(location.getZ() + this.minZ - 3)));
    }

    @Override
    public void send(ISectorClient masterserver, ISectorUser sectorUser, Player player) {
        this.send(masterserver, sectorUser, player, player.getLocation());
    }

    @Override
    public void send(ISectorClient masterserver, ISectorUser sectorUser, Player player, Location location) {

        // check cooldown
        long currentTimeMillis = System.currentTimeMillis();
        if ((SectorUserManager.getUserJoinTime(player.getUniqueId()) + this.sectorChangeCooldown) > currentTimeMillis) {

            // send message
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.sectors.languageProvider.getLocalizedMessage(sectorUser.getLocale(), "sector.change.cooldown")));
            return;

        }

        // call sector change event
        PlayerChangeSectorEvent event = new PlayerChangeSectorEvent(player, this);

        // fire event
        Bukkit.getServer().getPluginManager().callEvent(event);

        // check if the event is not cancelled
        if (!event.isCancelled()) {

            // update target sector
            sectorUser.setTargetSector(this, location);

            // send transfer data
            try {

                // send positon data to target server
                this.sectorClient.getRSocket().fireAndForget(SpigotPayloadUtils.createPositionDataPayload(player, location))
                        .doOnError(error -> {

                            // checks is player online
                            if (player.isOnline()) {

                                // clear target sector
                                sectorUser.setTargetSector(null, null);

                                // send message
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(this.sectors.languageProvider.getLocalizedMessage(sectorUser.getLocale(), "failed.connect"), error.getMessage())));

                            }

                        })
                        .doOnSuccess(success -> {

                            // send player to target server
                            masterserver.getRSocket().fireAndForget(SpigotPayloadUtils.createSectorChangePayload(player, this))
                                    .doOnError(error -> {

                                        // checks is player online
                                        if (player.isOnline()) {

                                            // clear target sector
                                            sectorUser.setTargetSector(null, null);

                                            // send message
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(this.sectors.languageProvider.getLocalizedMessage(sectorUser.getLocale(), "failed.connect"), error.getMessage())));

                                        }

                                    })
                                    .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(2)))
                                    .subscribe();

                        })
                        .subscribe();

            } catch (Throwable e) {

                // throw data
                e.printStackTrace();

            }

        }

    }
}
