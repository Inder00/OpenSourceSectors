package pl.inder00.opensource.sectors.spigot.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import pl.inder00.opensource.sectors.spigot.Sectors;

public class AsyncPlayerPreLoginListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(AsyncPlayerPreLoginEvent e){

        // check does sectors are already loaded
        if(Sectors.getMasterServer() == null || !Sectors.getMasterServer().isConnected() || Sectors.getSectorManager().getDataCount() == 0)
        {

            // disallow
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Sectors are not loaded yet. Contact with server administrator.");

        }

    }

}
