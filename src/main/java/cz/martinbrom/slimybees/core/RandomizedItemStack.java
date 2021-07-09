package cz.martinbrom.slimybees.core;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

/**
 * This class represents an {@link ItemStack} with a minimum and maximum count.
 * It then uses a uniform distribution to decide, how many items should be dropped/given.
 */
@ParametersAreNonnullByDefault
public class RandomizedItemStack {

    private final ItemStack itemStack;
    private final int minCount;
    private final int maxCount;

    /**
     * Creates a new instance from given {@link ItemStack} and min & max count.
     *
     * @param itemStack The {@link ItemStack} to get
     * @param minCount Minimum amount of items to get (inclusive)
     * @param maxCount Maximum amount of items to get (inclusive)
     */
    public RandomizedItemStack(ItemStack itemStack, int minCount, int maxCount) {
        Validate.notNull(itemStack, "The itemStack must not be null!");
        Validate.isTrue(maxCount > minCount, "The maxCount must be greater than the minCount");
        Validate.isTrue(maxCount <= itemStack.getMaxStackSize(), "The maxCount must be less than or exactly " + itemStack.getMaxStackSize());

        this.itemStack = itemStack;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    /**
     * Gets the {@link ItemStack} with randomly chosen amount between min and max attributes.
     *
     * @return The {@link ItemStack} with random amount (between min and max)
     */
    @Nullable
    public ItemStack getRandom() {
        int count = ThreadLocalRandom.current().nextInt(minCount, maxCount + 1);

        if (count == 0) {
            return null;
        }

        ItemStack copy = itemStack.clone();
        copy.setAmount(count);
        return copy;
    }

    /**
     * Returns the {@link ItemStack} this {@link RandomizedItemStack} holds.
     *
     * @return The {@link ItemStack} this holds
     */
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

}
