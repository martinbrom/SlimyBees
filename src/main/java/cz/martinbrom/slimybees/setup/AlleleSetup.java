package cz.martinbrom.slimybees.setup;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.enums.AlleleType;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;

public class AlleleSetup {

    private static boolean initialized = false;

    // prevent instantiation
    private AlleleSetup() {
    }

    public static void setUp() {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees Alleles can only be registered once!");
        }

        initialized = true;

        AlleleRegistry registry = SlimyBeesPlugin.getAlleleRegistry();

        registry.createAlleles(AlleleType.Speed.class, ChromosomeTypeImpl.SPEED);
        registry.createAlleles(AlleleType.Fertility.class, ChromosomeTypeImpl.FERTILITY);
    }

}
