package pl.inder00.opensource.sectors.logging;

import org.bukkit.plugin.Plugin;
import pl.inder00.opensource.sectors.basic.ISector;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SectorLogger extends Logger {

    /**
     * Sector name (UUIDv3)
     */
    private final String sectorName;

    /**
     * Creates a new SectorLogger that extracts the from a plugin.
     *
     * @param plugin A reference to the plugin
     * @param sector A reference to the sector
     */
    public SectorLogger(Plugin plugin, ISector sector) {
        super(plugin.getClass().getCanonicalName(), null);
        String prefix = plugin.getDescription().getPrefix();
        sectorName = "[" + (prefix != null ? prefix : plugin.getName()) + "-" + sector.getUniqueId().toString() + "] ";
        setParent(plugin.getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(sectorName + logRecord.getMessage());
        super.log(logRecord);
    }

}
