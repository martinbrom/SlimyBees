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

import cz.martinbrom.slimybees.core.BeeLifespanService;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeMutationDTO;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.items.bees.AbstractBee;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

import static cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType.CHROMOSOME_COUNT;

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
    private final AlleleRegistry alleleRegistry;
    private final BeeLifespanService lifespanService;

    public BeeGeneticService(CustomItemDataService beeTypeService, BeeLoreService beeLoreService, BeeRegistry beeRegistry,
                             GenomeParser genomeParser, AlleleRegistry alleleRegistry, BeeLifespanService lifespanService) {
        this.beeTypeService = beeTypeService;
        this.beeLoreService = beeLoreService;
        this.beeRegistry = beeRegistry;
        this.genomeParser = genomeParser;
        this.alleleRegistry = alleleRegistry;
        this.lifespanService = lifespanService;
    }

    /**
     * Performs a breeding process for two parents represented by given {@link Genome}s.
     * Returns a {@link BreedingResultDTO} containing all needed breeding results.
     *
     * @param princessGenome The princess' {@link Genome}
     * @param droneGenome The drone's {@link Genome}
     * @param modifier Modifiers applied to the breeding process by the housing and/or frames
     * @return The {@link BreedingResultDTO} containing data about the breeding process or null
     */
    @Nonnull
    public BreedingResultDTO breed(Genome princessGenome, Genome droneGenome, BreedingModifierDTO modifier) {
        Validate.notNull(princessGenome, "The princess genome must not be null!");
        Validate.notNull(droneGenome, "The drone genome must not be null!");

        // create drones
        Genome[] genomes = getChildrenGenomes(princessGenome, droneGenome);
        ItemStack[] drones = new ItemStack[genomes.length];
        for (int i = 0; i < genomes.length; i++) {
            drones[i] = createChildItemStack(genomes[i], false);
        }

        // create princess
        Genome childPrincessGenome = combineGenomes(princessGenome, droneGenome);
        ItemStack princess = createChildItemStack(childPrincessGenome, true);

        return new BreedingResultDTO(princess, drones, lifespanService.getLifespan(princessGenome, modifier));
    }

    /**
     * Performs a breeding process for two parents represented by given {@link Genome}s.
     * Returns a {@link Genome} for each child.
     *
     * @param firstGenome  The first parent's {@link Genome}
     * @param secondGenome The second parent's {@link Genome}
     * @return {@link Genome} for each child created by the breeding process
     */
    private Genome[] getChildrenGenomes(Genome firstGenome, Genome secondGenome) {
        Validate.notNull(firstGenome, "The first genome cannot null!");
        Validate.notNull(secondGenome, "The second genome cannot null!");

        int fertilityValue = chooseRandom(firstGenome, secondGenome).getFertilityValue();
        int childrenCount = 1 + ThreadLocalRandom.current().nextInt(fertilityValue);

        Genome[] output = new Genome[childrenCount];
        for (int i = 0; i < childrenCount; i++) {
            output[i] = combineGenomes(firstGenome, secondGenome);
        }

        return output;
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

        // take one half of each chromosome from each parent and merge the halves
        // together to form the child's chromosomes
        Chromosome[] combinedChromosomes = new Chromosome[CHROMOSOME_COUNT];
        for (int i = 0; i < CHROMOSOME_COUNT; i++) {
            combinedChromosomes[i] = combineChromosomes(firstChromosomes[i], secondChromosomes[i]);
        }

        tryMutate(combinedChromosomes);
        return new Genome(combinedChromosomes);
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
        // choose a random allele from each chromosome
        Allele firstAllele = chooseRandom(firstChromosome.getPrimaryAllele(), firstChromosome.getSecondaryAllele());
        Allele secondAllele = chooseRandom(secondChromosome.getPrimaryAllele(), secondChromosome.getSecondaryAllele());

        // and create a new chromosome with a chance to swap the alleles
        return chooseRandom(new Chromosome(firstAllele, secondAllele), new Chromosome(secondAllele, firstAllele));
    }

    /**
     * Tries to find and apply a mutation to a given array of {@link Chromosome}s.
     * The available mutations are determined by the given parents unique ids.
     *
     * @param chromosomes Child {@link Chromosome}s to apply the mutation to
     */
    private void tryMutate(Chromosome[] chromosomes) {
        AlleleSpecies firstSpecies = (AlleleSpecies) chromosomes[ChromosomeType.SPECIES.ordinal()].getPrimaryAllele();
        AlleleSpecies secondSpecies = (AlleleSpecies) chromosomes[ChromosomeType.SPECIES.ordinal()].getSecondaryAllele();

        List<BeeMutationDTO> mutations = beeRegistry.getMutationForParents(firstSpecies.getUid(), secondSpecies.getUid());

        Collections.shuffle(mutations);
        for (BeeMutationDTO mutation : mutations) {
            if (mutation != null && ThreadLocalRandom.current().nextDouble() < mutation.getChance()) {
                Allele[] partialTemplate = beeRegistry.getPartialTemplate(mutation.getChild());
                updateMutatedChromosomes(chromosomes, partialTemplate);

                return;
            }
        }
    }

    private void updateMutatedChromosomes(Chromosome[] chromosomes, @Nullable Allele[] partialTemplate) {
        // nothing to apply, exit early
        if (partialTemplate == null) {
            return;
        }

        for (int i = 0; i < CHROMOSOME_COUNT; i++) {
            // null means keeping the old chromosomes intact
            if (partialTemplate[i] != null) {
                Allele firstAllele = chromosomes[i].getPrimaryAllele();
                Allele secondAllele = chromosomes[i].getSecondaryAllele();

                if (ThreadLocalRandom.current().nextBoolean()) {
                    firstAllele = partialTemplate[i];
                } else {
                    secondAllele = partialTemplate[i];
                }

                // create a new chromosome with a chance to swap the alleles
                chromosomes[i] = chooseRandom(new Chromosome(firstAllele, secondAllele), new Chromosome(secondAllele, firstAllele));
            }
        }
    }

    /**
     * Tries to load a {@link Genome} for a given {@link ItemStack}.
     * Checks whether the {@link ItemStack} is an {@link AbstractBee}.
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

        return getGenomeUnsafe(item);
    }

    /**
     * Tries to load a {@link Genome} for a given {@link AlleleSpecies}.
     *
     * @param species The {@link AlleleSpecies} to load the {@link Genome} for
     * @return The {@link Genome} created from the {@link AlleleSpecies}'s full template
     */
    @Nonnull
    public Genome getGenome(AlleleSpecies species) {
        Validate.notNull(species, "Cannot get a genome for a null species!");

        Allele[] template = beeRegistry.getFullTemplate(species.getUid());
        return new Genome(getChromosomesFromAlleles(template));
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
    public Genome getGenomeUnsafe(@Nullable ItemStack item) {
        Optional<String> genomeStr = beeTypeService.getItemData(item);
        return genomeStr.map(genomeParser::parse).orElse(null);
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
     * Updates the stored {@link Genome} in a given {@link ItemStack} by changing the primary/secondary/both
     * alleles of given {@link ChromosomeType} and allele uid.
     * If the {@link ItemStack} is "analyzed", updates the lore accordingly as well.
     *
     * @param item The {@link ItemStack} representing a bee to alter
     * @param type The {@link ChromosomeType} to alter
     * @param alleleUid The uid of {@link Allele} to set the chromosome to
     * @param primary If the primary {@link Allele} should be altered
     * @param secondary If the secondary {@link Allele} should be altered
     * @return The updated {@link ItemStack} or null if the {@link ItemStack} could not be altered
     */
    @Nullable
    public ItemStack alterItemGenome(ItemStack item, ChromosomeType type, String alleleUid, boolean primary, boolean secondary) {
        Validate.notNull(item, "Cannot change a chromosome value for null item!");

        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem instanceof AbstractBee) {
            Genome oldGenome = getGenomeUnsafe(item);

            if (oldGenome != null) {
                Genome newGenome = alterGenome(oldGenome, type, alleleUid, primary, secondary);

                if (newGenome != null) {
                    updateItemGenome(item, newGenome);

                    if (!beeLoreService.isUnknown(item)) {
                        return beeLoreService.updateLore(item, oldGenome);
                    }

                    return item;
                }
            }
        }

        return null;
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

    /**
     * Updates given {@link Genome} by changing the primary/secondary/both
     * alleles of given {@link ChromosomeType} and allele uid.
     * CANNOT BE USED TO CHANGE THE SPECIES CHROMOSOME!
     *
     * @param genome The {@link Genome} to alter
     * @param type The {@link ChromosomeType} to alter
     * @param alleleUid The uid of {@link Allele} to set the chromosome to
     * @param primary If the primary {@link Allele} should be altered
     * @param secondary If the secondary {@link Allele} should be altered
     * @return The updated {@link Genome} or null if the {@link Allele}s could not be found
     */
    @Nullable
    private Genome alterGenome(Genome genome, ChromosomeType type, String alleleUid, boolean primary, boolean secondary) {
        Validate.notNull(genome, "Cannot change a chromosome value for null Genome!");
        Validate.notNull(type, "Cannot change a chromosome value for null ChromosomeType!");
        Validate.isTrue(type != ChromosomeType.SPECIES, "Cannot alter the species of a bee directly!");
        Validate.notNull(alleleUid, "Cannot change a chromosome value for null allele uid!");
        Validate.isTrue(primary || secondary, "At least one allele has to be altered!");

        Chromosome[] chromosomes = genome.getChromosomes();
        Chromosome chromosome = chromosomes[type.ordinal()];

        Allele newAllele = alleleRegistry.get(type, alleleUid);
        Allele primaryAllele = primary ? newAllele : chromosome.getPrimaryAllele();
        Allele secondaryAllele = secondary ? newAllele : chromosome.getSecondaryAllele();

        if (primaryAllele != null && secondaryAllele != null) {
            chromosomes[type.ordinal()] = new Chromosome(primaryAllele, secondaryAllele);
            return new Genome(chromosomes);
        }

        return null;
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

    // TODO: 11.06.21 Move somewhere
    private <T> T chooseRandom(T first, T second) {
        return ThreadLocalRandom.current().nextBoolean()
                ? first
                : second;
    }

}
