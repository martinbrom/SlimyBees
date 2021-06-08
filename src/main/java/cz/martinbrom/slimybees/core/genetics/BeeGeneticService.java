package cz.martinbrom.slimybees.core.genetics;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpeciesImpl;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;
import cz.martinbrom.slimybees.items.bees.AbstractBee;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

/**
 * This service handles gene-related logic
 *
 * @see Genome
 * @see Chromosome
 * @see Allele
 */
@ParametersAreNonnullByDefault
public class BeeGeneticService {

    private final CustomItemDataService beeTypeService;
    private final BeeLoreService beeLoreService;

    public BeeGeneticService(CustomItemDataService beeTypeService, BeeLoreService beeLoreService) {
        this.beeTypeService = beeTypeService;
        this.beeLoreService = beeLoreService;
    }

    /**
     * Performs a breeding process for two parents represented by given {@link ItemStack}s.
     * Returns {@link ItemStack} for each child.
     *
     * @param firstItemStack  The first parent
     * @param secondItemStack The second parent
     * @return {@link ItemStack} for each child created by the breeding process
     */
    @Nullable
    public ItemStack[] getChildren(ItemStack firstItemStack, ItemStack secondItemStack) {
        Validate.notNull(firstItemStack, "The first parent must not be null!");
        Validate.notNull(secondItemStack, "The second parent must not be null!");

        // we do not need to check for the type of ItemStack's SlimefunItem, 'getGenome' does that already
        Genome firstGenome = getGenome(firstItemStack);
        Genome secondGenome = getGenome(secondItemStack);

        if (firstGenome == null || secondGenome == null) {
            return null;
        }

        int fertilityValue = ThreadLocalRandom.current().nextBoolean()
                ? firstGenome.getFertilityValue()
                : secondGenome.getFertilityValue();
        int childrenCount = 1 + ThreadLocalRandom.current().nextInt(fertilityValue);

        // +1 because we also need to add a princess (it should also come first)
        ItemStack[] output = new ItemStack[childrenCount + 1];
        for (int i = 0; i < childrenCount + 1; i++) {
            Genome outputGenome = combineGenomes(firstGenome, secondGenome);
            AlleleSpecies species = outputGenome.getSpecies();

            ItemStack item = i == 0 ? species.getPrincessItemStack() : species.getDroneItemStack();
            ItemStack copy = beeLoreService.makeUnknown(item);

            updateItemGenome(copy, outputGenome);
            output[i] = copy;
        }

        return output;
    }


    /**
     * Tries to load a {@link Genome} for a given {@link ItemStack}.
     *
     * @param item The {@link ItemStack} to load the {@link Genome} for
     * @return The {@link Genome} stored in a given {@link ItemStack} if there is any, null otherwise
     */
    @Nullable
    public Genome getGenome(@Nullable ItemStack item) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (!(sfItem instanceof AbstractBee)) {
            return null;
        }

        Optional<String> genomeStr = beeTypeService.getItemData(item);

        return genomeStr.map(Genome::new).orElse(null);
    }

    /**
     * Tries to load a {@link Genome} for a given {@link AlleleSpecies}.
     *
     * @param species The {@link AlleleSpecies} to load the {@link Genome} for
     * @return The {@link Genome} created from the {@link AlleleSpecies}'s template if there is any, null otherwise
     */
    @Nullable
    public Genome getGenome(AlleleSpecies species) {
        Validate.notNull(species, "Cannot get a genome for a null species!");

        BeeRegistry registry = SlimyBeesPlugin.getBeeRegistry();
        Allele[] template = registry.getTemplate(species.getUid());
        if (template != null) {
            return new Genome(getChromosomesFromAlleles(template));
        }

        return null;
    }

    /**
     * Helper method to store a {@link Genome} in a given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to store a given {@link Genome} in
     * @param genome The {@link Genome} to store
     */
    public void updateItemGenome(ItemStack itemStack, Genome genome) {
        Validate.notNull(itemStack, "Cannot set a genome for a null ItemStack!");
        Validate.notNull(genome, "Cannot set a null genome to an ItemStack!");

        beeTypeService.setItemData(itemStack, genome.serialize());
    }

    /**
     * Combines two given {@link Genome}s into a single one.
     * Roughly follows the real-life genetic rules.
     *
     * @param firstGenome The {@link Genome} from the first parent
     * @param secondGenome The {@link Genome} from the second parent
     * @return The {@link Genome} created by merging both parents {@link Genome}s
     */
    @Nonnull
    private Genome combineGenomes(Genome firstGenome, Genome secondGenome) {
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

    /**
     * Tries to find and apply a mutation to a given array of {@link Chromosome}s.
     * The available mutations are determined by the given parents unique ids.
     *
     * @param chromosomes Child {link Chromosome}s to apply the mutation to
     * @param firstParentUid Unique id of the first parent
     * @param secondParentUid Unique id of the second parent
     * @return Updated {@link Chromosome}s
     */
    @Nonnull
    private Chromosome[] tryMutate(Chromosome[] chromosomes, String firstParentUid, String secondParentUid) {
        BeeRegistry beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        List<BeeMutation> mutations = beeRegistry.getBeeMutationTree().getMutationForParents(firstParentUid, secondParentUid);

        Collections.shuffle(mutations);
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

    /**
     * Combines two {@link Chromosome}s into one.
     * Roughly follow the real-life genetic rules.
     *
     * @param firstChromosome The {@link Chromosome} of the first parent
     * @param secondChromosome The {@link Chromosome} of the second parent
     * @return The {@link Chromosome} created by merging both parents {@link Chromosome}s
     */
    @Nonnull
    private Chromosome combineChromosomes(Chromosome firstChromosome, Chromosome secondChromosome) {
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
    private Chromosome[] getChromosomesFromAlleles(Allele[] alleles) {
        Chromosome[] chromosomes = new Chromosome[alleles.length];
        for (int i = 0; i < alleles.length; i++) {
            chromosomes[i] = new Chromosome(alleles[i]);
        }

        return chromosomes;
    }

    // TODO: 02.06.21 Move somewhere
    public Genome getGenomeFromAlleles(Allele[] alleles) {
        Chromosome[] chromosomes = getChromosomesFromAlleles(alleles);

        return new Genome(chromosomes);
    }

}
