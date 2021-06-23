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

    public boolean shouldGet() {
        return ThreadLocalRandom.current().nextDouble() < chance;
    }

}
