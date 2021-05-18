package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;

import cz.martinbrom.slimybees.BeeBuilder;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.utils.BiomeSets;

/**
 * This is the place where all base bees from SlimyBees are registered.
 */
@ParametersAreNonnullByDefault
public class BeeSetup {

    private static boolean initialized;

    public static void setUp(SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees bees can only be registered once!");
        }

        initialized = true;

        BeeBuilder.of("FOREST")
                .setName("&2Forest")
                .setNest(BiomeSets.MILD_FORESTS, new Material[] { Material.GRASS_BLOCK, Material.SAND }, 0.025)
                .register(plugin);

        BeeBuilder.of("ENDER")
                .setName("&5Ender")
                .setNest(BiomeSets.OUTER_END, new Material[] { Material.END_STONE }, 0.001)
                .register(plugin);
    }

}
