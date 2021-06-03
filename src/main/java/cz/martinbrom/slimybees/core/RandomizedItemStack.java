package cz.martinbrom.slimybees.core;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

// TODO: 17.05.21 Javadoc
public class RandomizedItemStack {

    private final ItemStack itemStack;
    private final int minCount;
    private final int maxCount;

    // TODO: 17.05.21 Javadoc
    public RandomizedItemStack(ItemStack itemStack, int maxCount) {
        this(itemStack, 0, maxCount);
    }

    // TODO: 17.05.21 Javadoc
    public RandomizedItemStack(ItemStack itemStack, int minCount, int maxCount) {
        Validate.notNull(itemStack, "The itemStack must not be null!");
        Validate.isTrue(maxCount > minCount, "The maxCount must be greater than the minCount");
        Validate.isTrue(maxCount <= 64, "The maxCount must be less than or exactly 64");

        this.itemStack = itemStack;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

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

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

}
