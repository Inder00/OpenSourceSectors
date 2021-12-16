package pl.inder00.opensource.sectors.spigot.basic.impl;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import pl.inder00.opensource.sectors.protocol.protobuf.TransferPacket;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.spigot.utils.SpigotSerializationUtils;

import java.util.Collection;

public class TransferDataImpl implements IProtobufData<TransferPacket.PlayerTransferPacket, Player> {

    /**
     * Data
     */
    private final TransferPacket.PlayerTransferPacket transferData;

    /**
     * Implementation
     */
    public TransferDataImpl(TransferPacket.PlayerTransferPacket transferData) {
        this.transferData = transferData;
    }

    @Override
    public TransferPacket.PlayerTransferPacket getData() {
        return this.transferData;
    }

    @Override
    public void execute(Player player) {

        // player abilities
        TransferPacket.ProtoPlayerAbilities playerAbilities = this.transferData.getPlayerAbilities();

        // player inventory
        TransferPacket.ProtoPlayerInventory playerInventory = this.transferData.getPlayerInventory();

        // apply data to player
        player.teleport(SpigotSerializationUtils.deserialize(Sectors.getSectorManager().getCurrentSector().getWorld(), this.transferData.getPlayerLocation()), PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setCompassTarget(SpigotSerializationUtils.deserialize(Sectors.getSectorManager().getCurrentSector().getWorld(), this.transferData.getCompassLocation()));
        player.setGameMode(GameMode.getByValue(playerAbilities.getGamemode()));
        player.setAllowFlight(playerAbilities.getFlyingAllowed());
        player.setFlying(playerAbilities.getFlying());
        player.setFallDistance(playerAbilities.getFallDistance());
        player.setMaxHealth(playerAbilities.getMaxHealth());
        player.setHealth(playerAbilities.getHealth());
        player.setFoodLevel(playerAbilities.getFoodLevel());
        player.setFireTicks(playerAbilities.getFireTicks());
        player.setTotalExperience(playerAbilities.getTotalXP());
        player.setSaturation(playerAbilities.getSaturation());
        player.setFlySpeed(playerAbilities.getFlySpeed());
        player.setWalkSpeed(playerAbilities.getWalkSpeed());
        player.setExhaustion(playerAbilities.getExhaustion());
        player.getInventory().setHeldItemSlot(playerAbilities.getHeldSlot());
        player.getInventory().setContents(SpigotSerializationUtils.deserialize(playerInventory.getInventoryContent().toByteArray()));
        player.getInventory().setArmorContents(SpigotSerializationUtils.deserialize(playerInventory.getArmourContent().toByteArray()));
        player.getEnderChest().setContents(SpigotSerializationUtils.deserialize(this.transferData.getEnderchestContent().toByteArray()));
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        SpigotSerializationUtils.<Collection<PotionEffect>>deserialize(this.transferData.getPotionEffects().toByteArray()).forEach(potion -> player.addPotionEffect(potion, true));

        // additional inventory content (> 1.8)
        if (playerInventory.hasExtraContent())
            player.getInventory().setExtraContents(SpigotSerializationUtils.deserialize(playerInventory.getExtraContent().toByteArray()));
        if (playerInventory.hasStorageContent())
            player.getInventory().setStorageContents(SpigotSerializationUtils.deserialize(playerInventory.getStorageContent().toByteArray()));

        // make player damagable
        player.setNoDamageTicks(0);

        // remove data from cache
        Sectors.getTransferDataManager().delete(ProtocolSerializationUtils.deserialize(this.transferData.getPlayerUniqueId()));

    }
}
