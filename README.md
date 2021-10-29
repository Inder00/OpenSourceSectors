<<<<<<< HEAD

# OpenSourceSectors 😎

🗒️This is a plugin for [Minecraft Server (Spigot API)](https://spigotmc.org/) introduces a sector system which connects
a single world across multiple servers.

🍎The reason why I released this plugin is because I would like to make combining servers into one to spread out load
between them easier. 👉Finally, I am glad to provide the OpenSourceSectors, and everyone is welcome to adjust the code
and help make it better by editting the code, adding new features, etc.

# Requirements 📗

- Bungeecord
- Spigot servers (as many you want)
- Private address space (to internal communication between servers) (eg. 127.0.0.1, 172.16.0.0, etc...)

# Usage ✔️

🎉Download the latest stable release from [releases page](https://github.com/Inder00/OpenSourceSectors/releases), put
the BungeeCord plugin into the BungeeCord Server and configure it, put the Spigot plugin into every Spigot server and
configure the master server (look at BungeeCord configuration for more information) and launch all servers.

# How it works ❓

💻Bungeecord becomes a "master server" which provides configuraction for each Spigot server, but you have to set a
server-name for each Spigot server and the "master server" configuration (ip, port and password - `optional`). Once all
servers are up and running, everything will be ready to use.

# Spigot listeners 🖤

#### PlayerChangeSectorEvent

```java
public class PlayerChangeSectorListener implements Listener {

    @EventHandler
    public void onSectorChange(PlayerChangeSectorEvent event) {
        event.getPlayer(); // Return player that's trying to change sector
        event.getNewSector(); // Return sector, that player is trying to reach
        event.getOldSector(); // Return player's sector
        event.setCancelled(true); // You can cancel this event. Then player can't change sector
    }
}
```

# Contribution ❤️

The most powerful feature of open source projects is developers community❤️. Everyone is welcome and will be written
below 🔥.

- [oskarPLL 🥰](https://github.com/oskarPLL)
- [shitzuu 🥰](https://github.com/shitzuu)

# TODO 📓

- Make a Easy-To-Use API to write compatible plugins with OpenSourceSectors. 📖

**Thanks for your support.❤️**
=======