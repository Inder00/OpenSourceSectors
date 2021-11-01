package pl.inder00.opensource.sectors.spigot.basic.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.basic.ISectorUser;
import pl.inder00.opensource.sectors.spigot.i18n.utils.I18nUtils;

import java.util.UUID;

public class SectorUserImpl implements ISectorUser {

    /**
     * Data
     */
    private final UUID uniqueId;
    private final long joinTime;
    private String locale;
    private ISector targetSector;
    private Location targetLocation;

    /**
     * Implementation
     */
    public SectorUserImpl(Player player) {
        this.uniqueId = player.getUniqueId();
        this.joinTime = System.currentTimeMillis();
        this.locale = I18nUtils.getPlayerLocale(player);
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getLocale() {
        return this.locale;
    }

    @Override
    public void setLocale(String value) {
        this.locale = value;
    }

    @Override
    public ISector getTargetSector() {
        return this.targetSector;
    }

    @Override
    public void setTargetSector(ISector value, Location target) {
        this.targetSector = value;
        this.targetLocation = target;
    }

    @Override
    public Location getTargetLocation() {
        return this.targetLocation;
    }

    @Override
    public long getJoinTime() {
        return this.joinTime;
    }
}
