package pl.inder00.opensource.sectors.spigot.basic.impl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.protocol.IPacketStatus;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.basic.ISectorUser;
import pl.inder00.opensource.sectors.spigot.events.PlayerChangeSectorEvent;
import pl.inder00.opensource.sectors.spigot.i18n.utils.I18nUtils;
import pl.inder00.opensource.sectors.spigot.utils.SpigotPacketUtils;

import java.util.UUID;

public class SectorUserImpl implements ISectorUser {

    /**
     * Data
     */
    private final Player player;
    private final UUID uniqueId;
    private final long joinTime;
    private String locale;
    private ISector targetSector;
    private Location targetLocation;

    /**
     * Implementation
     */
    public SectorUserImpl(Player player) {
        this.player = player;
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
    public Location getTargetLocation() {
        return this.targetLocation;
    }

    @Override
    public long getJoinTime() {
        return this.joinTime;
    }

    @Override
    public void send(ISector sector) {
        this.send(sector, this.player.getLocation());
    }

    @Override
    public void send(ISector sector, Location location) {

        // check cooldown
        long currentTimeMillis = System.currentTimeMillis();
        if ((this.joinTime + sector.getSectorChangeCooldown()) > currentTimeMillis) {

            // send message
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Sectors.getLanguageProvider().getLocalizedMessage(this.locale, "sector.change.cooldown")));
            return;

        }

        // call sector change event
        PlayerChangeSectorEvent event = new PlayerChangeSectorEvent(player, sector);

        // fire event
        Bukkit.getServer().getPluginManager().callEvent(event);

        // check if the event is not cancelled
        if (!event.isCancelled()) {

            // update target sector
            this.targetSector = sector;
            this.targetLocation = location;

            // send position data to target server
            try
            {

                // check does target server is online
                if(sector.getEndpoint() == null || !sector.getEndpoint().isConnected())
                {

                    // send message
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Sectors.getLanguageProvider().getLocalizedMessage(this.locale, "sector.offline")));
                    return;

                }

                // send position data to target server
                sector.getEndpoint().sendData(SpigotPacketUtils.createPlayerPositionPacket(this.player,location), status -> {

                    // check does packet has been successfully send
                    if(!status.equals(IPacketStatus.OK))
                    {

                        // send message
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(Sectors.getLanguageProvider().getLocalizedMessage(this.locale, "failed.connect"), "Packet can't reach the destination server.")));
                        return;

                    }

                    // send change server packet to master server
                    Sectors.getMasterServer().sendData(SpigotPacketUtils.createChangeServerPacket(this.player,sector));


                });

            }
            catch (Throwable e)
            {

                // print error
                e.printStackTrace();

            }

        }

    }
}
