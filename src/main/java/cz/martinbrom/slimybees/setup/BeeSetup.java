package cz.martinbrom.slimybees.setup;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
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

import static org.bukkit.World.Environment.NETHER;
import static org.bukkit.World.Environment.NORMAL;
import static org.bukkit.World.Environment.THE_END;

@ParametersAreNonnullByDefault
public class BeeSetup {

    public static final double COMMON_MUTATION_CHANCE = 0.25;
    public static final double CULTIVATED_MUTATION_CHANCE = 0.2;

    private static boolean initialized = false;

    public static final Consumer<BeeBuilder> HONEY_GROUP = b -> {
        b.addDefaultAlleleValue(ChromosomeType.PRODUCTIVITY, AlleleUids.PRODUCTIVITY_NORMAL);
        b.addDefaultAlleleValue(ChromosomeType.LIFESPAN, AlleleUids.LIFESPAN_LONG);
        b.addDefaultAlleleValue(ChromosomeType.RANGE, AlleleUids.RANGE_VERY_LONG);
    };

    public static final Consumer<BeeBuilder> FARMING_GROUP = b -> {
        b.addDefaultAlleleValue(ChromosomeType.FERTILITY, AlleleUids.FERTILITY_NORMAL);
        b.addDefaultAlleleValue(ChromosomeType.RANGE, AlleleUids.RANGE_LONG);
    };

    public static final Consumer<BeeBuilder> MATERIAL_GROUP = b -> {
        b.addDefaultAlleleValue(ChromosomeType.PRODUCTIVITY, AlleleUids.PRODUCTIVITY_NORMAL);
        b.addDefaultAlleleValue(ChromosomeType.LIFESPAN, AlleleUids.LIFESPAN_SHORT);
    };

    public static final Consumer<BeeBuilder> WATER_GROUP = b ->
            b.addDefaultAlleleValue(ChromosomeType.FERTILITY, AlleleUids.FERTILITY_HIGH);

    public static final Consumer<BeeBuilder> NETHER_GROUP = b ->
            b.addDefaultAlleleValue(ChromosomeType.FERTILITY, AlleleUids.FERTILITY_LOW);

    public static final Consumer<BeeBuilder> END_GROUP = b ->
            b.addDefaultAlleleValue(ChromosomeType.RANGE, AlleleUids.RANGE_LONG);

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
                        .addNest(NORMAL, BiomeSets.MILD_FORESTS, new Material[] { Material.GRASS_BLOCK, Material.DIRT }, 0.025)
                        .addGroupInformation(HONEY_GROUP)
                        .addProduct(ItemStacks.HONEY_COMB, 0.1),
                new BeeBuilder(SpeciesUids.MEADOWS, ChatColor.DARK_GREEN, true)
                        .addNest(NORMAL, BiomeSets.PLAINS, new Material[] { Material.GRASS_BLOCK }, 0.025)
                        .addGroupInformation(HONEY_GROUP)
                        .addProduct(ItemStacks.HONEY_COMB, 0.15),
                new BeeBuilder(SpeciesUids.STONE, ChatColor.GRAY, true)
                        .addNest(NORMAL, BiomeSets.MOUNTAINS, new Material[] { Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.GRAVEL }, 0.035)
                        .addGroupInformation(MATERIAL_GROUP)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                new BeeBuilder(SpeciesUids.SANDY, ChatColor.YELLOW, true)
                        .addNest(NORMAL, BiomeSets.DESERTS, new Material[] { Material.SAND, Material.RED_SAND, Material.COARSE_DIRT }, 0.015)
                        .addGroupInformation(MATERIAL_GROUP)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                new BeeBuilder(SpeciesUids.WATER, ChatColor.DARK_BLUE, true)
                        .addNest(NORMAL, BiomeSets.BODIES_OF_WATER, new Material[] { Material.WATER }, 0.005)
                        .addGroupInformation(WATER_GROUP)
                        .addProduct(ItemStacks.HONEY_COMB, 0.1),
                new BeeBuilder(SpeciesUids.NETHER, ChatColor.DARK_RED, true)
                        .addNest(NETHER, BiomeSets.COLORFUL_NETHER, new Material[] { Material.NETHERRACK, Material.CRIMSON_NYLIUM }, 0.01)
                        .addGroupInformation(NETHER_GROUP)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                new BeeBuilder(SpeciesUids.ENDER, ChatColor.DARK_PURPLE, true)
                        .addNest(THE_END, BiomeSets.OUTER_END, new Material[] { Material.END_STONE }, 0.005)
                        .addGroupInformation(END_GROUP)
                        .addProduct(ItemStacks.DRY_COMB, 0.15),
                // </editor-fold>

                // <editor-fold desc="Base" defaultstate="collapsed">
                new BeeBuilder(SpeciesUids.COMMON, ChatColor.WHITE)
                        .addGroupInformation(HONEY_GROUP)
                        .setAlwaysVisible(true)
                        .addProduct(ItemStacks.HONEY_COMB, 0.2),
                new BeeBuilder(SpeciesUids.CULTIVATED, ChatColor.AQUA, true)
                        .addGroupInformation(HONEY_GROUP)
                        .setAlwaysVisible(true)
                        .addProduct(ItemStacks.HONEY_COMB, 0.3),
                new BeeBuilder(SpeciesUids.NOBLE, ChatColor.GOLD)
                        .addMutation(SpeciesUids.CULTIVATED, SpeciesUids.COMMON, 0.15)
                        .addGroupInformation(HONEY_GROUP)
                        .addProduct(ItemStacks.SWEET_COMB, 0.2),
                new BeeBuilder(SpeciesUids.MAJESTIC, ChatColor.GOLD, true)
                        .addMutation(SpeciesUids.NOBLE, SpeciesUids.CULTIVATED, 0.1)
                        .addGroupInformation(HONEY_GROUP)
                        .addDefaultAlleleValue(ChromosomeType.FERTILITY, AlleleUids.FERTILITY_VERY_HIGH)
                        .addProduct(ItemStacks.SWEET_COMB, 0.3),
                new BeeBuilder(SpeciesUids.IMPERIAL, ChatColor.GOLD)
                        .setEnchanted(true)
                        .addMutation(SpeciesUids.MAJESTIC, SpeciesUids.NOBLE, 0.05)
                        .addGroupInformation(HONEY_GROUP)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_OXEYE_DAISY)
                        .addProduct(ItemStacks.SWEET_COMB, 0.2)
                        .addProduct(ItemStacks.ROYAL_JELLY, 0.05),
                new BeeBuilder(SpeciesUids.DILIGENT, ChatColor.YELLOW)
                        .addMutation(SpeciesUids.CULTIVATED, SpeciesUids.COMMON, 0.15)
                        .addGroupInformation(MATERIAL_GROUP)
                        .addProduct(ItemStacks.DRY_COMB, 0.2),
                new BeeBuilder(SpeciesUids.UNWEARY, ChatColor.YELLOW, true)
                        .addMutation(SpeciesUids.DILIGENT, SpeciesUids.CULTIVATED, 0.1)
                        .addGroupInformation(MATERIAL_GROUP)
                        .addDefaultAlleleValue(ChromosomeType.FERTILITY, AlleleUids.FERTILITY_LOW)
                        .addProduct(ItemStacks.DRY_COMB, 0.3),
                new BeeBuilder(SpeciesUids.INDUSTRIOUS, ChatColor.YELLOW)
                        .setEnchanted(true)
                        .addMutation(SpeciesUids.UNWEARY, SpeciesUids.DILIGENT, 0.05)
                        .addGroupInformation(MATERIAL_GROUP)
                        .addProduct(ItemStacks.DRY_COMB, 0.2)
                        .addProduct(ItemStacks.POLLEN, 0.05),
                // </editor-fold>

                // <editor-fold desc="Farming" defaultstate="collapsed">
                new BeeBuilder(SpeciesUids.FARMER, ChatColor.GREEN)
                        .addMutation(SpeciesUids.MEADOWS, SpeciesUids.COMMON, 0.25)
                        .addGroupInformation(FARMING_GROUP)
                        .addProduct(ItemStacks.HONEY_COMB, 0.2),
                createFarmingBee(SpeciesUids.WHEAT, 0.3)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_WHEAT)
                        .addProduct(new ItemStack(Material.WHEAT), 0.2),
                createFarmingBee(SpeciesUids.SUGAR_CANE, 0.3)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_SUGAR_CANE)
                        .addProduct(new ItemStack(Material.SUGAR_CANE), 0.2),
                createFarmingBee(SpeciesUids.MELON, 0.2)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_MELON)
                        .addProduct(new ItemStack(Material.MELON_SLICE), 0.6),
                createFarmingBee(SpeciesUids.PUMPKIN, 0.2)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_PUMPKIN)
                        .addProduct(new ItemStack(Material.PUMPKIN), 0.2),
                createFarmingBee(SpeciesUids.POTATO, 0.2)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_POTATO)
                        .addProduct(new ItemStack(Material.POTATO), 0.2)
                        .addProduct(new ItemStack(Material.POISONOUS_POTATO), 0.004),
                createFarmingBee(SpeciesUids.CARROT, 0.2)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_CARROT)
                        .addProduct(new ItemStack(Material.CARROT), 0.2),
                createFarmingBee(SpeciesUids.BEETROOT, 0.1)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_BEETROOT)
                        .addProduct(new ItemStack(Material.BEETROOT), 0.1),
                createFarmingBee(SpeciesUids.COCOA, 0.1)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_COCOA)
                        .addProduct(new ItemStack(Material.COCOA_BEANS), 0.4),
                createFarmingBee(SpeciesUids.BERRY, 0.1)
                        .addDefaultAlleleValue(ChromosomeType.PLANT, AlleleUids.PLANT_BERRY)
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
                BeeMutation mutation = new BeeMutation(firstUid, secondUid, SpeciesUids.COMMON, COMMON_MUTATION_CHANCE);
                plugin.getBeeRegistry().getBeeMutationTree().registerMutation(mutation);
            }
        }
    }

    private static void registerCultivatedBeeMutations(SlimyBeesPlugin plugin, List<BeeBuilder> bees) {
        for (BeeBuilder bee : bees) {
            String firstUid = bee.getUid();
            BeeMutation mutation = new BeeMutation(firstUid, SpeciesUids.COMMON, SpeciesUids.CULTIVATED, CULTIVATED_MUTATION_CHANCE);
            plugin.getBeeRegistry().getBeeMutationTree().registerMutation(mutation);
        }
    }

    public static BeeBuilder createFarmingBee(String uid, double mutationChance) {
        return new BeeBuilder(uid, ChatColor.GREEN)
                .addMutation(SpeciesUids.FARMER, SpeciesUids.CULTIVATED, mutationChance)
                .addGroupInformation(FARMING_GROUP);
    }

}


