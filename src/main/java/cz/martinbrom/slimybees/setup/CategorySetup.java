package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.SlimyBeesPlugin;

/**
 * This is the place where all categories from SlimyBees are registered.
 */
@ParametersAreNonnullByDefault
public class CategorySetup {

    private static boolean initialized;

    public static void setUp(SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees categories can only be registered once!");
        }

        initialized = true;

        Categories.ITEMS.register(plugin);
        Categories.BEE_ATLAS.register(plugin);
    }
}
