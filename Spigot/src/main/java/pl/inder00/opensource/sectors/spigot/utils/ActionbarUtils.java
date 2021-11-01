package pl.inder00.opensource.sectors.spigot.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.spigot.reflections.Reflection;

import java.lang.reflect.Method;

public class ActionbarUtils {

    /**
     * Send message method introduced in spigot 1_10 which allows send actionbar
     */
    private static Method sendMessageMethod;

    static {
        try {
            sendMessageMethod = Player.Spigot.class.getMethod("sendMessage", ChatMessageType.class, BaseComponent.class);
        } catch (Throwable ignore) {
        }
    }

    /**
     * Sends actionbar packet to player
     *
     * @param player Player
     * @param text   Text
     */
    public static void sendActionbar(Player player, String text) {
        try {

            // nms version
            String nmsVersion = Reflection.getServerVersion();

            // process
            if (sendMessageMethod != null) {
                sendMessageMethod.invoke(player.spigot(), ChatMessageType.ACTION_BAR, new TextComponent(text));
            } else {
                if (nmsVersion.equalsIgnoreCase("v1_8_R1")) {
                    Reflection.sendPacket(player, Reflection.getClass("net.minecraft.server", "PacketPlayOutChat").getConstructor(Reflection.getClass("net.minecraft.server", "IChatBaseComponent"), byte.class).newInstance(Reflection.getClass("net.minecraft.server", "ChatSerializer").getMethod("a", String.class).invoke(null, "{text:\"" + text + "\"}"), (byte) 2), "net.minecraft.server");
                } else {
                    Reflection.sendPacket(player, Reflection.getClass("net.minecraft.server", "PacketPlayOutChat").getConstructor(Reflection.getClass("net.minecraft.server", "IChatBaseComponent"), byte.class).newInstance(Reflection.getClass("net.minecraft.server", "ChatComponentText").getConstructor(new Class<?>[]{String.class}).newInstance(text), (byte) 2), "net.minecraft.server");
                }
            }

        } catch (Throwable e) {

            // throw error
            e.printStackTrace();

        }
    }

}
