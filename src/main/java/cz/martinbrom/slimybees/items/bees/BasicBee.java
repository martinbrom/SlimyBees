package cz.martinbrom.slimybees.items.bees;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.utils.RecipeTypes;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

// TODO: 16.05.21 Javadoc
public class BasicBee extends AbstractBee {

    // TODO: 16.05.21 Javadoc
    public BasicBee(@Nonnull SlimefunItemStack itemStack) {
        super(Categories.GENERAL, itemStack, RecipeTypes.BEE_NET, new ItemStack[9]);
    }

}
