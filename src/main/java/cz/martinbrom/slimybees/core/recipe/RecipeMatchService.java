package cz.martinbrom.slimybees.core.recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

@ParametersAreNonnullByDefault
public class RecipeMatchService {

    @Nullable
    public static GuaranteedRecipe match(@Nullable List<ItemStack> items, List<AbstractRecipe> recipes) {
        Validate.notEmpty(recipes, "Cannot match recipes for empty or null recipes!");

        if (items == null || items.isEmpty()) {
            return null;
        }

        Set<Integer> found = new HashSet<>();
        for (AbstractRecipe recipe : recipes) {
            for (ItemStack input : recipe.getIngredients()) {
                for (int i = 0; i < items.size(); i++) {
                    if (SlimefunUtils.isItemSimilar(items.get(i), input, true)) {
                        found.add(i);
                        break;
                    }
                }
            }

            if (found.size() == recipe.getIngredients().size()) {
                // make sure we return a GuaranteedRecipe so devs down the line don't run into
                // issues with calling get() multiple times and getting a different recipe each time
                return recipe instanceof RandomRecipe
                    ? ((RandomRecipe) recipe).get()
                    : ((GuaranteedRecipe) recipe);
            }

            found.clear();
        }

        return null;
    }

}
