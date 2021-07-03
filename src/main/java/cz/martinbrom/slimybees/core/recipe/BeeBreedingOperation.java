package cz.martinbrom.slimybees.core.recipe;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;

@ParametersAreNonnullByDefault
public class BeeBreedingOperation implements MachineOperation {

    private final ItemStack[] parents;
    private final ItemStack princess;
    private final ItemStack[] drones;

    private final int totalTicks;
    private int currentTicks = 0;

    public BeeBreedingOperation(ItemStack firstParent, ItemStack secondParent, ItemStack princess, ItemStack[] drones, int totalTicks) {
        Validate.notNull(firstParent, "The first parent cannot be null!");
        Validate.notNull(secondParent, "The second parent cannot be null!");
        Validate.notNull(princess, "The princess cannot be null!");
        Validate.notEmpty(drones, "The drones cannot be empty or null!");
        Validate.isTrue(totalTicks >= 0, "The amount of total ticks must be a positive integer or zero, received: " + totalTicks);

        this.parents = new ItemStack[] {firstParent, secondParent};
        this.princess = princess;
        this.drones = drones;
        this.totalTicks = totalTicks;
    }

    @Nonnull
    public ItemStack[] getParents() {
        return parents;
    }

    @Nonnull
    public ItemStack getPrincess() {
        return princess;
    }

    @Nonnull
    public ItemStack[] getDrones() {
        return drones;
    }

    @Override
    public void addProgress(int num) {
        Validate.isTrue(num > 0, "Progress must be positive!");
        currentTicks += num;
    }

    @Override
    public int getProgress() {
        return currentTicks;
    }

    @Override
    public int getTotalTicks() {
        return totalTicks;
    }

}
