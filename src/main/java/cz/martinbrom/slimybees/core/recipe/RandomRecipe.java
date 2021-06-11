package cz.martinbrom.slimybees.core.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.ChanceItemStack;

@ParametersAreNonnullByDefault
public class RandomRecipe extends AbstractRecipe {

    private final List<ChanceItemStack> outputs;

    public RandomRecipe(ItemStack input) {
        super(input);

        outputs = new ArrayList<>();
    }

    public RandomRecipe addOutput(ItemStack item, double chance) {
        return addOutput(new ChanceItemStack(item, chance));
    }

    public RandomRecipe addOutput(ChanceItemStack item) {
        outputs.add(item);
        return this;
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs() {
        List<ItemStack> out = new ArrayList<>();
        for (ChanceItemStack stack : outputs) {
            if (stack.shouldGet()) {
                out.add(stack.getItem());
            }
        }

        return out;
    }

}
