package pl.inder00.opensource.sectors.bungeecord.configuration;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import pl.inder00.opensource.sectors.bungeecord.Sectors;
import pl.inder00.opensource.sectors.bungeecord.basic.ISector;
import pl.inder00.opensource.sectors.bungeecord.basic.impl.SectorImpl;
import pl.inder00.opensource.sectors.bungeecord.configuration.exceptions.ConfigurationException;
import pl.inder00.opensource.sectors.commons.basic.IInternalServer;
import pl.inder00.opensource.sectors.commons.basic.impl.InternalServerImpl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;

public class PluginConfiguration {

    /**
     * Configuration
     */
    private final Sectors sectors;
    /**
     * List of options
     */
    public int sectorChangeCooldown;
    public int protectionDistance;
    public String masterHostname;
    public int masterPort;
    public boolean encryptTraffic;
    private Configuration yamlConfiguration;

    /**
     * Implementation
     *
     * @param configurationFile Configuration java file
     */
    public PluginConfiguration(Sectors sectors, File configurationFile) {

        // set plugin
        this.sectors = sectors;

        // load configuration
        try {
            this.yamlConfiguration = YamlConfiguration.getProvider(YamlConfiguration.class).load(configurationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Load configuration from file
     */
    public void loadConfiguration() {

        // master server configuration
        this.sectorChangeCooldown = this.yamlConfiguration.getInt("sector_change_cooldown", 20);
        this.protectionDistance = this.yamlConfiguration.getInt("protection_distance", 20);
        this.masterHostname = this.yamlConfiguration.getString("masterserver.hostname", "127.0.0.1");
        this.masterPort = this.yamlConfiguration.getInt("masterserver.port", 8180);
        this.encryptTraffic = this.yamlConfiguration.getBoolean("masterserver.encrypt_traffic", true);

        // sectors
        for (String key : this.yamlConfiguration.getSection("sectors").getKeys()) {

            try {

                // sector configuration
                String serverName = this.yamlConfiguration.getString("sectors." + key + ".name");
                String worldName = this.yamlConfiguration.getString("sectors." + key + ".world");
                int minX = this.yamlConfiguration.getInt("sectors." + key + ".minX");
                int minZ = this.yamlConfiguration.getInt("sectors." + key + ".minZ");
                int maxX = this.yamlConfiguration.getInt("sectors." + key + ".maxX");
                int maxZ = this.yamlConfiguration.getInt("sectors." + key + ".maxZ");
                String internalHostname = this.yamlConfiguration.getString("sectors." + key + ".internalserver.hostname");
                int internalPort = this.yamlConfiguration.getInt("sectors." + key + ".internalserver.port");

                // check configuration
                if (serverName == null) throw new ConfigurationException("Server name is not set");
                if (worldName == null) throw new ConfigurationException("World name is not set");
                if (internalHostname == null)
                    throw new ConfigurationException("Internal server hostname name is not set");
                if (internalPort <= 0 || internalPort >= 65536)
                    throw new ConfigurationException("Internal server port is invalid");
                if (minX == maxX) throw new ConfigurationException("Field \"minX\" is same like \"maxX\"");
                if (minZ == maxZ) throw new ConfigurationException("Field \"minZ\" is same like \"maxZ\"");

                // check for config mismatches
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverName);
                if (serverInfo == null) throw new ConfigurationException("Server name is invalid");

                // check for config collisions
                if (Sectors.getSectorManager().getSectorByServerInfo(serverInfo) != null)
                    throw new ConfigurationException("Server name in sector is already using by other sector");
                if (Sectors.getSectorManager().getByInternalServer(internalHostname, internalPort) != null)
                    throw new ConfigurationException("Internal server configuration is colliding with other sector");

                // create sector implementation
                UUID sectorUniqueId = UUID.nameUUIDFromBytes(serverName.getBytes(StandardCharsets.UTF_8));
                IInternalServer internalServer = new InternalServerImpl(internalHostname, internalPort);
                ISector sector = new SectorImpl(sectorUniqueId, serverInfo, internalServer, worldName, minX, minZ, maxX, maxZ);

                // add sector to manager
                Sectors.getSectorManager().save(sector, sectorUniqueId);

                // log
                this.sectors.getLogger().log(Level.INFO, String.format("Successfully registered \"%s\" (%s) sector.", key, serverName));

            } catch (Throwable e) {

                // log
                this.sectors.getLogger().log(Level.SEVERE, String.format("Failed to register \"%s\" sector (%s).", key, e.getMessage()));

                // stop proxy
                ProxyServer.getInstance().stop();

            }


        }
    }

}
