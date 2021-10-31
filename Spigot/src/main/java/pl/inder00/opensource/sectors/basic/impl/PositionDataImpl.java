package pl.inder00.opensource.sectors.basic.impl;

import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.Sectors;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.utils.ProtobufUtils;

public class PositionDataImpl implements IProtobufData<ProtobufPositionData.PositionPacket, Player> {

    /**
     * Data
     */
    private final Sectors sectors;
    private final ProtobufPositionData.PositionPacket positionData;

    /**
     * Constructor
     */
    public PositionDataImpl(Sectors sectors, ProtobufPositionData.PositionPacket positionData) {
        this.sectors = sectors;
        this.positionData = positionData;
    }

    @Override
    public ProtobufPositionData.PositionPacket getData() {
        return this.positionData;
    }

    @Override
    public void execute(Player player) {

        // update position
        player.teleport(ProtobufUtils.deserialize(this.sectors.sectorManager.getCurrentSector().getWorld(), this.getData().getPlayerPosition()));

        // remove inventory, enderchest, potions containts
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));

        // set player undamagable
        player.setNoDamageTicks(600);

        // remove data from cache
        this.sectors.positionDataManager.delete(ProtobufUtils.deserialize(this.getData().getPlayerUniqueId()));

    }

}
