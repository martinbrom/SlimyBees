package cz.martinbrom.slimybees.setup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.BiomeSets;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeBuilder;
import cz.martinbrom.slimybees.core.genetics.BeeMutation;
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
                new BeeBuilder(SpeciesUids.FOREST, ChatColor.DARK_GREEN, true)
                        .addNest(BiomeSets.MILD_FORESTS, new Material[] { Material.GRASS_BLOCK, Material.DIRT }, 0.025)
                        .addProduct(ItemStacks.HONEY_COMB, 0.1),
                new BeeBuilder(SpeciesUids.MEADOWS, ChatColor.DARK_GREEN, true)
                        .addNest(BiomeSets.PLAINS, new Material[] { Material.GRASS_BLOCK }, 0.025)
                        .addProduct(ItemStacks.HONEY_COMB, 0.15),
                new BeeBuilder(SpeciesUids.STONE, ChatColor.GRAY, true)
                        .addNest(BiomeSets.MOUNTAINS, new Material[] { Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.GRAVEL }, 0.035)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                new BeeBuilder(SpeciesUids.SANDY, ChatColor.YELLOW, true)
                        .addNest(BiomeSets.DESERTS, new Material[] { Material.SAND, Material.RED_SAND, Material.COARSE_DIRT }, 0.015)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                new BeeBuilder(SpeciesUids.WATER, ChatColor.DARK_BLUE, true)
                        .addNest(BiomeSets.BODIES_OF_WATER, new Material[] { Material.WATER }, 0.005)
                        .addProduct(ItemStacks.HONEY_COMB, 0.1),
                new BeeBuilder(SpeciesUids.NETHER, ChatColor.DARK_RED, true)
                        .addNest(BiomeSets.RED_NETHER, new Material[] { Material.NETHERRACK, Material.CRIMSON_NYLIUM }, 0.01)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                new BeeBuilder(SpeciesUids.ENDER, ChatColor.DARK_RED, true)
                        .addNest(BiomeSets.OUTER_END, new Material[] { Material.END_STONE }, 0.005)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                // </editor-fold>

                // <editor-fold desc="Base" defaultstate="collapsed">
                new BeeBuilder(SpeciesUids.COMMON, ChatColor.WHITE)
                        .addProduct(ItemStacks.HONEY_COMB, 0.2),
                new BeeBuilder(SpeciesUids.CULTIVATED, ChatColor.AQUA, true)
                        .addProduct(ItemStacks.HONEY_COMB, 0.3),
                new BeeBuilder(SpeciesUids.NOBLE, ChatColor.GOLD)
                        .addMutation(SpeciesUids.CULTIVATED, SpeciesUids.COMMON, 0.15)
                        .addProduct(ItemStacks.SWEET_COMB, 0.2),
                new BeeBuilder(SpeciesUids.MAJESTIC, ChatColor.GOLD, true)
                        .addMutation(SpeciesUids.NOBLE, SpeciesUids.CULTIVATED, 0.1)
                        // TODO: 27.06.21 Just a test, will create default alleles for every species at some point
                        .addDefaultAlleleValue(ChromosomeType.FERTILITY, AlleleUids.FERTILITY_HIGH)
                        .addProduct(ItemStacks.SWEET_COMB, 0.3),
                new BeeBuilder(SpeciesUids.IMPERIAL, ChatColor.GOLD)
                        .setEnchanted(true)
                        .addMutation(SpeciesUids.MAJESTIC, SpeciesUids.NOBLE, 0.05)
                        .addProduct(ItemStacks.SWEET_COMB, 0.2)
                        .addProduct(ItemStacks.ROYAL_JELLY, 0.05),
                new BeeBuilder(SpeciesUids.DILIGENT, ChatColor.YELLOW)
                        .addMutation(SpeciesUids.CULTIVATED, SpeciesUids.COMMON, 0.15)
                        .addProduct(ItemStacks.DRY_COMB, 0.2),
                new BeeBuilder(SpeciesUids.UNWEARY, ChatColor.YELLOW, true)
                        .addMutation(SpeciesUids.DILIGENT, SpeciesUids.CULTIVATED, 0.1)
                        .addProduct(ItemStacks.DRY_COMB, 0.3),
                new BeeBuilder(SpeciesUids.INDUSTRIOUS, ChatColor.YELLOW)
                        .setEnchanted(true)
                        .addMutation(SpeciesUids.UNWEARY, SpeciesUids.DILIGENT, 0.05)
                        .addProduct(ItemStacks.DRY_COMB, 0.2)
                        .addProduct(ItemStacks.POLLEN, 0.05),
                // </editor-fold>

                // <editor-fold desc="Farming" defaultstate="collapsed">
                new BeeBuilder(SpeciesUids.FARMER, ChatColor.GREEN)
                        .addMutation(SpeciesUids.MEADOWS, SpeciesUids.COMMON, 0.25)
                        .addProduct(ItemStacks.HONEY_COMB, 0.2),
                new BeeBuilder(SpeciesUids.WHEAT, ChatColor.GREEN)
                        .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, 0.3)
                        .addProduct(new ItemStack(Material.WHEAT), 0.2),
                new BeeBuilder(SpeciesUids.SUGAR_CANE, ChatColor.GREEN)
                        .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, 0.3)
                        .addProduct(new ItemStack(Material.WHEAT), 0.2),
                new BeeBuilder(SpeciesUids.MELON, ChatColor.GREEN)
                        .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, 0.2)
                        .addProduct(new ItemStack(Material.MELON_SLICE), 0.6),
                new BeeBuilder(SpeciesUids.PUMPKIN, ChatColor.GREEN)
                        .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, 0.2)
                        .addProduct(new ItemStack(Material.PUMPKIN), 0.2),
                new BeeBuilder(SpeciesUids.POTATO, ChatColor.GREEN)
                        .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, 0.2)
                        .addProduct(new ItemStack(Material.POTATO), 0.2)
                        .addProduct(new ItemStack(Material.POISONOUS_POTATO), 0.004),
                new BeeBuilder(SpeciesUids.CARROT, ChatColor.GREEN)
                        .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, 0.2)
                        .addProduct(new ItemStack(Material.CARROT), 0.2),
                new BeeBuilder(SpeciesUids.BEETROOT, ChatColor.GREEN)
                        .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, 0.1)
                        .addProduct(new ItemStack(Material.BEETROOT), 0.1),
                new BeeBuilder(SpeciesUids.COCOA, ChatColor.GREEN)
                        .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, 0.1)
                        .addProduct(new ItemStack(Material.COCOA), 0.4),
                new BeeBuilder(SpeciesUids.BERRY, ChatColor.GREEN)
                        .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, 0.1)
                        .addProduct(new ItemStack(Material.SWEET_BERRIES), 0.1),
                // TODO: 11.07.21 Glow Berries
                // </editor-fold>
        };

        SlimyBeesPlugin.logger().info("Registered " + bees.length + " bee species!");
        List<BeeBuilder> nestingBees = Arrays.stream(bees)
                // important line below, do not remove!!
                .peek(b -> b.register(plugin))
                .filter(BeeBuilder::isNesting)
                .collect(Collectors.toList());
        registerCommonBeeMutations(plugin, nestingBees);
        registerCultivatedBeeMutations(plugin, nestingBees);
    }

    private static void registerCommonBeeMutations(SlimyBeesPlugin plugin, List<BeeBuilder> bees) {
        int size = bees.size();
        for (int i = 0; i < size; i++) {
            String firstUid = bees.get(i).getUid();
            for (int j = i + 1; j < size; j++) {
                String secondUid = bees.get(j).getUid();
                BeeMutation mutation = new BeeMutation(firstUid, secondUid, SpeciesUids.COMMON, 0.25);
                plugin.getBeeRegistry().getBeeMutationTree().registerMutation(mutation);
            }
        }
    }

    private static void registerCultivatedBeeMutations(SlimyBeesPlugin plugin, List<BeeBuilder> bees) {
        for (BeeBuilder bee : bees) {
            String firstUid = bee.getUid();
            BeeMutation mutation = new BeeMutation(firstUid, SpeciesUids.COMMON, SpeciesUids.CULTIVATED, 0.2);
            plugin.getBeeRegistry().getBeeMutationTree().registerMutation(mutation);
        }
    }

}


