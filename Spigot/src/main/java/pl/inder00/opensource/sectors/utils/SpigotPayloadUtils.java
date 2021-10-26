package pl.inder00.opensource.sectors.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.Payload;
import io.rsocket.util.ByteBufPayload;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.protocol.buffer.ByteBufExtension;
import pl.inder00.opensource.sectors.protocol.packet.EPacket;

public class SpigotPayloadUtils {

    /**
     * Creating a sector change payload
     * @param player A reference of player
     * @param sector A reference of target sector
     * @return Request Payload
     */
    public static Payload createSectorChangePayload(Player player, ISector sector){

        // create output buffer
        ByteBuf outputBuffer = Unpooled.buffer();
        outputBuffer.writeShort(EPacket.SERVER_CHANGE.getPacketId());

        // write data
        ByteBufExtension.writeString(outputBuffer,player.getName());
        ByteBufExtension.writeString(outputBuffer,sector.getUniqueId().toString());


        // return payload
        return ByteBufPayload.create(outputBuffer);

    }

    /**
     * Creating a data transfer payload
     * @param player A reference of player
     * @param location Location
     * @return Transfer Data Payload
     */
    public static Payload createTransferDataPayload(Player player, Location location){

        // create output buffer
        ByteBuf outputBuffer = Unpooled.buffer();
        outputBuffer.writeShort(EPacket.DATA_EXCHANGE.getPacketId());

        // uuid
        ByteBufExtension.writeString(outputBuffer,player.getUniqueId().toString());

        // location
        outputBuffer.writeDouble(location.getX());
        outputBuffer.writeDouble(location.getY());
        outputBuffer.writeDouble(location.getZ());
        outputBuffer.writeFloat(location.getYaw());
        outputBuffer.writeFloat(location.getPitch());

        // abilities
        outputBuffer.writeInt(player.getGameMode().getValue());
        outputBuffer.writeBoolean(player.getAllowFlight());
        outputBuffer.writeBoolean(player.isFlying());

        // misc values
        outputBuffer.writeFloat(player.isDead() ? 0 : player.getFallDistance());
        outputBuffer.writeDouble(player.isDead() ? player.getMaxHealth() : player.getHealth());
        outputBuffer.writeDouble(player.getMaxHealth());
        outputBuffer.writeInt(player.isDead() ? 20 : player.getFoodLevel());
        outputBuffer.writeInt(player.isDead() ? 0 : player.getFireTicks());
        outputBuffer.writeInt(player.isDead() ? 0 : player.getTotalExperience());
        outputBuffer.writeFloat(player.isDead() ? 0 : player.getSaturation());
        outputBuffer.writeFloat(player.getFlySpeed());
        outputBuffer.writeFloat(player.getWalkSpeed());
        outputBuffer.writeFloat(player.isDead() ? 0 : player.getExhaustion());
        outputBuffer.writeInt(player.getInventory().getHeldItemSlot());

        // compass target
        outputBuffer.writeInt(player.getCompassTarget().getBlockX());
        outputBuffer.writeInt(player.getCompassTarget().getBlockY());
        outputBuffer.writeInt(player.getCompassTarget().getBlockZ());

        // inventory
        ByteBufExtension.writeByteArray(outputBuffer, SpigotSerializeUtils.serializeBukkitObject(player.getInventory().getContents()));

        // armour
        ByteBufExtension.writeByteArray(outputBuffer, SpigotSerializeUtils.serializeBukkitObject(player.getInventory().getArmorContents()));

        // enderchest
        ByteBufExtension.writeByteArray(outputBuffer, SpigotSerializeUtils.serializeBukkitObject(player.getEnderChest().getContents()));

        // effects
        ByteBufExtension.writeByteArray(outputBuffer, SpigotSerializeUtils.serializeBukkitObject(player.getActivePotionEffects()));

        // return payload
        return ByteBufPayload.create(outputBuffer);

    }

    /**
     * Creating a position data payload
     * @param player A reference of player
     * @param location Location
     * @return Position Data Payload
     */
    public static Payload createPositionDataPayload(Player player, Location location){

        // create output buffer
        ByteBuf outputBuffer = Unpooled.buffer();
        outputBuffer.writeShort(EPacket.POSITION_DATA_EXCHANGE.getPacketId());

        // uuid
        ByteBufExtension.writeString(outputBuffer,player.getUniqueId().toString());

        // location
        outputBuffer.writeDouble(location.getX());
        outputBuffer.writeDouble(location.getY());
        outputBuffer.writeDouble(location.getZ());
        outputBuffer.writeFloat(location.getYaw());
        outputBuffer.writeFloat(location.getPitch());

        // return payload
        return ByteBufPayload.create(outputBuffer);

    }

    /**
     * Creating a data transfer payload
     * @param player A reference of player
     * @return Transfer Data Payload
     */
    public static Payload createTransferDataPayload(Player player){
        return createTransferDataPayload(player, player.getLocation());
    }

}
