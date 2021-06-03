package cz.martinbrom.slimybees.core.genetics;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpeciesImpl;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;
import cz.martinbrom.slimybees.items.bees.AbstractBee;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

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

        // TODO: 30.05.21 Use the fertility value as average count for a normal distribution, not THE count
        int childrenCount = ThreadLocalRandom.current().nextBoolean()
                ? firstGenome.getFertility()
                : secondGenome.getFertility();

        ItemStack[] output = new ItemStack[childrenCount];
        for (int i = 0; i < childrenCount; i++) {
            Genome outputGenome = combineGenomes(firstGenome, secondGenome);
            ItemStack itemStack = outputGenome.getSpecies().getUnknownItemStack().clone();

            updateItemGenome(itemStack, outputGenome);
            output[i] = itemStack;
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
        Validate.notNull(itemStack, "Cannot set a genome for a null ItemStack!");
        Validate.notNull(genome, "Cannot set a null genome to an ItemStack!");

        SlimyBeesPlugin.instance().getBeeTypeService().setItemData(itemStack, genome.serialize());
    }

    private static Genome combineGenomes(Genome firstGenome, Genome secondGenome) {
        Chromosome[] firstChromosomes = firstGenome.getChromosomes();
        Chromosome[] secondChromosomes = secondGenome.getChromosomes();

        AlleleSpeciesImpl firstSpecies = (AlleleSpeciesImpl) firstChromosomes[ChromosomeTypeImpl.SPECIES.ordinal()].getActiveAllele();
        AlleleSpeciesImpl secondSpecies = (AlleleSpeciesImpl) secondChromosomes[ChromosomeTypeImpl.SPECIES.ordinal()].getActiveAllele();

        firstChromosomes = tryMutate(firstChromosomes, firstSpecies.getUid(), secondSpecies.getUid());

        Chromosome[] finalChromosomes = new Chromosome[firstChromosomes.length];
        for (int i = 0; i < firstChromosomes.length; i++) {
            finalChromosomes[i] = combineChromosomes(firstChromosomes[i], secondChromosomes[i]);
        }

        return new Genome(finalChromosomes);
    }

    @Nonnull
    private static Chromosome[] tryMutate(Chromosome[] chromosomes, String firstParentUid, String secondParentUid) {
        BeeRegistry beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        List<BeeMutation> mutations = beeRegistry.getBeeMutationTree().getMutationForParents(firstParentUid, secondParentUid);
        // TODO: 03.06.21 Shuffle mutations
        for (BeeMutation mutation : mutations) {
            if (mutation != null && ThreadLocalRandom.current().nextDouble() < mutation.getChance()) {
                Allele[] template = beeRegistry.getTemplate(mutation.getChild());
                if (template != null) {
                    chromosomes = getChromosomesFromAlleles(template);
                    break;
                }
            }
        }

        return chromosomes;
    }

    @Nonnull
    private static Chromosome combineChromosomes(Chromosome firstChromosome, Chromosome secondChromosome) {
        Allele firstAllele = ThreadLocalRandom.current().nextBoolean()
                ? firstChromosome.getPrimaryAllele()
                : firstChromosome.getSecondaryAllele();

        Allele secondAllele = ThreadLocalRandom.current().nextBoolean()
                ? secondChromosome.getPrimaryAllele()
                : secondChromosome.getSecondaryAllele();

        return ThreadLocalRandom.current().nextBoolean()
                ? new Chromosome(firstAllele, secondAllele)
                : new Chromosome(secondAllele, firstAllele);
    }

    // TODO: 02.06.21 Move somewhere
    private static Chromosome[] getChromosomesFromAlleles(Allele[] alleles) {
        Chromosome[] chromosomes = new Chromosome[alleles.length];
        for (int i = 0; i < alleles.length; i++) {
            chromosomes[i] = new Chromosome(alleles[i]);
        }

        return chromosomes;
    }

    // TODO: 02.06.21 Move somewhere
    public static Genome getGenomeFromAlleles(Allele[] alleles) {
        Chromosome[] chromosomes = getChromosomesFromAlleles(alleles);

        return new Genome(chromosomes);
    }

}
