package pl.inder00.opensource.sectors.basic;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.UUID;

public interface ITransferData {

    /**
     * Player unique id
     * @return UUIDv3
     */
    UUID getPlayerUniqueId();

    /**
     * Player location
     * @return A reference of location
     */
    Location getLocation();

    /**
     * Player compass location
     * @return Location
     */
    Location getCompassLocation();

    /**
     * Player GameMode
     * @return A reference of GameMode
     */
    GameMode getGameMode();

    /**
     * Is fly allowed?
     * @return boolean
     */
    boolean isAllowFlight();

    /**
     * Is playing flying?
     * @return boolean
     */
    boolean isFlying();

    /**
     * Player fall distance
     * @return float
     */
    float getFallDistance();

    /**
     * Player health
     * @return double
     */
    double getHealth();

    /**
     * Player max health
     * @return double
     */
    double getMaxHealth();

    /**
     * Player food level
     * @return int
     */
    int getFoodLevel();

    /**
     * Player fire ticks
     * @return int
     */
    int getFireTicks();

    /**
     * Player active slot
     * @return int
     */
    int getHeldSlot();

    /**
     * Player total XP
     * @return int
     */
    int getTotalExperience();

    /**
     * Player saturation
     * @return float
     */
    float getSaturation();

    /**
     * Player fly speed
     * @return float
     */
    float getFlySpeed();

    /**
     * Player walk speed
     * @return float
     */
    float getWalkSpeed();

    /**
     * Player exhaustion
     * @return float
     */
    float getExhaustion();

    /**
     * Inventory contents
     * @return Array of ItemStack
     */
    ItemStack[] getInventoryContents();

    /**
     * Armour contents
     * @return Array of ItemStack
     */
    ItemStack[] getArmourContents();

    /**
     * Enderchest contents
     * @return Array of ItemStack
     */
    ItemStack[] getEnderchestContents();

    /**
     * Active potions
     * @return Collection of PotionEffect
     */
    Collection<PotionEffect> getActivePotionEffects();

    /**
     * Apply transfer data to player
     * @param player A reference to player
     */
    void apply(Player player);

}
