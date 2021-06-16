package cz.martinbrom.slimybees.core.recipe;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

@ParametersAreNonnullByDefault
public abstract class AbstractRecipe {

    private final List<ItemStack> ingredients;
    private int duration;

    protected AbstractRecipe(ItemStack ingredient) {
        this(Collections.singletonList(ingredient));
    }

    protected AbstractRecipe(List<ItemStack> ingredients) {
        this(ingredients, -1);
    }

    protected AbstractRecipe(List<ItemStack> ingredients, int duration) {
        Validate.notEmpty(ingredients, "The recipe ingredients cannot be empty or null!");

        this.ingredients = ingredients;
        this.duration = duration;
    }

    @Nonnull
    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    @Nonnull
    public ItemStack[] getIngredientArray() {
        return ingredients.toArray(new ItemStack[0]);
    }

    public boolean isInstant() {
        return duration <= 0;
    }

    public int getDuration() {
        return duration;
    }

    @Nonnull
    public AbstractRecipe setDuration(int duration) {
        this.duration = duration;

        return this;
    }

    @Nonnull
    public abstract List<ItemStack> getOutputs();

}
