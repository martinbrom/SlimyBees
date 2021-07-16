package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.RecipeTypes;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.BeeMutation;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;
import cz.martinbrom.slimybees.items.bees.BeeNest;
import cz.martinbrom.slimybees.items.bees.Drone;
import cz.martinbrom.slimybees.items.bees.Princess;
import cz.martinbrom.slimybees.utils.PatternUtil;
import cz.martinbrom.slimybees.utils.StringUtils;
import cz.martinbrom.slimybees.utils.types.Triple;
import cz.martinbrom.slimybees.worldgen.NestDTO;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This class is used to easily register bee species with everything related to it.
 */
@ParametersAreNonnullByDefault
public class BeeBuilder {

    private final AlleleService alleleService;
    private final AlleleRegistry alleleRegistry;
    private final BeeRegistry beeRegistry;
    private final SlimyBeesRegistry registry;
    private final BeeGeneticService geneticService;
    private final BeeLoreService loreService;

    private final String uid;
    private final String name;
    private final ChatColor color;
    private final boolean dominant;
    private final List<ChanceItemStack> products;
    private final Allele[] partialTemplate;
    private final List<Triple<String, String, Double>> mutations;

    private boolean enchanted;
    private boolean alwaysVisible;

    private NestDTO nest;

    public BeeBuilder(String uid, ChatColor color) {
        this(uid, color, false);
    }

    public BeeBuilder(String uid, ChatColor color, boolean dominant) {
        Validate.notEmpty(uid, "The bee uid must not be null or empty!");
        Validate.isTrue(PatternUtil.SPECIES_UID_PATTERN.matcher(uid).matches(), "The bee uid must start with the species prefix " +
                "and be in the lower snake case format, got " + uid + "!");
        Validate.notNull(color, "The bee color must not be null!");

        alleleService = SlimyBeesPlugin.getAlleleService();
        alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();
        beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        registry = SlimyBeesPlugin.getRegistry();
        geneticService = SlimyBeesPlugin.getBeeGeneticService();
        loreService = SlimyBeesPlugin.getBeeLoreService();

        this.name = StringUtils.uidToName(uid);
        this.uid = uid;
        this.color = color;
        this.dominant = dominant;

        products = new ArrayList<>();
        mutations = new ArrayList<>();

        partialTemplate = new Allele[ChromosomeType.CHROMOSOME_COUNT];
    }

    @Nonnull
    public String getUid() {
        return uid;
    }

    public boolean isNesting() {
        return nest != null;
    }

    /**
     * Marks the bee as always visible.
     * This means that the bee detail page will be visible in the BeeAtlas
     * even if the player didn't discover this species yet.
     * Can be turned off in the configuration.
     *
     * @param alwaysVisible If the bee should be always visible or not
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder setAlwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;

        return this;
    }

    /**
     * Marks the bee as enchanted (used for top tier species).
     * This applies a hidden enchantment to the bee {@link ItemStack}.
     *
     * @param enchanted If the bee should be enchanted or not
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;

        return this;
    }

    /**
     * Adds a product with given chance to the bee species.
     *
     * @param item The {@link ItemStack} representing the bee product
     * @param chance The base chance that the bee will produce this item during one production cycle
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addProduct(ItemStack item, double chance) {
        products.add(new ChanceItemStack(item, chance));
        return this;
    }

    /**
     * Utility method to call multiple builder methods from one variable.
     * Useful for example for adding same genes for multiple bees belonging to the same branch.
     *
     * @param groupDefinition Function(s) to update this {@link BeeBuilder} with
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addGroupInformation(Consumer<BeeBuilder> groupDefinition) {
        Validate.notNull(groupDefinition, "Cannot update BeeBuilder by null group definition!");

        groupDefinition.accept(this);
        return this;
    }

    /**
     * Adds an {@link Allele} identified by given uid and {@link ChromosomeType} to the bee species allele template.
     *
     * @param chromosomeType The {@link ChromosomeType} to update with the {@link Allele}
     * @param uid The identifier of the {@link Allele} to set
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addDefaultAlleleValue(ChromosomeType chromosomeType, String uid) {
        Validate.notNull(chromosomeType, "Cannot set an allele value for null chromosome type!");
        if (chromosomeType == ChromosomeType.SPECIES) {
            throw new IllegalArgumentException("Cannot set the species chromosome directly! It is done automatically!");
        }

        alleleService.set(partialTemplate, chromosomeType, uid);
        return this;
    }

    /**
     * Adds a mutation for this bee by specifying both parents and the chance that the mutation will happen.
     *
     * @param firstParentUid The uid of the first parent
     * @param secondParentUid The uid of the second parent
     * @param chance The base chance that the mutation will happen
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addMutation(String firstParentUid, String secondParentUid, double chance) {
        Validate.notEmpty(firstParentUid, "The uid of the first parent cannot be empty or null!");
        Validate.notEmpty(secondParentUid, "The uid of the second parent cannot be empty or null!");

        mutations.add(new Triple<>(firstParentUid, secondParentUid, chance));
        return this;
    }

    /**
     * Adds a naturally spawning nest for this bee.
     * Also marks the bee as always visible!
     *
     * @param env The {@link World.Environment} that the nest can spawn in
     * @param validBiomes The {@link Biome}s that the nest can spawn in
     * @param validFloorMaterials The {@link Material}s that the nest can spawn on
     * @param chance The chance that the nest will spawn in a chunk with the correct biome
     * @return The {@link BeeBuilder} instance for call chaining
     */
    @Nonnull
    public BeeBuilder addNest(World.Environment env, Biome[] validBiomes, Material[] validFloorMaterials, double chance) {
        nest = new NestDTO(env, validBiomes, validFloorMaterials, chance);

        setAlwaysVisible(true);
        return this;
    }

    /**
     * Creates and registers everything needed for this bee species.
     *
     * @param plugin The {@link SlimyBeesPlugin} instance
     */
    public void register(SlimyBeesPlugin plugin) {
        AlleleSpecies species = new AlleleSpecies(uid, name, dominant);
        species.setProducts(products);

        alleleRegistry.register(ChromosomeType.SPECIES, species);

        alleleService.set(partialTemplate, ChromosomeType.SPECIES, uid);
        beeRegistry.registerPartialTemplate(partialTemplate);
        if (alwaysVisible) {
            beeRegistry.registerAlwaysDisplayedSpecies(species);
        }

        Genome genome = geneticService.getGenomeFromAlleles(beeRegistry.getFullTemplate(uid));

        registerItemStacks(plugin, genome);
        registerNest(plugin, species);
        registerMutations();
    }

    private void registerMutations() {
        for (Triple<String, String, Double> dto : mutations) {
            BeeMutation mutation = new BeeMutation(dto.getFirst(), dto.getSecond(), uid, dto.getThird());
            beeRegistry.getBeeMutationTree().registerMutation(mutation);
        }
    }

    private void registerItemStacks(SlimyBeesPlugin plugin, Genome genome) {
        AlleleSpecies species = genome.getSpecies();
        String coloredName = color + species.getDisplayName();

        SlimefunItemStack princessStack = ItemStacks.createPrincess(species.getName(), coloredName, enchanted, "");
        SlimefunItemStack droneStack = ItemStacks.createDrone(species.getName(), coloredName, enchanted, "");

        // TODO: 01.07.21 Cleaner way to update?
        princessStack = new SlimefunItemStack(princessStack.getItemId(), loreService.updateLore(princessStack, genome));
        droneStack = new SlimefunItemStack(droneStack.getItemId(), loreService.updateLore(droneStack, genome));

        geneticService.updateItemGenome(princessStack, genome);
        geneticService.updateItemGenome(droneStack, genome);

        Princess princess = new Princess(Categories.ITEMS, princessStack, RecipeTypes.BREEDING, ItemStacks.CONSULT_BEE_ATLAS_RECIPE);
        Drone drone = new Drone(Categories.ITEMS, droneStack, RecipeTypes.BREEDING, ItemStacks.CONSULT_BEE_ATLAS_RECIPE);

        princess.register(plugin);
        princess.setHidden(true);
        drone.register(plugin);
        drone.setHidden(true);

        species.setPrincessItemStack(princessStack);
        species.setDroneItemStack(droneStack);
    }

    private void registerNest(SlimyBeesPlugin plugin, AlleleSpecies species) {
        if (isNesting()) {
            SlimefunItemStack nestItemStack = new SlimefunItemStack(
                    species.getName() + "_BEE_NEST",
                    Material.BEEHIVE,
                    color + species.getDisplayName() + " Bee Nest");

            BeeNest nestBlock = new BeeNest(nestItemStack, species.getPrincessItemStack(), species.getDroneItemStack());
            nestBlock.addRandomDrop(new RandomizedItemStack(ItemStacks.HONEY_COMB, 1, 3));

            nestBlock.register(plugin);
            nestBlock.setHidden(true);

            nest.setItemStack(nestItemStack);
            registry.registerNest(nest);
        }
    }

}
