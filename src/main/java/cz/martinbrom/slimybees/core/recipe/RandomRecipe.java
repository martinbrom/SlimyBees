package cz.martinbrom.slimybees.core.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

@ParametersAreNonnullByDefault
public class RandomRecipe extends AbstractRecipe {

    private final List<Pair<ItemStack, Double>> outputs;

    public RandomRecipe(ItemStack input) {
        super(input);

        outputs = new ArrayList<>();
    }

    public RandomRecipe addOutput(ItemStack item, double chance) {
        Validate.notNull(item, "Cannot add a null item to a RandomRecipe!");
        Validate.isTrue(chance >= 0 && chance <= 1, "Chance must be between 0 and 100%!");

        outputs.add(new Pair<>(item, chance));
        return this;
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs() {
        List<ItemStack> out = new ArrayList<>();
        for (Pair<ItemStack, Double> pair : outputs) {
            if (ThreadLocalRandom.current().nextDouble() < pair.getSecondValue()) {
                out.add(pair.getFirstValue());
            }
        }

        return out;
    }

}
