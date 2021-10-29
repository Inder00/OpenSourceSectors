package pl.inder00.opensource.sectors.configuration;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import pl.inder00.opensource.sectors.configuration.exceptions.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class MessagesConfiguration {

    /**
     * List of messages
     */
    public String defaultLocale;
    public HashMap<String, String> localeAliases = new HashMap<String, String>();
    public HashMap<String, String> messagesList = new HashMap<String, String>();

    /**
     * Configuration
     */
    private Plugin plugin;
    private Configuration yamlConfiguration;

    /**
     * Implementation
     *
     * @param configurationFile Configuration java file
     */
    public MessagesConfiguration(Plugin plugin, File configurationFile) {

        // set plugin
        this.plugin = plugin;

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
    public void loadConfiguration() throws Throwable {

        // default locale
        this.defaultLocale = this.yamlConfiguration.getString("default_locale", "en_us").toLowerCase();

        // locale aliases
        for (String targetLocale : this.yamlConfiguration.getSection("locale_alises").getKeys()) {

            try {

                // source locale
                String sourceLocale = this.yamlConfiguration.getString("locale_alises." + targetLocale);
                if (sourceLocale == null) throw new ConfigurationException("Source locale is not set");

                // append to list
                localeAliases.put(targetLocale.toLowerCase(), sourceLocale.toLowerCase());

            } catch (Throwable e) {

                // log
                this.plugin.getLogger().log(Level.SEVERE, String.format("Failed to load \"%s\" locale alias (%s).", targetLocale, e.getMessage()));

                // stop proxy
                ProxyServer.getInstance().stop();

            }

        }

        // locales
        for (String locale : this.yamlConfiguration.getSection("messages").getKeys()) {

            try {

                // localized messages
                for (String messageKey : this.yamlConfiguration.getSection("messages." + locale).getKeys()) {

                    // message
                    String message = this.yamlConfiguration.getString("messages." + locale + "." + messageKey);
                    if (message == null)
                        throw new ConfigurationException(String.format("Message is not set in \"%s\" key", messageKey));

                    // append  to list
                    messagesList.put(locale.toLowerCase() + "." + messageKey.toLowerCase().replaceAll("_", "."), message);

                }

            } catch (Throwable e) {

                // log
                this.plugin.getLogger().log(Level.SEVERE, String.format("Failed to load \"%s\" locale messages (%s).", locale, e.getMessage()));

                // stop proxy
                ProxyServer.getInstance().stop();

            }

        }

    }

}
