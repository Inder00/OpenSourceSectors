package pl.inder00.opensource.sectors.spigot.reflections;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class Reflection {

    /**
     * Data
     */
    private static final String serverNmsVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";

    /**
     * Reurning a nms class
     *
     * @param prefix Part of string before nms version tag
     * @param suffix Part of string after nms version tag
     * @return Class
     */
    public static Class<?> getClass(String prefix, String suffix) {
        String name = prefix + "." + serverNmsVersion + suffix;
        Class<?> nmsClass;
        try {
            nmsClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return nmsClass;
    }

    /**
     * Send nms packet to player
     *
     * @param player Player
     * @param packet Packet
     * @param prefix Part of string before nms version tag of packet class
     */
    public static <T> void sendPacket(Player player, T packet, String prefix) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, ClassNotFoundException {
        Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object plrConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
        plrConnection.getClass().getMethod("sendPacket", getClass(prefix, "Packet")).invoke(plrConnection, packet);
    }

    /**
     * Gets a nms server version
     *
     * @return String
     */
    public static String getServerVersion() {
        return serverNmsVersion;
    }

}
