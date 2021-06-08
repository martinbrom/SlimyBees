package cz.martinbrom.slimybees.core.genetics.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.BiomeSets;
import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.RandomizedItemStack;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.BeeMutation;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleHelper;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpeciesImpl;
import cz.martinbrom.slimybees.items.bees.BeeNest;
import cz.martinbrom.slimybees.items.bees.Drone;
import cz.martinbrom.slimybees.items.bees.Princess;
import cz.martinbrom.slimybees.utils.StringUtils;
import cz.martinbrom.slimybees.worldgen.AbstractNestPopulator;
import cz.martinbrom.slimybees.worldgen.GroundNestPopulator;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

@ParametersAreNonnullByDefault
public enum BeeType {

    // <editor-fold desc="Nesting" defaultstate="collapsed">
    FOREST(true, ChatColor.DARK_GREEN) {
        @Override
        protected void registerNest() {
            registerNest(BiomeSets.MILD_FORESTS,
                    new Material[] { Material.GRASS_BLOCK, Material.DIRT },
                    0.025);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.HONEY_COMB, 0.15));
        }
    },
    STONE(true, ChatColor.GRAY) {
        @Override
        protected void registerNest() {
            registerNest(BiomeSets.MOUNTAINS,
                    new Material[] { Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.GRAVEL },
                    0.035);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.DRY_COMB, 0.15));
        }
    },
    SANDY(true, ChatColor.YELLOW) {
        @Override
        protected void registerNest() {
            registerNest(BiomeSets.DESERTS,
                    new Material[] { Material.SAND, Material.RED_SAND, Material.COARSE_DIRT },
                    0.015);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.DRY_COMB, 0.15));
        }
    },
    WATER(true, ChatColor.DARK_BLUE) {
        @Override
        protected void registerNest() {
            // TODO: 03.06.21 Find a way to spawn on the sea floor
            registerNest(BiomeSets.BODIES_OF_WATER,
                    new Material[] { Material.WATER },
                    0.005);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.HONEY_COMB, 0.15));
        }
    },
    NETHER(true, ChatColor.DARK_RED) {
        @Override
        protected void registerNest() {
            registerNest(BiomeSets.RED_NETHER,
                    new Material[] { Material.NETHERRACK, Material.CRIMSON_NYLIUM },
                    0.01);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.DRY_COMB, 0.15));
        }
    },
    // </editor-fold>

    // <editor-fold desc="Base" defaultstate="collapsed">
    COMMON(false, ChatColor.WHITE) {
        @Override
        protected void registerMutations() {
            int nestingBeeCount = NESTING_BEES.length;
            for (int i = 0; i < nestingBeeCount; i++) {
                for (int j = i + 1; j < nestingBeeCount; j++) {
                    registerMutation(NESTING_BEES[i], NESTING_BEES[j], 0.25);
                }
            }
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.HONEY_COMB, 0.2));
        }
    },
    CULTIVATED(true, ChatColor.AQUA) {
        @Override
        protected void registerMutations() {
            for (BeeType bee : NESTING_BEES) {
                registerMutation(bee, COMMON, 0.2);
            }
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.HONEY_COMB, 0.3));
        }
    },
    NOBLE(false, ChatColor.GOLD) {
        @Override
        protected void registerMutations() {
            registerMutation(CULTIVATED, COMMON, 0.15);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.SWEET_COMB, 0.2));
        }
    },
    MAJESTIC(true, ChatColor.GOLD) {
        @Override
        protected void registerMutations() {
            registerMutation(NOBLE, CULTIVATED, 0.1);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.SWEET_COMB, 0.3));
        }

        @Override
        protected void setAlleles(Allele[] alleles) {
            AlleleHelper.set(alleles, ChromosomeTypeImpl.FERTILITY, AlleleType.Fertility.VERY_HIGH);
        }
    },
    IMPERIAL(false, ChatColor.GOLD, true) {
        @Override
        protected void registerMutations() {
            registerMutation(MAJESTIC, NOBLE, 0.05);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.SWEET_COMB, 0.2));
            products.add(new Pair<>(ItemStacks.ROYAL_JELLY, 0.05));
        }
    },
    DILIGENT(false, ChatColor.YELLOW) {
        @Override
        protected void registerMutations() {
            registerMutation(CULTIVATED, COMMON, 0.15);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.DRY_COMB, 0.2));
        }
    },
    UNWEARY(true, ChatColor.YELLOW) {
        @Override
        protected void registerMutations() {
            registerMutation(DILIGENT, CULTIVATED, 0.1);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.DRY_COMB, 0.3));
        }

        @Override
        protected void setAlleles(Allele[] alleles) {
            AlleleHelper.set(alleles, ChromosomeTypeImpl.FERTILITY, AlleleType.Fertility.LOW);
        }
    },
    INDUSTRIOUS(false, ChatColor.YELLOW, true) {
        @Override
        protected void registerMutations() {
            registerMutation(UNWEARY, DILIGENT, 0.05);
        }

        @Override
        protected void setProducts(List<Pair<ItemStack, Double>> products) {
            products.add(new Pair<>(ItemStacks.DRY_COMB, 0.2));
            products.add(new Pair<>(ItemStacks.POLLEN, 0.05));
        }
    },
    // </editor-fold>
    ;

    private static final BeeType[] NESTING_BEES = { FOREST, STONE, SANDY, WATER, NETHER };

    private static boolean initialized = false;

    private final AlleleSpecies species;
    private final ChatColor color;

    private Genome genome;
    private Allele[] template;

    BeeType(boolean dominant, ChatColor color) {
        this(dominant, color, false);
    }

    BeeType(boolean dominant, ChatColor color, boolean enchanted) {
        Validate.notNull(color, "BeeType color cannot be null!");

        // TODO: 08.06.21 Enchant item stacks
        this.color = color;

        String lowercaseName = toString().toLowerCase(Locale.ROOT);
        String name = StringUtils.capitalize(lowercaseName);
        String uid = "species." + lowercaseName;

        species = new AlleleSpeciesImpl(uid, name, dominant);

        List<Pair<ItemStack, Double>> products = new ArrayList<>();
        setProducts(products);
        species.setProducts(products);

        SlimyBeesPlugin.getAlleleRegistry().registerAllele(species, ChromosomeTypeImpl.SPECIES);
    }

    public static void setUp() {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees bees can only be registered once!");
        }

        initialized = true;

        for (BeeType type : values()) {
            type.register();
            type.registerNest();
        }

        for (BeeType type : values()) {
            type.registerMutations();
        }
    }

    @Nonnull
    public final Allele[] getTemplate() {
        return template;
    }

    protected void setProducts(List<Pair<ItemStack, Double>> products) {
        // default does nothing
    }

    protected void setAlleles(Allele[] alleles) {
        // default does nothing
    }

    protected void registerMutations() {
        // default does nothing
    }

    protected void registerNest() {
        // default does nothing
    }

    protected final void registerNest(Biome[] validBiomes, Material[] validFloorMaterials, double chance) {
        SlimefunItemStack nestItemStack = new SlimefunItemStack(
                species.getName().toUpperCase(Locale.ROOT) + "_BEE_NEST",
                Material.BEEHIVE,
                species.getName() + " Bee Nest");
        AbstractNestPopulator populator = new GroundNestPopulator(validBiomes, validFloorMaterials, chance, nestItemStack);

        BeeNest nest = new BeeNest(nestItemStack, species.getPrincessItemStack(), species.getDroneItemStack());
        nest.addRandomDrop(new RandomizedItemStack(ItemStacks.HONEY_COMB, 1, 3));

        nest.register(SlimyBeesPlugin.instance());
        nest.setHidden(true);
        SlimyBeesPlugin.getRegistry().getPopulators().add(populator);
    }

    protected final void registerMutation(BeeType firstParent, BeeType secondParent, double chance) {
        String firstParentUid = firstParent.species.getUid();
        String secondParentUid = secondParent.species.getUid();
        String childUid = species.getUid();

        BeeMutation mutation = new BeeMutation(firstParentUid, secondParentUid, childUid, chance);
        SlimyBeesPlugin.getBeeRegistry().getBeeMutationTree().registerMutation(mutation);
    }

    private void register() {
        BeeRegistry beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        template = beeRegistry.getDefaultTemplate();

        setAlleles(template);
        AlleleHelper.set(template, ChromosomeTypeImpl.SPECIES, species);

        BeeGeneticService geneticService = SlimyBeesPlugin.getBeeGeneticService();
        genome = geneticService.getGenomeFromAlleles(template);

        registerItemStacks();

        beeRegistry.registerTemplate(template);
    }

    private void registerItemStacks() {
        String coloredName = color + species.getName();
        String uppercaseName = species.getName().toUpperCase(Locale.ROOT);

        SlimefunItemStack princessStack = ItemStacks.createPrincess(uppercaseName, coloredName, "");
        SlimefunItemStack droneStack = ItemStacks.createDrone(uppercaseName, coloredName, "");

        BeeGeneticService geneticService = SlimyBeesPlugin.getBeeGeneticService();

        geneticService.updateItemGenome(princessStack, genome);
        geneticService.updateItemGenome(droneStack, genome);

        Princess princess = new Princess(Categories.GENERAL, princessStack, RecipeType.NULL, new ItemStack[9]);
        Drone drone = new Drone(Categories.GENERAL, droneStack, RecipeType.NULL, new ItemStack[9]);

        SlimyBeesPlugin plugin = SlimyBeesPlugin.instance();

        princess.register(plugin);
        princess.setHidden(true);
        drone.register(plugin);
        drone.setHidden(true);

        species.setPrincessItemStack(princessStack);
        species.setDroneItemStack(droneStack);
    }

}
