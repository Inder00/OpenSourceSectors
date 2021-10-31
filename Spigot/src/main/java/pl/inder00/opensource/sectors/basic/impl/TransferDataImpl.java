package pl.inder00.opensource.sectors.basic.impl;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.protobuf.ProtobufTransferData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.utils.ProtobufUtils;

import java.util.Collection;

public class TransferDataImpl implements IProtobufData<ProtobufTransferData.TransferPacket, Player> {

    /**
     * Data
     */
    private final Sectors sectors;
    private final ProtobufTransferData.TransferPacket transferData;

    /**
     * Implementation
     */
    public TransferDataImpl(Sectors sectors, ProtobufTransferData.TransferPacket transferData) {
        this.sectors = sectors;
        this.transferData = transferData;
    }

    @Override
    public ProtobufTransferData.TransferPacket getData() {
        return this.transferData;
    }

    @Override
    public void execute(Player player) {

        // player abilities
        ProtobufTransferData.ProtoPlayerAbilities playerAbilities = this.transferData.getPlayerAbilities();

        // player inventory
        ProtobufTransferData.ProtoPlayerInventory playerInventory = this.transferData.getPlayerInventory();

        // apply data to player
        player.teleport(ProtobufUtils.deserialize(this.sectors.sectorManager.getCurrentSector().getWorld(), this.transferData.getPlayerLocation()), PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setCompassTarget(ProtobufUtils.deserialize(this.sectors.sectorManager.getCurrentSector().getWorld(), this.transferData.getCompassLocation()));
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
        player.getInventory().setContents(ProtobufUtils.deserialize(playerInventory.getInventoryContent().toByteArray()));
        player.getInventory().setArmorContents(ProtobufUtils.deserialize(playerInventory.getArmourContent().toByteArray()));
        player.getEnderChest().setContents(ProtobufUtils.deserialize(this.transferData.getEnderchestContent().toByteArray()));
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        ProtobufUtils.<Collection<PotionEffect>>deserialize(this.transferData.getPotionEffects().toByteArray()).forEach(potion -> player.addPotionEffect(potion, true));

        // additional inventory content (> 1.8)
        if (playerInventory.hasExtraContent())
            player.getInventory().setExtraContents(ProtobufUtils.deserialize(playerInventory.getExtraContent().toByteArray()));
        if (playerInventory.hasStorageContent())
            player.getInventory().setStorageContents(ProtobufUtils.deserialize(playerInventory.getStorageContent().toByteArray()));

        // make player damagable
        player.setNoDamageTicks(0);

        // remove data from cache
        this.sectors.transferDataManager.delete(ProtobufUtils.deserialize(this.transferData.getPlayerUniqueId()));

    }
}
