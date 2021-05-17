package cz.martinbrom.slimybees.setup;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;

import cz.martinbrom.slimybees.Items;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.utils.Biomes;
import cz.martinbrom.slimybees.worldgen.GroundNestPopulator;

/**
 * This is the place where all {@link BlockPopulator}s from SlimyBees are registered.
 */
public class PopulatorSetup {

    private static boolean initialized = false;

    // prevent instantiation
    private PopulatorSetup() {
    }

    public static void setUp(@Nonnull SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees populators can only be registered once!");
        }

        initialized = true;

        // TODO: 16.05.21 Adjust chances
        new GroundNestPopulator(
                Biomes.OUTER_END,
                new Material[] { Material.END_STONE },
                0.1,
                Items.ENDER_BEE_NEST
        ).register(plugin);

        new GroundNestPopulator(
                Biomes.MILD_FORESTS,
                new Material[] { Material.GRASS },
                0.1,
                Items.FOREST_BEE_NEST
        ).register(plugin);
    }

}
