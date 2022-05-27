package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.category.BeeAtlasCategoryFactory;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;

/**
 * This is the place where all categories from SlimyBees are registered.
 */
@ParametersAreNonnullByDefault
public class CategorySetup {

    // prevent instantiation
    private CategorySetup() {}

    private static boolean initialized;

    public static void setUp(SlimyBeesPlugin plugin, BeeAtlasCategoryFactory factory) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees categories can only be registered once!");
        }

        initialized = true;

        Categories.ITEMS.register(plugin);

        ItemStack displayItem = new CustomItemStack(SlimyBeesHeadTexture.DRONE.getAsItemStack(), "Bee Atlas");
        factory.createList(displayItem).register(plugin);
    }
}
