package pl.inder00.opensource.sectors.communication;

import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import pl.inder00.opensource.sectors.basic.ITransferData;
import pl.inder00.opensource.sectors.basic.impl.TransferDataImpl;
import pl.inder00.opensource.sectors.basic.manager.SectorManager;
import pl.inder00.opensource.sectors.basic.manager.TransferDataManager;
import pl.inder00.opensource.sectors.protocol.buffer.ByteBufExtension;
import pl.inder00.opensource.sectors.protocol.packet.IPacket;
import pl.inder00.opensource.sectors.utils.SpigotSerializeUtils;

import java.util.Collection;
import java.util.UUID;

public class TransferDataPacket implements IPacket {

    /**
     * Data
     */
    private JavaPlugin plugin;

    /**
     * Implementation
     */
    public TransferDataPacket(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ByteBuf bufferIn, ByteBuf bufferOut) throws Throwable {

        // uuid
        UUID uniqueId = UUID.fromString( ByteBufExtension.readString(bufferIn, 64) );

        // location
        Location location = new Location( SectorManager.getCurrentSector().getWorld(), bufferIn.readDouble(), bufferIn.readDouble(), bufferIn.readDouble(), bufferIn.readFloat(), bufferIn.readFloat());

        // abilities
        GameMode gameMode = GameMode.getByValue( bufferIn.readInt() );
        boolean flyingAllowed = bufferIn.readBoolean();
        boolean flying = bufferIn.readBoolean();

        // misc values
        float fallDistance = bufferIn.readFloat();
        double health = bufferIn.readDouble();
        double maxHealth = bufferIn.readDouble();
        int foodLevel = bufferIn.readInt();
        int fireTicks = bufferIn.readInt();
        int totalXP = bufferIn.readInt();
        float saturation = bufferIn.readFloat();
        float flySpeed = bufferIn.readFloat();
        float walkSpeed = bufferIn.readFloat();
        float exhaustion = bufferIn.readFloat();
        int heldSlot = bufferIn.readInt();

        // compass location
        Location compassLocation = new Location( SectorManager.getCurrentSector().getWorld(), bufferIn.readInt(), bufferIn.readInt(), bufferIn.readInt() );

        // inventory
        ItemStack[] inventoryContent = SpigotSerializeUtils.deserializeBukkitObject( ByteBufExtension.readByteArray( bufferIn ) );
        ItemStack[] armourContent = SpigotSerializeUtils.deserializeBukkitObject( ByteBufExtension.readByteArray( bufferIn ) );
        ItemStack[] enderchestContent = SpigotSerializeUtils.deserializeBukkitObject( ByteBufExtension.readByteArray( bufferIn ) );
        Collection<PotionEffect> potionEffects = SpigotSerializeUtils.deserializeBukkitObject( ByteBufExtension.readByteArray( bufferIn ) );

        // insert data into cache
        ITransferData transferData = new TransferDataImpl(uniqueId, location, compassLocation, gameMode, flyingAllowed, flying, fallDistance, health, maxHealth, foodLevel, fireTicks, totalXP, saturation, flySpeed, walkSpeed, exhaustion, heldSlot, inventoryContent, armourContent, enderchestContent, potionEffects);
        TransferDataManager.cacheTransferData(transferData);

        // check does target player is online
        Player targetPlayer = Bukkit.getPlayer(uniqueId);
        if(targetPlayer != null && targetPlayer.isOnline()){

            // apply transfer data into player
            Bukkit.getServer().getScheduler().runTask(this.plugin, () -> transferData.apply(targetPlayer));

        }

    }
}
