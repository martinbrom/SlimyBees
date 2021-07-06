package cz.martinbrom.slimybees.core.genetics;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

@ParametersAreNonnullByDefault
public class BreedingResultDTO {

    private final ItemStack princess;
    private final ItemStack[] drones;
    private final List<ItemStack> products;
    private final int ticks;

    public BreedingResultDTO(ItemStack princess, ItemStack[] drones, List<ItemStack> products, int ticks) {
        Validate.notNull(princess, "The princess cannot be null!");
        Validate.notEmpty(drones, "The drones cannot be empty or null!");
        Validate.notNull(products, "The products cannot be null!");
        Validate.isTrue(ticks >= 0, "The amount of ticks must be a positive integer or zero, received: " + ticks);

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

    @Nonnull
    public List<ItemStack> getProducts() {
        return products;
    }

    public int getTicks() {
        return ticks;
    }

}
