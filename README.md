# OpenSourceSectors 😎

🗒️This is a plugin for [Minecraft Server (Spigot API)](https://spigotmc.org/) introduces a sectors which connects single world across multiple servers.

🍎The reason why I released this plugin is because I would like to make easier combining servers into one to spread out load between them.
👉Finally, I am glad to provide the OpenSourceSectors, and everyone is welcome to adjust the code and help make it better by editting the code, adding new features, etc.

# Requirements 📗
- Bungeecord
- Spigot servers (as many you want)
- Private address space (to internal communication between servers) (eg. 127.0.0.1, 172.16.0.0, etc...)

# Usage ✔️
🎉Download latest stable release from [releases page](https://github.com/Inder00/OpenSourceSectors/releases), put BungeeCord plugin to BungeeCord Server and configure it, put Spigot plugin to each Spigot server and configure master server (look at BungeeCord configuration for more information) and launch all servers.

# How it works ❓
💻Bungeecord becomes "master server" which provides configuraction to each Spigot server, but you have to set server-name per each Spigot server and "master server" configuration (ip, port and password - `optional`). Once all servers are up and running, everything will ready to use.

# Spigot listeners 🖤
#### PlayerChangeSectorEvent
```java
public class PlayerChangeSectorListener implements Listener {

    @EventHandler
    public void onSectorChange(PlayerChangeSectorEvent event) {
        event.getPlayer(); // Return player that's trying to change sector
        event.getSector(); // Return sector, that player is trying to reach
        event.setCancelled(true); // You can cancel this event. Then player can't change sector
    }
}
```

# Contribution ❤️
The most powerful feature of open source projects is developers community❤️. Everyone is welcome and will be written below 🔥.

# TODO 📓
- Make a Easy-To-Use API to write compatible plugins with OpenSourceSectors. 📖
- Support for newer MC versions. 🚩

**Thanks for your support.❤️**
