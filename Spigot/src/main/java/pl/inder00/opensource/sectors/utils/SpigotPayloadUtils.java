package pl.inder00.opensource.sectors.utils;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.Payload;
import io.rsocket.util.ByteBufPayload;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.basic.ISector;
import pl.inder00.opensource.sectors.protobuf.ProtobufChangeSectorData;
import pl.inder00.opensource.sectors.protobuf.ProtobufGeneric;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.packet.EPacket;
import pl.inder00.opensource.sectors.reflections.Reflection;

public class SpigotPayloadUtils {

    /**
     * Creating a sector change payload
     *
     * @param player A reference of player
     * @param sector A reference of target sector
     * @return Request Payload
     */
    public static Payload createSectorChangePayload(Player player, ISector sector) {

        // create output buffer
        ByteBuf outputBuffer = Unpooled.buffer();
        outputBuffer.writeShort(EPacket.SERVER_CHANGE.getPacketId());

        // build data
        ProtobufChangeSectorData.ChangeSectorPacket changeSectorPacket = ProtobufChangeSectorData.ChangeSectorPacket.newBuilder()
                .setPlayerName(player.getName())
                .setSector(ProtobufGeneric.ProtoSector.newBuilder().setUniqueId(ProtobufUtils.serialize(sector.getUniqueId())).build())
                .build();

        // write bytes to payload
        outputBuffer.writeBytes(changeSectorPacket.toByteArray());

        // return payload
        return ByteBufPayload.create(outputBuffer);

    }

    /**
     * Creating a data transfer payload
     *
     * @param player   A reference of player
     * @param location Location
     * @return Transfer Data Payload
     */
    public static Payload createTransferDataPayload(Player player, Location location) {

        // create output buffer
        ByteBuf outputBuffer = Unpooled.buffer();
        outputBuffer.writeShort(EPacket.DATA_EXCHANGE.getPacketId());

        // nms version
        String nmsVersion = Reflection.getServerVersion();

        // data
        ProtobufTransferData.ProtoPlayerInventory.Builder playerInventoryBuilder = ProtobufTransferData.ProtoPlayerInventory.newBuilder()
                .setInventoryContent(ByteString.copyFrom(ProtobufUtils.serialize(player.getInventory().getContents())))
                .setArmourContent(ByteString.copyFrom(ProtobufUtils.serialize(player.getInventory().getArmorContents())));

        //TO DO: Check if player is from bydgoszcz and if the value is true then don't allow the player to go through the sector and then ban him
        ProtobufTransferData.TransferPacket.Builder transferDataPacketBuilder = ProtobufTransferData.TransferPacket.newBuilder()
                .setPlayerUniqueId(ProtobufUtils.serialize(player.getUniqueId()))
                .setPlayerLocation(ProtobufUtils.serialize(player.getLocation()))
                .setCompassLocation(ProtobufUtils.serialize(player.getCompassTarget()))
                .setPlayerAbilities(ProtobufTransferData.ProtoPlayerAbilities.newBuilder()
                        .setGamemode(player.getGameMode().getValue())
                        .setFlyingAllowed(player.getAllowFlight())
                        .setFlying(player.isFlying())
                        .setFallDistance(player.isDead() ? 0 : player.getFallDistance())
                        .setHealth(player.isDead() ? player.getMaxHealth() : player.getHealth())
                        .setMaxHealth(player.getMaxHealth())
                        .setFoodLevel(player.isDead() ? 20 : player.getFoodLevel())
                        .setFireTicks(player.isDead() ? 0 : player.getFireTicks())
                        .setTotalXP(player.isDead() ? 0 : player.getTotalExperience())
                        .setSaturation(player.isDead() ? 0 : player.getSaturation())
                        .setFlySpeed(player.getFlySpeed())
                        .setWalkSpeed(player.getWalkSpeed())
                        .setExhaustion(player.isDead() ? 0 : player.getExhaustion())
                        .setHeldSlot(player.getInventory().getHeldItemSlot())
                        .build())
                .setEnderchestContent(ByteString.copyFrom(ProtobufUtils.serialize(player.getEnderChest().getContents())))
                .setPotionEffects(ByteString.copyFrom(ProtobufUtils.serialize(player.getActivePotionEffects())));

        // > 1_8
        if (!nmsVersion.contains("v1_8")) {
            playerInventoryBuilder.setExtraContent(ByteString.copyFrom(ProtobufUtils.serialize(player.getInventory().getExtraContents())));
            playerInventoryBuilder.setStorageContent(ByteString.copyFrom(ProtobufUtils.serialize(player.getInventory().getStorageContents())));
        }

        // build inventory
        transferDataPacketBuilder.setPlayerInventory(playerInventoryBuilder.build());

        // write bytes to payload
        outputBuffer.writeBytes(transferDataPacketBuilder.build().toByteArray());

        // return payload
        return ByteBufPayload.create(outputBuffer);

    }

    /**
     * Creating a position data payload
     *
     * @param player   A reference of player
     * @param location Location
     * @return Position Data Payload
     */
    public static Payload createPositionDataPayload(Player player, Location location) {

        // create output buffer
        ByteBuf outputBuffer = Unpooled.buffer();
        outputBuffer.writeShort(EPacket.POSITION_DATA_EXCHANGE.getPacketId());

        // build data
        ProtobufPositionData.PositionPacket positionDataPacket = ProtobufPositionData.PositionPacket.newBuilder()
                .setPlayerUniqueId(ProtobufUtils.serialize(player.getUniqueId()))
                .setPlayerPosition(ProtobufUtils.serialize(location))
                .build();

        // write bytes to payload
        outputBuffer.writeBytes(positionDataPacket.toByteArray());

        // return payload
        return ByteBufPayload.create(outputBuffer);

    }

    /**
     * Creating a data transfer payload
     *
     * @param player A reference of player
     * @return Transfer Data Payload
     */
    public static Payload createTransferDataPayload(Player player) {
        return createTransferDataPayload(player, player.getLocation());
    }

}
