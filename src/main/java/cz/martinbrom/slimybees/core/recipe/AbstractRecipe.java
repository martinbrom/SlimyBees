package cz.martinbrom.slimybees.core.recipe;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

@ParametersAreNonnullByDefault
public abstract class AbstractRecipe {

    private final ItemStack input;

    protected AbstractRecipe(ItemStack input) {
        Validate.notNull(input, "Cannot pass null as the recipe input!");

        this.input = input;
    }

    @Nonnull
    public ItemStack getInput() {
        return input;
    }

    @Nonnull
    public abstract List<ItemStack> getOutputs();

}
