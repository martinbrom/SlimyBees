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

        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(0.5, true), AlleleUids.PRODUCTIVITY_VERY_LOW);
        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(0.75), AlleleUids.PRODUCTIVITY_LOW);
        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(1.0, true), AlleleUids.PRODUCTIVITY_NORMAL);
        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(1.5), AlleleUids.PRODUCTIVITY_GOOD);
        registry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(2.0), AlleleUids.PRODUCTIVITY_VERY_GOOD);

        registry.register(ChromosomeType.FERTILITY, new AlleleValue<>(1, true), AlleleUids.FERTILITY_LOW);
        registry.register(ChromosomeType.FERTILITY, new AlleleValue<>(2, true), AlleleUids.FERTILITY_NORMAL);
        registry.register(ChromosomeType.FERTILITY, new AlleleValue<>(3), AlleleUids.FERTILITY_HIGH);
        registry.register(ChromosomeType.FERTILITY, new AlleleValue<>(4), AlleleUids.FERTILITY_VERY_HIGH);

        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(15), AlleleUids.LIFESPAN_VERY_SHORT);
        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(25), AlleleUids.LIFESPAN_SHORT);
        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(30, true), AlleleUids.LIFESPAN_NORMAL);
        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(40), AlleleUids.LIFESPAN_LONG);
        registry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(60, true), AlleleUids.LIFESPAN_VERY_LONG);

        registry.register(ChromosomeType.RANGE, new AlleleValue<>(1), AlleleUids.RANGE_VERY_SHORT);
        registry.register(ChromosomeType.RANGE, new AlleleValue<>(2, true), AlleleUids.RANGE_SHORT);
        registry.register(ChromosomeType.RANGE, new AlleleValue<>(3, true), AlleleUids.RANGE_NORMAL);
        registry.register(ChromosomeType.RANGE, new AlleleValue<>(4), AlleleUids.RANGE_LONG);
        registry.register(ChromosomeType.RANGE, new AlleleValue<>(5), AlleleUids.RANGE_VERY_LONG);

        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.AIR), AlleleUids.PLANT_NONE);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.OXEYE_DAISY), AlleleUids.PLANT_OXEYE_DAISY);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.WHEAT), AlleleUids.PLANT_WHEAT);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.SUGAR_CANE), AlleleUids.PLANT_SUGAR_CANE);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.MELON), AlleleUids.PLANT_MELON);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.PUMPKIN), AlleleUids.PLANT_PUMPKIN);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.POTATOES), AlleleUids.PLANT_POTATO);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.CARROTS), AlleleUids.PLANT_CARROT);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.BEETROOTS), AlleleUids.PLANT_BEETROOT);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.COCOA), AlleleUids.PLANT_COCOA);
        registry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.SWEET_BERRY_BUSH), AlleleUids.PLANT_BERRY);
    }

}
