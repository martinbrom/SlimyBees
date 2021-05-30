package cz.martinbrom.slimybees.core;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.BeeMutation;
import cz.martinbrom.slimybees.core.genetics.ChromosomeType;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.GenomeBuilder;
import cz.martinbrom.slimybees.items.bees.AnalyzedBee;
import cz.martinbrom.slimybees.items.bees.BeeNest;
import cz.martinbrom.slimybees.items.bees.UnknownBee;
import cz.martinbrom.slimybees.setup.ItemSetup;
import cz.martinbrom.slimybees.worldgen.AbstractNestPopulator;
import cz.martinbrom.slimybees.worldgen.GroundNestPopulator;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

public class BeeBuilder {

    private final String id;
    private String name;
    private SlimefunItemStack analyzedBeeItemStack;
    private SlimefunItemStack unknownBeeItemStack;
    private SlimefunItemStack honeycombItemStack;
    private SlimefunItemStack nestItemStack;
    private AbstractNestPopulator populator;
    private BeeMutation mutation;

    private GenomeBuilder genomeBuilder;

    private BeeBuilder(String id) {
        this.id = id;
    }

    public static BeeBuilder of(String id) {
        BeeBuilder beeBuilder = new BeeBuilder(id);
        beeBuilder.genomeBuilder = new GenomeBuilder(id);
        return beeBuilder;
    }

    public BeeBuilder setName(String name) {
        Validate.notNull(id, "You must set the bee's id before setting a name!");
        this.name = name;
        unknownBeeItemStack = ItemStacks.createBee("_UNKNOWN_" + id, name, "", "&8<unknown>");
        analyzedBeeItemStack = ItemStacks.createBee(id, name);
        honeycombItemStack = ItemStacks.createHoneycomb(id, name);
        return this;
    }

    public BeeBuilder setNest(Biome[] validBiomes, Material[] validFloorMaterials, double chance) {
        Validate.isTrue(mutation == null, "You can only set a nest or a mutation, not both!");
        Validate.notNull(id, "You must set the bee's id before setting a nest!");
        Validate.notNull(name, "You must set the bee's name before setting a nest!");
        nestItemStack = new SlimefunItemStack(
                id + "_BEE_NEST",
                Material.BEEHIVE,
                name + " Bee Nest");
        populator = new GroundNestPopulator(validBiomes, validFloorMaterials, chance, nestItemStack);
        return this;
    }

    public BeeBuilder setMutation(String firstParent, String secondParent, double chance) {
        Validate.isTrue(populator == null, "You can only set a nest or a mutation, not both!");
        Validate.notNull(id, "You must set the bee's id before setting a mutation!");
        this.mutation = new BeeMutation(firstParent, secondParent, id, chance);
        return this;
    }

    public BeeBuilder setDefaultChromosome(ChromosomeType type, Object value) {
        genomeBuilder.setDefaultChromosome(type, value);
        return this;
    }

    public void register(SlimyBeesPlugin plugin) {
        // TODO: 29.05.21 Move most of the validations and instance creation here

        SlimyBeesRegistry registry = SlimyBeesPlugin.getRegistry();

        Genome genome = genomeBuilder.build();
        BeeGeneticService.updateItemGenome(unknownBeeItemStack, genome);
        BeeGeneticService.updateItemGenome(analyzedBeeItemStack, genome);

        UnknownBee unknownBee = new UnknownBee(Categories.GENERAL, unknownBeeItemStack, RecipeType.NULL, new ItemStack[9]);
        AnalyzedBee analyzedBee = new AnalyzedBee(Categories.GENERAL, analyzedBeeItemStack, RecipeType.NULL, new ItemStack[9]);

        registry.getBeeTypes().put(genome.getSpeciesValue(), new Pair<>(analyzedBee, unknownBee));

        unknownBee.register(plugin);
//        unknownBee.setHidden(true);
        analyzedBee.register(plugin);
//        analyzedBee.setHidden(true);

        if (honeycombItemStack != null) {
            ItemSetup.registerAndHide(honeycombItemStack, plugin);
        }

        if (mutation != null) {
            registry.getBeeMutationTree().registerMutation(mutation);
        }

        if (populator != null) {
            BeeNest nest = new BeeNest(nestItemStack, unknownBeeItemStack)
                    .addRandomDrop(new RandomizedSlimefunItemStack(honeycombItemStack, 0, 1))
                    .addRandomDrop(new RandomizedSlimefunItemStack(ItemStacks.COMMON_HONEYCOMB, 0, 3));
            nest.register(plugin);
            nest.setHidden(true);
            registry.getPopulators().add(populator);
        }
    }

}
