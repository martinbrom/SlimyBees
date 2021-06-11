package cz.martinbrom.slimybees.core;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

@ParametersAreNonnullByDefault
public class BreedingResultDTO {

    private final ItemStack princess;
    private final ItemStack[] drones;
    private final List<ItemStack> products;
    private final int ticks;

    public BreedingResultDTO(ItemStack princess, ItemStack[] drones, @Nullable List<ItemStack> products, int ticks) {
        Validate.notNull(princess, "Cannot pass a null princess to a BreedingResultDTO!");
        Validate.notNull(princess, "Cannot pass null drones to a BreedingResultDTO!");

        this.princess = princess;
        this.drones = drones;
        this.products = products;
        this.ticks = ticks;
    }

    public ItemStack getPrincess() {
        return princess;
    }

    public ItemStack[] getDrones() {
        return drones;
    }

    @Nullable
    public List<ItemStack> getProducts() {
        return products;
    }

    @Nonnull
    public ItemStack[] getOutput() {
        int productCount = products == null ? 0 : products.size();

        // + 1 for a princess
        int len = 1 + drones.length + productCount;
        ItemStack[] output = new ItemStack[len];
        output[0] = princess;
        int i = 1;
        for (int j = 0; j < drones.length; i++, j++) {
            output[i] = drones[j];
        }

        if (i != len) {
            for (int j = 0; j < productCount; i++, j++) {
                output[i] = products.get(j);
            }
        }

        return output;
    }

    public int getTicks() {
        return ticks;
    }

}
