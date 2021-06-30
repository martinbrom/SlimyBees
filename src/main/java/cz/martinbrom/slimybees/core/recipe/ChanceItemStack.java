package cz.martinbrom.slimybees.core.recipe;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

@ParametersAreNonnullByDefault
public class ChanceItemStack {

    private final ItemStack item;
    private final double chance;

    public ChanceItemStack(ItemStack item, double chance) {
        Validate.notNull(item, "Cannot add a null item to a ChanceItemStack!");
        Validate.isTrue(chance >= 0 && chance <= 1, "Chance must be between 0 and 100%!");

        this.item = item;
        this.chance = chance;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getChance() {
        return chance;
    }

    /**
     * Performs a random "roll" to get (or not get) a result item.
     * Modifier increases the chance to get the item for numbers greater than 1
     * and lowers the chance for numbers smaller than 1.
     *
     * Example:
     * chance = 0.3 (30 %)
     * modifier = 2
     * final chance = 0.6 (60 %)
     *
     * @param modifier Ratio that modifies the chance of getting the item
     * @return True, if the dice roll results in getting the item
     */
    public boolean shouldGet(double modifier) {
        // TODO: 30.06.21 If modifier * chance > 1, get more?
        return ThreadLocalRandom.current().nextDouble() < modifier * chance;
    }

    public boolean shouldGet() {
        return ThreadLocalRandom.current().nextDouble() < chance;
    }

}
