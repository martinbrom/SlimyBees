package cz.martinbrom.slimybees.core.recipe;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.recipe.GuaranteedRecipe;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;

@ParametersAreNonnullByDefault
public class CustomCraftingOperation implements MachineOperation {

    private final List<ItemStack> ingredients;
    private final List<ItemStack> outputs;

    private final int totalTicks;
    private int currentTicks = 0;

    public CustomCraftingOperation(GuaranteedRecipe recipe) {
        this(recipe.getIngredients(), recipe.getOutputs(), recipe.getDuration());
    }

    public CustomCraftingOperation(List<ItemStack> ingredients, List<ItemStack> outputs, int totalTicks) {
        Validate.notNull(ingredients, "The ingredients cannot be empty or null!");
        Validate.notNull(outputs, "The outputs cannot be null!");
        Validate.isTrue(totalTicks >= 0, "The amount of total ticks must be a positive integer or zero, received: " + totalTicks);

        this.ingredients = ingredients;
        this.outputs = outputs;
        this.totalTicks = totalTicks;
    }

    @Override
    public void addProgress(int num) {
        Validate.isTrue(num > 0, "Progress must be positive!");
        currentTicks += num;
    }

    @Nonnull
    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    @Nonnull
    public List<ItemStack> getOutputs() {
        return outputs;
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
