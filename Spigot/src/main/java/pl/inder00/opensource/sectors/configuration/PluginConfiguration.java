package pl.inder00.opensource.sectors.configuration;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class PluginConfiguration {

    /**
     * List of options
     */
    public String sectorId;
    public String masterHostname;
    public int masterPort;
    public String masterPassword;

    /**
     * Configuration file
     */
    private YamlConfiguration yamlConfiguration;

    /**
     * Implementation
     * @param configurationFile Configuration java file
     */
    public PluginConfiguration(File configurationFile) {

        // load configuration
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(configurationFile);

    }

    /**
     * Load configuration from file
     */
    public void loadConfiguration(){
        this.sectorId = this.yamlConfiguration.getString("serverId");
        this.masterHostname = this.yamlConfiguration.getString("masterserver.hostname");
        this.masterPort = this.yamlConfiguration.getInt("masterserver.port");
        this.masterPassword = this.yamlConfiguration.getString("masterserver.password");
    }

}
