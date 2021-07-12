package cz.martinbrom.slimybees.setup;

import org.bukkit.Material;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleValue;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

public class AlleleSetup {
    private static boolean initialized = false;

    // prevent instantiation
    private AlleleSetup() {}

    public static void setUp() {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees Alleles can only be registered once!");
        }

        initialized = true;

        AlleleRegistry registry = SlimyBeesPlugin.getAlleleRegistry();

        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(0.5, true), AlleleUids.PRODUCTIVITY_VERY_LOW, "Very Low");
        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(0.75), AlleleUids.PRODUCTIVITY_LOW, "Low");
        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(1.0, true), AlleleUids.PRODUCTIVITY_AVERAGE, "Average");
        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(1.5), AlleleUids.PRODUCTIVITY_GOOD, "Good");
        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(2.0), AlleleUids.PRODUCTIVITY_VERY_GOOD, "Very Good");

        registry.register(ChromosomeType.FERTILITY, new AlleleValue<>(1, true), AlleleUids.FERTILITY_LOW, "Low");
        registry.register(ChromosomeType.FERTILITY, new AlleleValue<>(2, true), AlleleUids.FERTILITY_NORMAL, "Normal");
        registry.register(ChromosomeType.FERTILITY, new AlleleValue<>(3), AlleleUids.FERTILITY_HIGH, "High");
        registry.register(ChromosomeType.FERTILITY, new AlleleValue<>(4), AlleleUids.FERTILITY_VERY_HIGH, "Very High");

        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(15), AlleleUids.LIFESPAN_VERY_SHORT, "Very Short");
        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(25), AlleleUids.LIFESPAN_SHORT, "Short");
        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(30, true), AlleleUids.LIFESPAN_NORMAL, "Normal");
        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(40), AlleleUids.LIFESPAN_LONG, "Long");
        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(60, true), AlleleUids.LIFESPAN_VERY_LONG, "Very Long");

        registry.register(ChromosomeType.RANGE, new AlleleValue<>(1), AlleleUids.RANGE_TINY, "Tiny");
        registry.register(ChromosomeType.RANGE, new AlleleValue<>(2, true), AlleleUids.RANGE_SMALL, "Small");
        registry.register(ChromosomeType.RANGE, new AlleleValue<>(3, true), AlleleUids.RANGE_NORMAL, "Normal");
        registry.register(ChromosomeType.RANGE, new AlleleValue<>(4), AlleleUids.RANGE_LONG, "Long");

        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.AIR), AlleleUids.PLANT_NONE, "None");
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.SUNFLOWER), AlleleUids.PLANT_SUNFLOWER, "Sunflower");
    }

}
