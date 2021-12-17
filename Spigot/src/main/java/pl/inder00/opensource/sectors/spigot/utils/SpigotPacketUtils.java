package pl.inder00.opensource.sectors.spigot.utils;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.protocol.protobuf.PositionPacket;
import pl.inder00.opensource.sectors.protocol.protobuf.ProtobufGeneric;
import pl.inder00.opensource.sectors.protocol.protobuf.ServerPacket;
import pl.inder00.opensource.sectors.protocol.protobuf.TransferPacket;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;
import pl.inder00.opensource.sectors.spigot.basic.ISector;
import pl.inder00.opensource.sectors.spigot.reflections.Reflection;

public class SpigotPacketUtils {

    /**
     * Creating a sector change packet
     *
     * @param player A reference of player
     * @param sector A reference of target sector
     * @return MessageLite
     */
    public static MessageLite createChangeServerPacket(Player player, ISector sector) {

        // build data
        ServerPacket.ChangeServerPacket changeSectorPacket = ServerPacket.ChangeServerPacket.newBuilder()
                .setPlayerName(player.getName())
                .setSector(ProtobufGeneric.ProtoSector.newBuilder().setUniqueId(ProtocolSerializationUtils.serialize(sector.getUniqueId())).build())
                .build();

        // return packet
        return changeSectorPacket;

    }

    /**
     * Creating a transfer packet
     *
     * @param player   A reference of player
     * @param location Location
     * @return MessageLite
     */
    public static MessageLite createPlayerTransferPacket(Player player, Location location) {

        // nms version
        String nmsVersion = Reflection.getServerVersion();

        // data
        TransferPacket.ProtoPlayerInventory.Builder playerInventoryBuilder = TransferPacket.ProtoPlayerInventory.newBuilder()
                .setInventoryContent(ByteString.copyFrom(SpigotSerializationUtils.serialize(player.getInventory().getContents())))
                .setArmourContent(ByteString.copyFrom(SpigotSerializationUtils.serialize(player.getInventory().getArmorContents())));
        TransferPacket.PlayerTransferPacket.Builder transferDataPacketBuilder = TransferPacket.PlayerTransferPacket.newBuilder()
                .setPlayerUniqueId(ProtocolSerializationUtils.serialize(player.getUniqueId()))
                .setPlayerLocation(SpigotSerializationUtils.serialize(player.getLocation()))
                .setCompassLocation(SpigotSerializationUtils.serialize(player.getCompassTarget()))
                .setPlayerAbilities(TransferPacket.ProtoPlayerAbilities.newBuilder()
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
                .setEnderchestContent(ByteString.copyFrom(SpigotSerializationUtils.serialize(player.getEnderChest().getContents())))
                .setPotionEffects(ByteString.copyFrom(SpigotSerializationUtils.serialize(player.getActivePotionEffects())));

        // > 1_8
        if (!nmsVersion.contains("v1_8")) {
            playerInventoryBuilder.setExtraContent(ByteString.copyFrom(SpigotSerializationUtils.serialize(player.getInventory().getExtraContents())));
            playerInventoryBuilder.setStorageContent(ByteString.copyFrom(SpigotSerializationUtils.serialize(player.getInventory().getStorageContents())));
        }

        // build inventory
        transferDataPacketBuilder.setPlayerInventory(playerInventoryBuilder.build());

        // return payload
        return transferDataPacketBuilder.build();

    }

    /**
     * Creating a position packet
     *
     * @param player   A reference of player
     * @param location Location
     * @return MessageLite
     */
    public static MessageLite createPlayerPositionPacket(Player player, Location location) {

        // build data
        PositionPacket.PlayerPositionPacket positionDataPacket = PositionPacket.PlayerPositionPacket.newBuilder()
                .setPlayerUniqueId(ProtocolSerializationUtils.serialize(player.getUniqueId()))
                .setPlayerPosition(SpigotSerializationUtils.serialize(location))
                .build();

        // return payload
        return positionDataPacket;

    }

    /**
     * Creating a transfer packet
     *
     * @param player A reference of player
     * @return MessageLite
     */
    public static MessageLite createPlayerTransferPacket(Player player) {
        return createPlayerTransferPacket(player, player.getLocation());
    }

}
