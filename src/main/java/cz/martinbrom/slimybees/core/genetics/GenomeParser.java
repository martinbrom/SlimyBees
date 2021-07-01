package cz.martinbrom.slimybees.core.genetics;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

import static cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType.CHROMOSOME_COUNT;

@ParametersAreNonnullByDefault
public class GenomeParser {

    public static final String DELIMITER = "|";

    private final ChromosomeParser chromosomeParser;

    public GenomeParser(ChromosomeParser chromosomeParser) {
        this.chromosomeParser = chromosomeParser;
    }

    @Nonnull
    public Genome parse(String genomeStr) {
        Validate.notNull(genomeStr, "Serialized genome cannot be null");

        String[] parts = genomeStr.split("\\" + DELIMITER);

        // we load species separately, if other chromosomes are missing, species is used to load default values
        Chromosome speciesChromosome = chromosomeParser.parseSpecies(parts[0]);
        String firstSpecies = speciesChromosome.getPrimaryAllele().getUid();
        String secondSpecies = speciesChromosome.getSecondaryAllele().getUid();

        Chromosome[] chromosomes = new Chromosome[CHROMOSOME_COUNT];
        chromosomes[0] = speciesChromosome;
        for (int i = 1; i < CHROMOSOME_COUNT; i++) {
            // if the string contains less chromosomes than there are in the current version,
            // we will simply load the species default chromosomes for those missing in the string
            String chromosomeStr = parts.length > i ? parts[i] : null;

            ChromosomeType type = ChromosomeType.values()[i];
            chromosomes[i] = chromosomeParser.parse(chromosomeStr, type, firstSpecies, secondSpecies);
        }

        return new Genome(chromosomes);
    }

    @Nonnull
    public String serialize(Genome genome) {
        return Arrays.stream(genome.getChromosomes())
                .map(chromosomeParser::serialize)
                .collect(Collectors.joining(DELIMITER));
    }

}
