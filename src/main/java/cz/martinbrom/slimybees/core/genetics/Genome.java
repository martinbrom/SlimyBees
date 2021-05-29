package cz.martinbrom.slimybees.core.genetics;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import static cz.martinbrom.slimybees.core.genetics.ChromosomeType.CHROMOSOME_COUNT;

@ParametersAreNonnullByDefault
public class Genome {

    private final Chromosome<Object>[] chromosomes;

    @SuppressWarnings("unchecked")
    Genome(String genomeStr) {
        Validate.notNull(genomeStr, "Serialized genome cannot be null");
        String[] parts = genomeStr.split("\\|");
        if (parts.length != CHROMOSOME_COUNT) {
            throw new IllegalArgumentException("Found a bee with incorrect number of chromosomes!");
        }

        chromosomes = new Chromosome[CHROMOSOME_COUNT];
        for (int i = 0; i < CHROMOSOME_COUNT; i++) {
            chromosomes[i] = Chromosome.parse(parts[i], ChromosomeType.values()[i].getParser());
        }
    }

    public Genome(Chromosome<Object>[] chromosomes) {
        Validate.noNullElements(chromosomes, "Chromosomes cannot be null");
        this.chromosomes = chromosomes;
    }

    public String serialize() {
        return Arrays.stream(chromosomes)
                .map(c -> c.getPrimaryAllele().getValue() + Chromosome.DELIMITER + c.getSecondaryAllele().getValue())
                .collect(Collectors.joining("|"));
    }

    @Nonnull
    public Chromosome<Object>[] getChromosomes() {
        return chromosomes;
    }

    @Nonnull
    public String getSpeciesValue() {
        return (String) getChromosomeValue(ChromosomeType.SPECIES, true);
    }

    public int getFertilityValue() {
        return (Integer) getChromosomeValue(ChromosomeType.FERTILITY, true);
    }

    public int getRangeValue() {
        return (Integer) getChromosomeValue(ChromosomeType.RANGE, true);
    }

    public int getSpeedValue() {
        return (Integer) getChromosomeValue(ChromosomeType.SPEED, true);
    }

    @Nonnull
    public String getSpeciesValueInactive() {
        return (String) getChromosomeValue(ChromosomeType.SPECIES, false);
    }

    public int getFertilityValueInactive() {
        return (Integer) getChromosomeValue(ChromosomeType.FERTILITY, false);
    }

    public int getRangeValueInactive() {
        return (Integer) getChromosomeValue(ChromosomeType.RANGE, false);
    }

    public int getSpeedValueInactive() {
        return (Integer) getChromosomeValue(ChromosomeType.SPEED, false);
    }

    @Nonnull
    private Object getChromosomeValue(ChromosomeType type, boolean active) {
        Chromosome<Object> chromosome = chromosomes[type.ordinal()];

        return active
                ? chromosome.getActiveAllele().getValue()
                : chromosome.getInactiveAllele().getValue();
    }

}
