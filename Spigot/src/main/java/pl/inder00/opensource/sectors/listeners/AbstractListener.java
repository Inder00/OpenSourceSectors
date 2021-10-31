package pl.inder00.opensource.sectors.listeners;

import org.bukkit.event.Listener;
import pl.inder00.opensource.sectors.Sectors;

public abstract class AbstractListener implements Listener {

    /**
     * Main class
     */
    public Sectors sectors;

    /**
     * Implementation
     */
    public AbstractListener(Sectors sectors) {
        this.sectors = sectors;
    }

}
