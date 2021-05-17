package cz.martinbrom.slimybees.setup;

import javax.annotation.Nonnull;

import cz.martinbrom.slimybees.Items;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.items.bees.BasicBee;
import cz.martinbrom.slimybees.items.bees.BeeNest;

/**
 * This is the place where all items from SlimyBees are registered.
 */
public class ItemSetup {

    private static boolean initialized = false;

    // prevent instantiation
    private ItemSetup() {
    }

    public static void setUp(@Nonnull SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees items can only be registered once!");
        }

        initialized = true;

        // bees
        new BasicBee(Items.ENDER_BEE).register(plugin);
        new BasicBee(Items.FOREST_BEE).register(plugin);

        // nests
        new BeeNest(Items.ENDER_BEE_NEST, Items.ENDER_BEE).register(plugin);
        new BeeNest(Items.FOREST_BEE_NEST, Items.FOREST_BEE).register(plugin);
    }

}
