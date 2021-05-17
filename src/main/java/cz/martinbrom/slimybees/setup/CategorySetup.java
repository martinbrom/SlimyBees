package cz.martinbrom.slimybees.setup;

import javax.annotation.Nonnull;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.SlimyBeesPlugin;

/**
 * This is the place where all categories from SlimyBees are registered.
 */
public class CategorySetup {

    private static boolean initialized;

    public static void setUp(@Nonnull SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees categories can only be registered once!");
        }

        initialized = true;

        Categories.MAIN_CATEGORY.register(plugin);
        Categories.GENERAL.register(plugin);
    }
}
