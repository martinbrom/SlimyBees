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

import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpeciesImpl;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.items.bees.AbstractBee;
import cz.martinbrom.slimybees.items.bees.Drone;
import cz.martinbrom.slimybees.items.bees.Princess;
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
    private final BeeRegistry beeRegistry;
    private final GenomeParser genomeParser;

    public BeeGeneticService(CustomItemDataService beeTypeService,
                             BeeLoreService beeLoreService,
                             BeeRegistry beeRegistry,
                             GenomeParser genomeParser) {
        this.beeTypeService = beeTypeService;
        this.beeLoreService = beeLoreService;
        this.beeRegistry = beeRegistry;
        this.genomeParser = genomeParser;
    }

    /**
     * Performs a breeding process for two parents represented by given {@link ItemStack}s.
     * Returns a {@link BreedingResultDTO} containing all needed breeding results.
     *
     * @param firstItemStack  The first parent
     * @param secondItemStack The second parent
     * @return {@link BreedingResultDTO} containing data about the breeding process or null
     */
    @Nullable
    public BreedingResultDTO breed(ItemStack firstItemStack, ItemStack secondItemStack) {
        Validate.notNull(firstItemStack, "The first parent must not be null!");
        Validate.notNull(secondItemStack, "The second parent must not be null!");

        SlimefunItem firstSfItem = SlimefunItem.getByItem(firstItemStack);
        SlimefunItem secondSfItem = SlimefunItem.getByItem(secondItemStack);

        // we need exactly one princess and one drone
        if (firstSfItem instanceof Princess && secondSfItem instanceof Drone
                || firstSfItem instanceof Drone && secondSfItem instanceof Princess) {

            // we can skip the instanceof checks using the unsafe method
            Genome firstGenome = getGenomeUnsafe(firstItemStack);
            Genome secondGenome = getGenomeUnsafe(secondItemStack);

            if (firstGenome == null || secondGenome == null) {
                return null;
            }

            // create drones
            Genome[] genomes = getChildrenGenomes(firstGenome, secondGenome);
            ItemStack[] drones = new ItemStack[genomes.length];
            for (int i = 0; i < genomes.length; i++) {
                drones[i] = createChildItemStack(genomes[i], false);
            }

            // create princess
            Genome princessGenome = combineGenomes(firstGenome, secondGenome);
            ItemStack princess = createChildItemStack(princessGenome, true);

            // TODO: 11.06.21 Hardcoded duration for now, will get changed in later updates
            return new BreedingResultDTO(princess, drones, 60);
        }

        return null;
    }

    /**
     * Performs a breeding process for two parents represented by given {@link Genome}s.
     * Returns a {@link Genome} for each child.
     *
     * @param firstGenome  The first parent's {@link Genome}
     * @param secondGenome The second parent's {@link Genome}
     * @return {@link Genome} for each child created by the breeding process
     */
    public Genome[] getChildrenGenomes(Genome firstGenome, Genome secondGenome) {
        Validate.notNull(firstGenome, "The first genome cannot null!");
        Validate.notNull(secondGenome, "The second genome cannot null!");

        int fertilityValue = ThreadLocalRandom.current().nextBoolean()
                ? firstGenome.getFertilityValue()
                : secondGenome.getFertilityValue();
        int childrenCount = 1 + ThreadLocalRandom.current().nextInt(fertilityValue);

        Genome[] output = new Genome[childrenCount];
        for (int i = 0; i < childrenCount; i++) {
            output[i] = combineGenomes(firstGenome, secondGenome);
        }

        return output;
    }

    /**
     * Tries to load a {@link Genome} for a given {@link ItemStack}.
     * Checks whether the {@link ItemStack} is an {@link AbstractBee}.
     *
     * @param item The {@link ItemStack} to load the {@link Genome} for
     * @return The {@link Genome} stored in a given {@link ItemStack} if there is any, null otherwise
     */
    @Nullable
    public Genome getGenome(ItemStack item) {
        Validate.notNull(item, "Cannot get a genome for a null ItemStack!");

        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (!(sfItem instanceof AbstractBee)) {
            return null;
        }

        return getGenomeUnsafe(item);
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

        Allele[] template = beeRegistry.getTemplate(species.getUid());
        if (template != null) {
            return new Genome(getChromosomesFromAlleles(template));
        }

        return null;
    }

    /**
     * Helper method to store a {@link Genome} in a given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to store a given {@link Genome} in
     * @param genome    The {@link Genome} to store
     */
    public void updateItemGenome(ItemStack itemStack, Genome genome) {
        Validate.notNull(itemStack, "Cannot set a genome for a null ItemStack!");
        Validate.notNull(genome, "Cannot set a null genome to an ItemStack!");

        beeTypeService.setItemData(itemStack, genomeParser.serialize(genome));
    }

    /**
     * Combines two given {@link Genome}s into a single one.
     * Roughly follows the real-life genetic rules.
     *
     * @param firstGenome  The {@link Genome} from the first parent
     * @param secondGenome The {@link Genome} from the second parent
     * @return The {@link Genome} created by merging both parents {@link Genome}s
     */
    @Nonnull
    private Genome combineGenomes(Genome firstGenome, Genome secondGenome) {
        Chromosome[] firstChromosomes = firstGenome.getChromosomes();
        Chromosome[] secondChromosomes = secondGenome.getChromosomes();

        AlleleSpeciesImpl firstSpecies = (AlleleSpeciesImpl) firstChromosomes[ChromosomeType.SPECIES.ordinal()].getActiveAllele();
        AlleleSpeciesImpl secondSpecies = (AlleleSpeciesImpl) secondChromosomes[ChromosomeType.SPECIES.ordinal()].getActiveAllele();

        if (ThreadLocalRandom.current().nextBoolean()) {
            firstChromosomes = tryMutate(firstChromosomes, firstSpecies.getUid(), secondSpecies.getUid());
        } else {
            secondChromosomes = tryMutate(secondChromosomes, firstSpecies.getUid(), secondSpecies.getUid());
        }

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
     * @param chromosomes     Child {link Chromosome}s to apply the mutation to
     * @param firstParentUid  Unique id of the first parent
     * @param secondParentUid Unique id of the second parent
     * @return Updated {@link Chromosome}s
     */
    @Nonnull
    private Chromosome[] tryMutate(Chromosome[] chromosomes, String firstParentUid, String secondParentUid) {
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
     * Roughly follows the real-life genetic rules.
     *
     * @param firstChromosome  The {@link Chromosome} of the first parent
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

    /**
     * Tries to load a {@link Genome} for a given {@link ItemStack}.
     * WARNING: This method does not check the class of the {@link SlimefunItem}
     * associated with this {@link ItemStack}, or even if there is any.
     *
     * @param item The {@link ItemStack} to load the {@link Genome} for
     * @return The {@link Genome} stored in a given {@link ItemStack} if there is any, null otherwise
     */
    @Nullable
    private Genome getGenomeUnsafe(ItemStack item) {
        Optional<String> genomeStr = beeTypeService.getItemData(item);
        return genomeStr.map(genomeParser::parse).orElse(null);
    }

    /**
     * Creates an unknown bee {@link ItemStack} with stored genes using the given {@link Genome}.
     *
     * @param genome   The {@link Genome} that determines the bee {@link ItemStack}
     * @param princess Whether the {@link ItemStack} should be a princess or a drone
     * @return An {@link ItemStack} with stored genes and representing an "unknown species"
     */
    @Nonnull
    private ItemStack createChildItemStack(Genome genome, boolean princess) {
        ItemStack item = princess
                ? genome.getSpecies().getPrincessItemStack()
                : genome.getSpecies().getDroneItemStack();

        ItemStack copy = beeLoreService.makeUnknown(item);
        updateItemGenome(copy, genome);

        return copy;
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
