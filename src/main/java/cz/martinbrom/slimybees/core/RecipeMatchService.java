package cz.martinbrom.slimybees.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.recipe.AbstractRecipe;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

@ParametersAreNonnullByDefault
public class RecipeMatchService {

    @Nullable
    public static AbstractRecipe match(ItemStack[] items, List<AbstractRecipe> recipes) {
        Validate.notEmpty(items, "Cannot match recipes for empty or null items!");
        Validate.notEmpty(recipes, "Cannot match recipes for empty or null recipes!");

        Set<Integer> found = new HashSet<>();
        for (AbstractRecipe recipe : recipes) {
            for (ItemStack input : recipe.getIngredients()) {
                for (int i = 0; i < items.length; i++) {
                    if (SlimefunUtils.isItemSimilar(items[i], input, true)) {
                        found.add(i);
                        break;
                    }
                }
            }

            if (found.size() == recipe.getIngredients().size()) {
                return recipe;
            }

            found.clear();
        }

        return null;
    }

}
