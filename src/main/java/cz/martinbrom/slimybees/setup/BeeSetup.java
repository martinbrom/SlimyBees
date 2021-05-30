package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;

import cz.martinbrom.slimybees.core.BeeBuilder;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.BiomeSets;
import cz.martinbrom.slimybees.core.genetics.AlleleSpeedValue;
import cz.martinbrom.slimybees.core.genetics.ChromosomeType;
import cz.martinbrom.slimybees.core.genetics.AlleleFertilityValue;
import cz.martinbrom.slimybees.core.genetics.GenomeBuilder;

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
                .setDefaultChromosome(ChromosomeType.SPEED, AlleleSpeedValue.SLOW)
                .register(plugin);

        BeeBuilder.of("ENDER")
                .setName("&5Ender")
                .setNest(BiomeSets.OUTER_END, new Material[] { Material.END_STONE }, 0.001)
                .setDefaultChromosome(ChromosomeType.RANGE, AlleleFertilityValue.GOOD)
                .register(plugin);

        BeeBuilder.of("TEST")
                .setName("&fTest")
                .setMutation("ENDER", "FOREST", 0.5)
                .register(plugin);
    }

}
