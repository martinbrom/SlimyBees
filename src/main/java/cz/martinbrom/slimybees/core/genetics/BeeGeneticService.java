package cz.martinbrom.slimybees.core.genetics;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.SlimyBeesRegistry;
import cz.martinbrom.slimybees.items.bees.AbstractBee;
import cz.martinbrom.slimybees.items.bees.AnalyzedBee;
import cz.martinbrom.slimybees.items.bees.UnknownBee;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

import static cz.martinbrom.slimybees.core.genetics.ChromosomeType.CHROMOSOME_COUNT;

@ParametersAreNonnullByDefault
public class BeeGeneticService {

    // TODO: 01.06.21 Add boolean param 'allowMutations'
    @Nullable
    public static ItemStack[] getChildren(ItemStack firstItemStack, ItemStack secondItemStack) {
        Genome firstGenome = getGenome(firstItemStack);
        Genome secondGenome = getGenome(secondItemStack);

        if (firstGenome == null || secondGenome == null) {
            return null;
        }

//        SlimyBeesPlugin.logger().info("Creating children for parents: ------------");
//        SlimyBeesPlugin.logger().info(firstGenome.serialize());
//        SlimyBeesPlugin.logger().info(secondGenome.serialize());
//        SlimyBeesPlugin.logger().info("-------------------------------------------");

        SlimyBeesRegistry registry = SlimyBeesPlugin.getRegistry();

        // TODO: 30.05.21 Use the fertility value as average count for a normal distribution, not THE count
        int childrenCount = ThreadLocalRandom.current().nextBoolean()
                ? firstGenome.getFertilityValue()
                : secondGenome.getFertilityValue();

        ItemStack[] output = new ItemStack[childrenCount];
        for (int i = 0; i < childrenCount; i++) {
            Genome outputGenome = combineGenomes(firstGenome, secondGenome, true);

            // find the item (by active species value) and update the item data with the correct genome
            Pair<AnalyzedBee, UnknownBee> beePair = registry.getBeeTypes().get(outputGenome.getSpeciesValue());
            if (beePair == null) {
                // TODO: 01.06.21 Null or AIR?
                output[i] = new ItemStack(Material.AIR);
            } else {
                ItemStack itemStack = beePair.getSecondValue().getItem().clone();
                updateItemGenome(itemStack, outputGenome);
                output[i] = itemStack;
            }
        }

        return output;
    }


    @Nullable
    public static Genome getGenome(ItemStack item) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (!(sfItem instanceof AbstractBee)) {
            return null;
        }

        CustomItemDataService beeTypeService = SlimyBeesPlugin.instance().getBeeTypeService();
        Optional<String> genomeStr = beeTypeService.getItemData(item);

        return genomeStr.map(Genome::new).orElse(null);
    }

    public static void updateItemGenome(ItemStack itemStack, Genome genome) {
        SlimyBeesPlugin.instance().getBeeTypeService().setItemData(itemStack, genome.serialize());
    }

    @SuppressWarnings("unchecked")
    private static Genome combineGenomes(Genome firstGenome, Genome secondGenome, boolean allowMutations) {
        Chromosome<Object>[] firstChromosomes = firstGenome.getChromosomes();
        Chromosome<Object>[] secondChromosomes = secondGenome.getChromosomes();

        Chromosome<Object>[] finalChromosomes = new Chromosome[CHROMOSOME_COUNT];
        for (int i = 0; i < CHROMOSOME_COUNT; i++) {
            finalChromosomes[i] = combineChromosomes(firstChromosomes[i], secondChromosomes[i]);
        }

        Genome genome = new Genome(finalChromosomes);
        if (allowMutations) {
            tryApplyMutations(firstGenome, secondGenome, genome);
        }

        return genome;
    }

    private static void tryApplyMutations(Genome firstGenome, Genome secondGenome, Genome childGenome) {
        SlimyBeesRegistry registry = SlimyBeesPlugin.getRegistry();
        BeeMutation mutation = registry.getBeeMutationTree().getMutationForParents(
                firstGenome.getSpeciesValue(), secondGenome.getSpeciesValue());
        if (mutation != null && ThreadLocalRandom.current().nextDouble() < mutation.getChance()) {
            boolean primary = ThreadLocalRandom.current().nextBoolean();
            String newSpecies = mutation.getChild();
            childGenome.setSpeciesValue(newSpecies, primary);
            Map<ChromosomeType, Object> specificChromosomeValues = registry.getSpecificChromosomeValues().get(newSpecies);

            if (specificChromosomeValues != null) {
                specificChromosomeValues.forEach((k, v) -> childGenome.setChromosomeValue(k, v, primary));
            }
        }
    }

    private static Chromosome<Object> combineChromosomes(Chromosome<Object> firstChromosome, Chromosome<Object> secondChromosome) {
        Allele<Object> firstAllele = firstChromosome.getAllele(ThreadLocalRandom.current().nextBoolean());
        Allele<Object> secondAllele = secondChromosome.getAllele(ThreadLocalRandom.current().nextBoolean());

        return ThreadLocalRandom.current().nextBoolean()
            ? new Chromosome<>(firstAllele, secondAllele)
            : new Chromosome<>(secondAllele, firstAllele);
    }

}
