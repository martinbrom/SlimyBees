package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import cz.martinbrom.slimybees.BiomeSets;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeBuilder;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class BeeSetup {

    private static boolean initialized = false;

    // prevent instantiation
    private BeeSetup() {}

    public static void setUp(SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees bees can only be registered once!");
        }

        initialized = true;

        BeeBuilder[] bees = new BeeBuilder[] {
                // <editor-fold desc="Nesting" defaultstate="collapsed">
                new BeeBuilder("FOREST", ChatColor.DARK_GREEN, true)
                        .addNest(BiomeSets.MILD_FORESTS, new Material[] { Material.GRASS_BLOCK, Material.DIRT }, 0.025)
                        .addProduct(ItemStacks.HONEY_COMB, 0.15),
                new BeeBuilder("STONE", ChatColor.GRAY, true)
                        .addNest(BiomeSets.MOUNTAINS, new Material[] { Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.GRAVEL }, 0.035)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                new BeeBuilder("SANDY", ChatColor.YELLOW, true)
                        .addNest(BiomeSets.DESERTS, new Material[] { Material.SAND, Material.RED_SAND, Material.COARSE_DIRT }, 0.015)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                new BeeBuilder("WATER", ChatColor.DARK_BLUE, true)
                        .addNest(BiomeSets.BODIES_OF_WATER, new Material[] { Material.WATER }, 0.005)
                        .addProduct(ItemStacks.HONEY_COMB, 0.15),
                new BeeBuilder("NETHER", ChatColor.DARK_RED, true)
                        .addNest(BiomeSets.RED_NETHER, new Material[] { Material.NETHERRACK, Material.CRIMSON_NYLIUM }, 0.01)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                // </editor-fold>

                // <editor-fold desc="Base" defaultstate="collapsed">
                new BeeBuilder("COMMON", ChatColor.WHITE)
                        .addProduct(ItemStacks.HONEY_COMB, 0.2),
                new BeeBuilder("CULTIVATED", ChatColor.AQUA, true)
                        .addProduct(ItemStacks.HONEY_COMB, 0.3),
                new BeeBuilder("NOBLE", ChatColor.GOLD)
                        .addMutation(SpeciesUids.CULTIVATED, SpeciesUids.COMMON, 0.15)
                        .addProduct(ItemStacks.SWEET_COMB, 0.2),
                new BeeBuilder("MAJESTIC", ChatColor.GOLD, true)
                        .addMutation(SpeciesUids.NOBLE, SpeciesUids.CULTIVATED, 0.1)
                        // TODO: 27.06.21 Just a test, will create default alleles for every species at some point
                        .addDefaultAlleleValue(ChromosomeType.FERTILITY, AlleleUids.FERTILITY_HIGH)
                        .addProduct(ItemStacks.SWEET_COMB, 0.3),
                new BeeBuilder("IMPERIAL", ChatColor.GOLD)
                        .setEnchanted(true)
                        .addMutation(SpeciesUids.MAJESTIC, SpeciesUids.NOBLE, 0.05)
                        .addProduct(ItemStacks.SWEET_COMB, 0.2)
                        .addProduct(ItemStacks.ROYAL_JELLY, 0.05),
                new BeeBuilder("DILIGENT", ChatColor.YELLOW)
                        .addMutation(SpeciesUids.CULTIVATED, SpeciesUids.COMMON, 0.15)
                        .addProduct(ItemStacks.DRY_COMB, 0.2),
                new BeeBuilder("UNWEARY", ChatColor.YELLOW, true)
                        .addMutation(SpeciesUids.DILIGENT, SpeciesUids.CULTIVATED, 0.1)
                        .addProduct(ItemStacks.DRY_COMB, 0.3),
                new BeeBuilder("INDUSTRIOUS", ChatColor.YELLOW)
                        .setEnchanted(true)
                        .addMutation(SpeciesUids.UNWEARY, SpeciesUids.DILIGENT, 0.05)
                        .addProduct(ItemStacks.DRY_COMB, 0.2)
                        .addProduct(ItemStacks.POLLEN, 0.05)
                // </editor-fold>
        };

        for (BeeBuilder bee : bees) {
            bee.register(plugin);
        }

        // TODO: 27.06.21 Register common and cultivated mutations

        for (BeeBuilder bee : bees) {
            bee.postRegister(plugin);
        }
    }

}


