package cz.martinbrom.slimybees.core.machine;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.genetics.BreedingResultDTO;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;

@ParametersAreNonnullByDefault
public class BeeBreedingOperation implements MachineOperation {

    private final ItemStack[] parents;
    private final BreedingResultDTO result;
    private final List<ItemStack> products;

    private int currentTicks = 0;

    public BeeBreedingOperation(ItemStack firstParent, ItemStack secondParent, BreedingResultDTO result, List<ItemStack> products) {
        Validate.notNull(firstParent, "The first parent cannot be null!");
        Validate.notNull(secondParent, "The second parent cannot be null!");
        Validate.notNull(result, "The breeding result cannot be null!");
        Validate.notNull(products, "The products cannot be null!");

        this.parents = new ItemStack[] { firstParent, secondParent };
        this.result = result;
        this.products = products;
    }

    @Nonnull
    public ItemStack[] getParents() {
        return parents;
    }

    @Nonnull
    public ItemStack getPrincess() {
        return result.getPrincess();
    }

    @Nonnull
    public ItemStack[] getDrones() {
        return result.getDrones();
    }

    @Nonnull
    public List<ItemStack> getProducts() {
        return products;
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
        return result.getTicks();
    }

}
