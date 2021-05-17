package cz.martinbrom.slimybees.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Items;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;

/**
 * This class holds a static reference to every {@link RecipeType} found in SlimyBees.
 */
public class RecipeTypes {

    // prevent instantiation
    private RecipeTypes() {
    }

    public static final RecipeType BEE_NET = new RecipeType(
            SlimyBeesPlugin.instance().getKey("bee_net"),
            Items.BEE_NET,
            "",
            "&a&oCatch using the Bee Net",
            "&a&oin the wilderness"
    );

    public static final RecipeType WILDERNESS = new RecipeType(
            SlimyBeesPlugin.instance().getKey("wilderness"),
            new ItemStack(Material.BEE_NEST)
    );

}
