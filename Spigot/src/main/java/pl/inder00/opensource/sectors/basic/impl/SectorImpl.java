package pl.inder00.opensource.sectors.basic.impl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.basic.ISectorUser;
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
    private final Sectors sectors;
    private final UUID uniqueId;
    private final ISectorClient sectorClient;
    private final Logger logger;
    private final World world;
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;
    private final int protectionDistance;
    private final int sectorChangeCooldown;

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
        return (int) Math.min(Math.min(Math.abs(location.getX() - this.minX), Math.abs(location.getX() - this.maxX)), Math.min(Math.abs(location.getZ() - this.maxZ), Math.abs(location.getZ() - this.minZ)));
    }

    @Override
    public void send(ISectorClient masterserver, ISectorUser sectorUser, Player player) {
        this.send(masterserver, sectorUser, player, player.getLocation());
    }

    @Override
    public void send(ISectorClient masterserver, ISectorUser sectorUser, Player player, Location location) {

        // check cooldown
        long currentTimeMillis = System.currentTimeMillis();
        if ((sectorUser.getJoinTime() + this.sectorChangeCooldown) > currentTimeMillis) {

            // send message
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.sectors.languageProvider.getLocalizedMessage(sectorUser.getLocale(), "sector.change.cooldown")));
            return;

        }

        // call sector change event
        PlayerChangeSectorEvent event = new PlayerChangeSectorEvent(this.sectors, player, this);

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
