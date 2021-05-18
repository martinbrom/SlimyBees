package cz.martinbrom.slimybees;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

/**
 * This class holds a static reference to every {@link RecipeType} found in SlimyBees.
 */
public class RecipeTypes {

    // prevent instantiation
    private RecipeTypes() {
    }

    public static final RecipeType BEE_NET = new RecipeType(
            SlimyBeesPlugin.getKey("bee_net"),
            ItemStacks.BEE_NET,
            "",
            "&2&oCatch using the Bee Net",
            "&2&oin the wilderness"
    );

    public static final RecipeType WILDERNESS = new RecipeType(
            SlimyBeesPlugin.getKey("wilderness"),
            new ItemStack(Material.SPRUCE_SAPLING)
    );

    public static final RecipeType BREEDING = new RecipeType(
            SlimyBeesPlugin.getKey("breeding"),
            new CustomItem(
                    Material.BEE_SPAWN_EGG,
                    "&cBreeding",
                    "",
                    "&fCan be obtained by breeding",
                    "&fwo specific bees in a bee hive",
                    "&fwith a little bit of luck"
            )
    );

    public static final RecipeType BEE_PRODUCT = new RecipeType(
            SlimyBeesPlugin.getKey("bee_product"),
            new CustomItem(
                    Material.HONEYCOMB,
                    "&cBee Product",
                    "",
                    "&fCan be obtained as a product",
                    "&fof bees working in a bee hive"
            )
    );

}
