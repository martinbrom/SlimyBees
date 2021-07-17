package cz.martinbrom.slimybees.items.machines;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class HiveFrame extends UnplaceableBlock {

    public static final int DEFAULT_PRODUCTION_MODIFIER = 1;
    public static final int DEFAULT_LIFESPAN_MODIFIER = 1;

    public HiveFrame(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    public HiveFrame(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @Nullable ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);
    }

    public double getProductionModifier() {
        return DEFAULT_PRODUCTION_MODIFIER;
    }

    public double getLifespanModifier() {
        return DEFAULT_LIFESPAN_MODIFIER;
    }

}
