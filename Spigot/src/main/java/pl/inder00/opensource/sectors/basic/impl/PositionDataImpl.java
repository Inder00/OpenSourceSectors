package pl.inder00.opensource.sectors.basic.impl;

import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.basic.manager.PositionDataManager;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.protobuf.ProtobufPositionData;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.utils.ProtobufUtils;

public class PositionDataImpl implements IProtobufData<ProtobufPositionData.PositionPacket, Player> {

    /**
     * Data
     */
    private ProtobufPositionData.PositionPacket positionData;

    /**
     * Constructor
     */
    public PositionDataImpl(ProtobufPositionData.PositionPacket positionData) {
        this.positionData = positionData;
    }

    @Override
    public ProtobufPositionData.PositionPacket getData() {
        return this.positionData;
    }

    @Override
    public void execute(Player player) {

        // update position
        player.teleport(ProtobufUtils.deserialize(SectorManager.getCurrentSector().getWorld(), this.getData().getPlayerPosition()));

        // remove inventory, enderchest, potions containts
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));

        // set player undamagable
        player.setNoDamageTicks(600);

        // remove data from cache
        PositionDataManager.clearPositionData(this);

    }

}
