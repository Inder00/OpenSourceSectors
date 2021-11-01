package pl.inder00.opensource.sectors.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.basic.ISector;

public class PlayerChangeSectorEvent extends PlayerEvent implements Cancellable {

    /**
     * Data
     */
    private static final HandlerList handlers = new HandlerList();
    private final ISector sector;
    private boolean cancel = false;

    /**
     * Implementation
     */
    public PlayerChangeSectorEvent(Player player, ISector sector) {
        super(player);
        this.sector = sector;
    }

    /**
     * Event handler list
     *
     * @return HandlerList
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returning a reference of target sector
     *
     * @return ISector
     */
    public ISector getNewSector() {
        return sector;
    }

    /**
     * Returning a reference of current sector
     *
     * @return ISector
     */
    public ISector getOldSector() {
        return Sectors.getSectorManager().getCurrentSector();
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
