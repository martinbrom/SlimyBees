package cz.martinbrom.slimybees;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;

/**
 * This class holds a static reference to every {@link RecipeType} found in SlimyBees.
 */
public class RecipeTypes {

    // prevent instantiation
    private RecipeTypes() {}

    public static final RecipeType WILDERNESS = new RecipeType(
            SlimyBeesPlugin.getKey("wilderness"),
            new ItemStack(Material.SPRUCE_SAPLING));

    public static final RecipeType BREEDING = new RecipeType(
            SlimyBeesPlugin.getKey("breeding"),
            new CustomItemStack(Material.BEE_SPAWN_EGG,
                    "&cBreeding",
                    "",
                    "&fCan be obtained by breeding",
                    "&fwo specific bees in a bee hive",
                    "&fwith a little bit of luck"));

    public static final RecipeType BEE_PRODUCT = new RecipeType(
            SlimyBeesPlugin.getKey("bee_product"),
            new CustomItemStack(Material.HONEYCOMB,
                    "&cBee Product",
                    "",
                    "&fCan be obtained as a product",
                    "&fof bees working in a bee hive"));

    public static final RecipeType CENTRIFUGE = new RecipeType(
            SlimyBeesPlugin.getKey("centrifuge"),
            new CustomItemStack(Material.GRINDSTONE,
                    "&fCentrifuge",
                    "",
                    "&7Spin it in a centrifuge"));

}
