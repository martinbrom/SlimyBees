package cz.martinbrom.slimybees.core.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

@ParametersAreNonnullByDefault
public class RandomRecipe extends AbstractRecipe {

    private final List<ChanceItemStack> outputs;

    public RandomRecipe(ItemStack ingredient) {
        super(ingredient);

        outputs = new ArrayList<>();
    }

    public RandomRecipe(List<ItemStack> ingredients) {
        super(ingredients);

        outputs = new ArrayList<>();
    }

    public RandomRecipe addOutput(ItemStack item, double chance) {
        return addOutput(new ChanceItemStack(item, chance));
    }

    public RandomRecipe addOutput(ChanceItemStack item) {
        outputs.add(item);
        return this;
    }

    public RandomRecipe addOutputs(List<ChanceItemStack> items) {
        Validate.notNull(items, "Cannot add null items to a RandomRecipe!");

        for (ChanceItemStack item : items) {
            addOutput(item);
        }

        return this;
    }

    @Nonnull
    public GuaranteedRecipe get() {
        List<ItemStack> out = new ArrayList<>();
        for (ChanceItemStack stack : outputs) {
            if (stack.shouldGet()) {
                out.add(stack.getItem());
            }
        }

        GuaranteedRecipe recipe = new GuaranteedRecipe(ingredients);
        recipe.addOutputs(out);
        recipe.setDuration(duration);

        return recipe;
    }

    @Nonnull
    @Override
    protected AbstractRecipe copy(AbstractRecipe recipe) {
        RandomRecipe newRecipe = new RandomRecipe(recipe.getIngredients());
        return newRecipe.addOutputs(outputs).setDuration(recipe.getDuration());
    }

}
