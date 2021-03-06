package pl.inder00.opensource.sectors.spigot.basic.impl;

import org.bukkit.entity.Player;
import pl.inder00.opensource.sectors.protocol.IProtobufData;
import pl.inder00.opensource.sectors.protocol.protobuf.PositionPacket;
import pl.inder00.opensource.sectors.protocol.utils.ProtocolSerializationUtils;
import pl.inder00.opensource.sectors.spigot.Sectors;
import pl.inder00.opensource.sectors.spigot.utils.SpigotSerializationUtils;

public class PositionDataImpl implements IProtobufData<PositionPacket.PlayerPositionPacket, Player> {

    /**
     * Data
     */
    private final PositionPacket.PlayerPositionPacket positionData;

    /**
     * Constructor
     */
    public PositionDataImpl(PositionPacket.PlayerPositionPacket positionData) {
        this.positionData = positionData;
    }

    @Override
    public PositionPacket.PlayerPositionPacket getData() {
        return this.positionData;
    }

    @Override
    public void execute(Player player) {

        // update position
        player.teleport(SpigotSerializationUtils.deserialize(Sectors.getSectorManager().getCurrentSector().getWorld(), this.getData().getPlayerPosition()));

        // remove inventory, enderchest, potions containts
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));

        // set player undamagable
        player.setNoDamageTicks(600);

        // remove data from cache
        Sectors.getPositionDataManager().delete(ProtocolSerializationUtils.deserialize(this.getData().getPlayerUniqueId()));

    }

}
