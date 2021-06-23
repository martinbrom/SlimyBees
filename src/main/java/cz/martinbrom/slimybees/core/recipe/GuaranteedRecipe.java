package cz.martinbrom.slimybees.core.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

@ParametersAreNonnullByDefault
public class GuaranteedRecipe extends AbstractRecipe {

    private final List<ItemStack> outputs = new ArrayList<>();

    public GuaranteedRecipe(ItemStack input) {
        super(input);
    }

    public GuaranteedRecipe(List<ItemStack> ingredients) {
        super(ingredients);
    }

    public GuaranteedRecipe addOutput(ItemStack item) {
        Validate.notNull(item, "Cannot add a null item to a GuaranteedRecipe!");

        outputs.add(item);
        return this;
    }

    public GuaranteedRecipe addOutputs(ItemStack[] items) {
        Validate.notNull(items, "Cannot add null items to a GuaranteedRecipe!");

        for (ItemStack item : items) {
            addOutput(item);
        }

        return this;
    }

    public GuaranteedRecipe addOutputs(List<ItemStack> items) {
        Validate.notNull(items, "Cannot add null items to a GuaranteedRecipe!");

        for (ItemStack item : items) {
            addOutput(item);
        }

        return this;
    }

    @Nonnull
    public List<ItemStack> getOutputs() {
        return outputs;
    }

    @Nonnull
    @Override
    protected AbstractRecipe copy(AbstractRecipe recipe) {
        GuaranteedRecipe newRecipe = new GuaranteedRecipe(recipe.getIngredients());
        return newRecipe.addOutputs(outputs).setDuration(recipe.getDuration());
    }

}
