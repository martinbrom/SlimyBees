package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpeciesImpl;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;
import cz.martinbrom.slimybees.items.bees.BeeNest;
import cz.martinbrom.slimybees.items.bees.Drone;
import cz.martinbrom.slimybees.items.bees.Princess;
import cz.martinbrom.slimybees.utils.GeneticUtil;
import cz.martinbrom.slimybees.utils.StringUtils;
import cz.martinbrom.slimybees.utils.Triple;
import cz.martinbrom.slimybees.worldgen.AbstractNestPopulator;
import cz.martinbrom.slimybees.worldgen.GroundNestPopulator;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

@ParametersAreNonnullByDefault
public class BeeBuilder {

    private final String uid;
    private final String name;
    private final ChatColor color;
    private final boolean dominant;
    private final List<ChanceItemStack> products;
    private final Allele[] template;
    private final List<Triple<String, String, Double>> mutations;

    private boolean enchanted;

    private Biome[] nestBiomes;
    private Material[] nestFloorMaterials;
    private double nestSpawnChance;

    public BeeBuilder(String name, ChatColor color) {
        this(name, color, false);
    }

    public BeeBuilder(String name, ChatColor color, boolean dominant) {
        this.name = StringUtils.capitalize(name);
        this.uid = GeneticUtil.speciesNameToUid(name);
        this.color = color;
        this.dominant = dominant;

        products = new ArrayList<>();
        mutations = new ArrayList<>();

        template = SlimyBeesPlugin.instance().getBeeRegistry().getDefaultTemplate();
    }

    public String getUid() {
        return uid;
    }

    public boolean isNesting() {
        return nestBiomes != null;
    }

    public BeeBuilder setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;
        return this;
    }

    public BeeBuilder addProduct(ItemStack item, double chance) {
        products.add(new ChanceItemStack(item, chance));
        return this;
    }

    public BeeBuilder addDefaultAlleleValue(ChromosomeType chromosomeType, String uid) {
        Validate.notNull(chromosomeType, "Cannot set an allele value for null chromosome type!");
        if (chromosomeType == ChromosomeType.SPECIES) {
            throw new IllegalArgumentException("Cannot set the species chromosome directly! It is done automatically!");
        }

        SlimyBeesPlugin.getAlleleService().set(template, chromosomeType, uid);
        return this;
    }

    public BeeBuilder addMutation(String firstParentUid, String secondParentUid, double chance) {
        Validate.notEmpty(firstParentUid, "The uid of the first parent cannot be empty or null!");
        Validate.notEmpty(secondParentUid, "The uid of the second parent cannot be empty or null!");
        // TODO: 27.06.21 Chance validation?
        // if nest set -> error

        mutations.add(new Triple<>(firstParentUid, secondParentUid, chance));
        return this;
    }

    public BeeBuilder addNest(Biome[] validBiomes, Material[] validFloorMaterials, double chance) {
        Validate.notEmpty(validBiomes, "The valid biomes for a nest cannot be empty or null!");
        Validate.notEmpty(validFloorMaterials, "The floor materials for a nest cannot be empty or null!");
        // TODO: 27.06.21 Chance validation?

        // if mutations not empty -> error

        nestBiomes = validBiomes;
        nestFloorMaterials = validFloorMaterials;
        nestSpawnChance = chance;

        return this;
    }

    public void register(SlimyBeesPlugin plugin) {
        AlleleSpecies species = new AlleleSpeciesImpl(uid, name, dominant);
        species.setProducts(products);

        SlimyBeesPlugin.getAlleleRegistry().register(ChromosomeType.SPECIES, species);

        BeeRegistry beeRegistry = plugin.getBeeRegistry();
        SlimyBeesPlugin.getAlleleService().set(template, ChromosomeType.SPECIES, uid);
        beeRegistry.registerTemplate(template);

        BeeGeneticService geneticService = SlimyBeesPlugin.getBeeGeneticService();
        Genome genome = geneticService.getGenomeFromAlleles(template);

        registerItemStacks(plugin, genome);
        registerNest(plugin, species);
    }

    public void registerNest(SlimyBeesPlugin plugin, AlleleSpecies species) {
        if (isNesting()) {
            SlimefunItemStack nestItemStack = new SlimefunItemStack(
                    species.getName().toUpperCase(Locale.ROOT) + "_BEE_NEST",
                    Material.BEEHIVE,
                    species.getName() + " Bee Nest");
            AbstractNestPopulator populator = new GroundNestPopulator(nestBiomes, nestFloorMaterials, nestSpawnChance, nestItemStack);

            BeeNest nest = new BeeNest(nestItemStack, species.getPrincessItemStack(), species.getDroneItemStack());
            nest.addRandomDrop(new RandomizedItemStack(ItemStacks.HONEY_COMB, 1, 3));

            nest.register(plugin);
            nest.setHidden(true);
            populator.register(plugin);
        }
    }

    public void postRegister(SlimyBeesPlugin plugin) {
        for (Triple<String, String, Double> dto : mutations) {
            BeeMutation mutation = new BeeMutation(dto.getFirst(), dto.getSecond(), uid, dto.getThird());
            plugin.getBeeRegistry().getBeeMutationTree().registerMutation(mutation);
        }
    }

    private void registerItemStacks(SlimyBeesPlugin plugin, Genome genome) {
        AlleleSpecies species = genome.getSpecies();
        String coloredName = color + species.getName();
        String uppercaseName = species.getName().toUpperCase(Locale.ROOT);

        SlimefunItemStack princessStack = ItemStacks.createPrincess(uppercaseName, coloredName, enchanted, "");
        SlimefunItemStack droneStack = ItemStacks.createDrone(uppercaseName, coloredName, enchanted, "");

        // TODO: 01.07.21 Cleaner way to update?
        BeeLoreService loreService = SlimyBeesPlugin.getBeeLoreService();
        princessStack = new SlimefunItemStack(princessStack.getItemId(), loreService.updateLore(princessStack, genome));
        droneStack = new SlimefunItemStack(droneStack.getItemId(), loreService.updateLore(droneStack, genome));

        BeeGeneticService geneticService = SlimyBeesPlugin.getBeeGeneticService();
        geneticService.updateItemGenome(princessStack, genome);
        geneticService.updateItemGenome(droneStack, genome);

        Princess princess = new Princess(Categories.GENERAL, princessStack, RecipeTypes.BREEDING, ItemStacks.CONSULT_BEE_ATLAS);
        Drone drone = new Drone(Categories.GENERAL, droneStack, RecipeTypes.BREEDING, ItemStacks.CONSULT_BEE_ATLAS);

        princess.register(plugin);
        princess.setHidden(true);
        drone.register(plugin);
        drone.setHidden(true);

        species.setPrincessItemStack(princessStack);
        species.setDroneItemStack(droneStack);
    }

}
