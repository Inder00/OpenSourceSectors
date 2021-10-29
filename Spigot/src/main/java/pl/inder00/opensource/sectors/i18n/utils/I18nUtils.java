package pl.inder00.opensource.sectors.i18n.utils;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class I18nUtils {

    /**
     * Locale methods
     */
    private static Method playerLocaleMethod;
    private static Method spigotLocaleMethod;

    static {
        try {
            playerLocaleMethod = Player.class.getMethod("getLocale");
        } catch (Throwable e) {
            try {
                spigotLocaleMethod = Player.Spigot.class.getMethod("getLocale");
            } catch (NoSuchMethodException ignore) {
            }
        }
    }

    /**
     * Gets player locale
     *
     * @param player Player
     * @return String
     */
    public static String getPlayerLocale(Player player) {
        try {
            if (playerLocaleMethod != null) {
                return (String) playerLocaleMethod.invoke(player);
            } else {
                return (String) spigotLocaleMethod.invoke(player.spigot());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
