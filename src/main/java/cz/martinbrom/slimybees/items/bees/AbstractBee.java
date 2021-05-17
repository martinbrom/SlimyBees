package cz.martinbrom.slimybees.items.bees;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

// TODO: 16.05.21 Javadoc
// TODO: 16.05.21 Maybe use interface instead of empty abstract class?
public abstract class AbstractBee extends UnplaceableBlock {

    // TODO: 16.05.21 Javadoc
    @ParametersAreNonnullByDefault
    public AbstractBee(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

}
