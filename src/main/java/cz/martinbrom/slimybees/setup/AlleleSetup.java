package cz.martinbrom.slimybees.setup;

import org.bukkit.Material;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleValue;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

public class AlleleSetup {

    public static String DEFAULT_SPECIES_UID = SpeciesUids.FOREST;
    public static String DEFAULT_PRODUCTIVITY_UID = AlleleUids.PRODUCTIVITY_NORMAL;
    public static String DEFAULT_FERTILITY_UID = AlleleUids.FERTILITY_NORMAL;
    public static String DEFAULT_LIFESPAN_UID = AlleleUids.LIFESPAN_NORMAL;
    public static String DEFAULT_RANGE_UID = AlleleUids.RANGE_NORMAL;
    public static String DEFAULT_PLANT_UID = AlleleUids.PLANT_NONE;

    private static boolean initialized = false;

    // prevent instantiation
    private AlleleSetup() {}

    public static void setUp() {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees Alleles can only be registered once!");
        }

        initialized = true;

        AlleleRegistry alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();

        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(0.5, true), AlleleUids.PRODUCTIVITY_VERY_LOW);
        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(0.75), AlleleUids.PRODUCTIVITY_LOW);
        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(1.0, true), AlleleUids.PRODUCTIVITY_NORMAL);
        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(1.5), AlleleUids.PRODUCTIVITY_HIGH);
        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(2.0), AlleleUids.PRODUCTIVITY_VERY_HIGH);

        alleleRegistry.register(ChromosomeType.FERTILITY, new AlleleValue<>(1, true), AlleleUids.FERTILITY_LOW);
        alleleRegistry.register(ChromosomeType.FERTILITY, new AlleleValue<>(2, true), AlleleUids.FERTILITY_NORMAL);
        alleleRegistry.register(ChromosomeType.FERTILITY, new AlleleValue<>(3), AlleleUids.FERTILITY_HIGH);
        alleleRegistry.register(ChromosomeType.FERTILITY, new AlleleValue<>(4), AlleleUids.FERTILITY_VERY_HIGH);

        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(15), AlleleUids.LIFESPAN_VERY_SHORT);
        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(25), AlleleUids.LIFESPAN_SHORT);
        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(30, true), AlleleUids.LIFESPAN_NORMAL);
        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(40), AlleleUids.LIFESPAN_LONG);
        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(60, true), AlleleUids.LIFESPAN_VERY_LONG);

        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(1), AlleleUids.RANGE_VERY_SHORT);
        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(2, true), AlleleUids.RANGE_SHORT);
        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(3, true), AlleleUids.RANGE_NORMAL);
        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(4), AlleleUids.RANGE_LONG);
        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(5), AlleleUids.RANGE_VERY_LONG);

        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.AIR), AlleleUids.PLANT_NONE);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.OXEYE_DAISY), AlleleUids.PLANT_OXEYE_DAISY);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.WHEAT), AlleleUids.PLANT_WHEAT);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.SUGAR_CANE), AlleleUids.PLANT_SUGAR_CANE);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.MELON), AlleleUids.PLANT_MELON);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.PUMPKIN), AlleleUids.PLANT_PUMPKIN);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.POTATOES), AlleleUids.PLANT_POTATO);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.CARROTS), AlleleUids.PLANT_CARROT);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.BEETROOTS), AlleleUids.PLANT_BEETROOT);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.COCOA), AlleleUids.PLANT_COCOA);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.SWEET_BERRY_BUSH), AlleleUids.PLANT_BERRY);

        BeeRegistry beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        AlleleService alleleService = SlimyBeesPlugin.getAlleleService();
        Allele[] defaultTemplate = new Allele[ChromosomeType.CHROMOSOME_COUNT];

        alleleService.set(defaultTemplate, ChromosomeType.SPECIES, DEFAULT_SPECIES_UID);
        alleleService.set(defaultTemplate, ChromosomeType.PRODUCTIVITY, DEFAULT_PRODUCTIVITY_UID);
        alleleService.set(defaultTemplate, ChromosomeType.FERTILITY, DEFAULT_FERTILITY_UID);
        alleleService.set(defaultTemplate, ChromosomeType.LIFESPAN, DEFAULT_LIFESPAN_UID);
        alleleService.set(defaultTemplate, ChromosomeType.RANGE, DEFAULT_RANGE_UID);
        alleleService.set(defaultTemplate, ChromosomeType.PLANT, DEFAULT_PLANT_UID);

        beeRegistry.registerDefaultTemplate(defaultTemplate);

    }

}
