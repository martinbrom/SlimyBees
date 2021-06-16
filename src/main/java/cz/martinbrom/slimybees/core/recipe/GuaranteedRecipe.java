package cz.martinbrom.slimybees.core.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

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
    @Override
    public List<ItemStack> getOutputs() {
        return outputs;
    }

}
