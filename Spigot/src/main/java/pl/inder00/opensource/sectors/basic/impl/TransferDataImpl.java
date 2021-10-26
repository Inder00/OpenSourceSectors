package pl.inder00.opensource.sectors.basic.impl;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pl.inder00.opensource.sectors.basic.ITransferData;
import pl.inder00.opensource.sectors.basic.manager.TransferDataManager;

import java.util.Collection;
import java.util.UUID;

public class TransferDataImpl implements ITransferData {

    /**
     * Data
     */
    private UUID uniqueId;
    private Location location;
    private Location compassLocation;
    private GameMode gameMode;
    private boolean flightAllowed;
    private boolean flying;
    private float fallDistance;
    private double health;
    private double maxHealth;
    private int foodLevel;
    private int fireTicks;
    private int totalXP;
    private float saturation;
    private float flySpeed;
    private float walkSpeed;
    private float exhaustion;
    private int heldSlot;
    private ItemStack[] inventory;
    private ItemStack[] armour;
    private ItemStack[] enderchest;
    private Collection<PotionEffect> potionEffects;

    /**
     * Implementation
     */
    public TransferDataImpl(UUID uniqueId, World world, int locationX, int locationY, int locationZ, float locationYaw, float locationPitch, int compassX, int compassY, int compassZ, float compassYaw, float compassPitch, GameMode gameMode, boolean flightAllowed, boolean flying, float fallDistance, double health, double maxHealth, int foodLevel, int fireTicks, int totalXP, float saturation, float flySpeed, float walkSpeed, float exhaustion, int heldSlot, ItemStack[] inventory, ItemStack[] armour, ItemStack[] enderchest, Collection<PotionEffect> potionEffects) {
        this(uniqueId,new Location(world,locationX,locationY,locationZ,locationYaw,locationPitch),new Location(world,compassX,compassY,compassZ,compassYaw,compassPitch),gameMode,flightAllowed,flying,fallDistance,health,maxHealth,foodLevel,fireTicks,totalXP,saturation,flySpeed,walkSpeed,exhaustion,heldSlot,inventory,armour,enderchest,potionEffects);
    }

    /**
     * Implementation
     */
    public TransferDataImpl(UUID uniqueId, Location location, Location compassLocation, GameMode gameMode, boolean flightAllowed, boolean flying, float fallDistance, double health, double maxHealth, int foodLevel, int fireTicks, int totalXP, float saturation, float flySpeed, float walkSpeed, float exhaustion, int heldSlot, ItemStack[] inventory, ItemStack[] armour, ItemStack[] enderchest, Collection<PotionEffect> potionEffects) {
        this.uniqueId = uniqueId;
        this.location = location;
        this.compassLocation = compassLocation;
        this.gameMode = gameMode;
        this.flightAllowed = flightAllowed;
        this.flying = flying;
        this.fallDistance = fallDistance;
        this.health = health;
        this.maxHealth = maxHealth;
        this.foodLevel = foodLevel;
        this.fireTicks = fireTicks;
        this.totalXP = totalXP;
        this.saturation = saturation;
        this.flySpeed = flySpeed;
        this.walkSpeed = walkSpeed;
        this.exhaustion = exhaustion;
        this.heldSlot = heldSlot;
        this.inventory = inventory;
        this.armour = armour;
        this.enderchest = enderchest;
        this.potionEffects = potionEffects;
    }

    @Override
    public UUID getPlayerUniqueId() {
        return this.uniqueId;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public Location getCompassLocation() {
        return this.compassLocation;
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public boolean isAllowFlight() {
        return this.flightAllowed;
    }

    @Override
    public boolean isFlying() {
        return this.flying;
    }

    @Override
    public float getFallDistance() {
        return this.fallDistance;
    }

    @Override
    public double getHealth() {
        return this.health;
    }

    @Override
    public double getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public int getFoodLevel() {
        return this.foodLevel;
    }

    @Override
    public int getFireTicks() {
        return this.fireTicks;
    }

    @Override
    public int getTotalExperience() {
        return this.totalXP;
    }

    @Override
    public float getSaturation() {
        return this.saturation;
    }

    @Override
    public float getFlySpeed() {
        return this.flySpeed;
    }

    @Override
    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    @Override
    public float getExhaustion() {
        return this.exhaustion;
    }

    @Override
    public int getHeldSlot() {
        return this.heldSlot;
    }

    @Override
    public ItemStack[] getInventoryContents() {
        return this.inventory;
    }

    @Override
    public ItemStack[] getArmourContents() {
        return this.armour;
    }

    @Override
    public ItemStack[] getEnderchestContents() {
        return this.enderchest;
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return this.potionEffects;
    }

    @Override
    public void apply(Player player) {

        // apply data to player
        player.teleport( this.location, PlayerTeleportEvent.TeleportCause.PLUGIN );
        player.setCompassTarget( this.compassLocation );
        player.setGameMode( this.gameMode );
        player.setAllowFlight( this.flightAllowed );
        player.setFlying( this.flying );
        player.setFallDistance( this.fallDistance );
        player.setMaxHealth( this.maxHealth );
        player.setHealth( this.health );
        player.setFoodLevel( this.foodLevel );
        player.setFireTicks( this.fireTicks );
        player.setTotalExperience( this.totalXP );
        player.setSaturation( this.saturation );
        player.setFlySpeed( this.flySpeed );
        player.setWalkSpeed( this.walkSpeed );
        player.setExhaustion( this.exhaustion );
        player.getInventory().setHeldItemSlot( this.heldSlot );
        player.getInventory().setContents( this.inventory );
        player.getInventory().setArmorContents( this.armour );
        player.getEnderChest().setContents( this.enderchest );
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        this.potionEffects.forEach(potion -> player.addPotionEffect(potion, true));

        // make player damagable
        player.setNoDamageTicks( 0 );

        // remove data from cache
        TransferDataManager.clearTransferData( this );

    }
}
