package cz.martinbrom.slimybees.core;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

@ParametersAreNonnullByDefault
public class BreedingResultDTO {

    private final ItemStack princess;
    private final ItemStack[] drones;
    private final int ticks;

    public BreedingResultDTO(ItemStack princess, ItemStack[] drones, int ticks) {
        Validate.notNull(princess, "Cannot pass a null princess to a BreedingResultDTO!");
        Validate.notNull(princess, "Cannot pass null drones to a BreedingResultDTO!");

        this.princess = princess;
        this.drones = drones;
        this.ticks = ticks;
    }

    public ItemStack getPrincess() {
        return princess;
    }

    public ItemStack[] getDrones() {
        return drones;
    }

    @Nonnull
    public ItemStack[] getOutput() {
        ItemStack[] output = new ItemStack[1 + drones.length];

        output[0] = princess;
        for (int i = 0; i < drones.length; i++) {
            output[i + 1] = drones[0];
        }

        return output;
    }

    public int getTicks() {
        return ticks;
    }

}
