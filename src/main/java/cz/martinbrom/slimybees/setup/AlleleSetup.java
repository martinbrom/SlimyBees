package cz.martinbrom.slimybees.setup;

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

        registry.register(ChromosomeType.FERTILITY, createAlleleValue(1, true), AlleleUids.FERTILITY_LOW, "Low");
        registry.register(ChromosomeType.FERTILITY, createAlleleValue(2, true), AlleleUids.FERTILITY_NORMAL, "Normal");
        registry.register(ChromosomeType.FERTILITY, createAlleleValue(3), AlleleUids.FERTILITY_HIGH, "High");
        registry.register(ChromosomeType.FERTILITY, createAlleleValue(4), AlleleUids.FERTILITY_VERY_HIGH, "Very High");

        registry.register(ChromosomeType.PRODUCTIVITY, createAlleleValue(0.5, true), AlleleUids.PRODUCTIVITY_VERY_LOW, "Very Low");
        registry.register(ChromosomeType.PRODUCTIVITY, createAlleleValue(0.75), AlleleUids.PRODUCTIVITY_LOW, "Low");
        registry.register(ChromosomeType.PRODUCTIVITY, createAlleleValue(1.0, true), AlleleUids.PRODUCTIVITY_AVERAGE, "Average");
        registry.register(ChromosomeType.PRODUCTIVITY, createAlleleValue(1.5), AlleleUids.PRODUCTIVITY_GOOD, "Good");
        registry.register(ChromosomeType.PRODUCTIVITY, createAlleleValue(2.0), AlleleUids.PRODUCTIVITY_VERY_GOOD, "Very Good");
    }

    private static <T> AlleleValue<T> createAlleleValue(T value) {
        return createAlleleValue(value, false);
    }

    private static <T> AlleleValue<T> createAlleleValue(T value, boolean isDominant) {
        return new AlleleValue<T>() {
            @Override
            public boolean isDominant() {
                return isDominant;
            }

            @Override
            public T getValue() {
                return value;
            }
        };
    }

}
