package cz.martinbrom.slimybees.core.genetics;

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
        Validate.notNull(princess, "The princess cannot be null!");
        Validate.notEmpty(drones, "The drones cannot be empty or null!");
        Validate.isTrue(ticks >= 0, "The amount of ticks must be a positive integer or zero, received: " + ticks);

        this.princess = princess;
        this.drones = drones;
        this.ticks = ticks;
    }

    @Nonnull
    public ItemStack getPrincess() {
        return princess;
    }

    @Nonnull
    public ItemStack[] getDrones() {
        return drones;
    }

    public int getTicks() {
        return ticks;
    }

}
