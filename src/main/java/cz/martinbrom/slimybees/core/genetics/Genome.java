package cz.martinbrom.slimybees.core.genetics;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleInteger;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;

import static cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl.CHROMOSOME_COUNT;

@ParametersAreNonnullByDefault
public class Genome {

    private final Chromosome[] chromosomes;

    Genome(String genomeStr) {
        Validate.notNull(genomeStr, "Serialized genome cannot be null");
        String[] parts = genomeStr.split("\\|");
        if (parts.length != CHROMOSOME_COUNT) {
            throw new IllegalArgumentException("Found a bee with incorrect number of chromosomes!");
        }

        chromosomes = new Chromosome[CHROMOSOME_COUNT];
        String firstSpecies = null;
        String secondSpecies = null;
        for (int i = 0; i < CHROMOSOME_COUNT; i++) {
            ChromosomeTypeImpl type = ChromosomeTypeImpl.values()[i];
            Chromosome chromosome = Chromosome.parse(firstSpecies, secondSpecies, parts[i], type);
            chromosomes[i] = chromosome;
            if (type == ChromosomeTypeImpl.SPECIES) {
                firstSpecies = chromosome.getPrimaryAllele().getUid();
                secondSpecies = chromosome.getSecondaryAllele().getUid();
            }
        }
    }

    Genome(Chromosome[] chromosomes) {
        this.chromosomes = chromosomes;
    }

    @Nonnull
    public Chromosome[] getChromosomes() {
        return chromosomes;
    }

    @Nonnull
    public AlleleSpecies getSpecies() {
        return (AlleleSpecies) getActiveAllele(ChromosomeTypeImpl.SPECIES);
    }

    @Nonnull
    public AlleleInteger getFertility() {
        return (AlleleInteger) getActiveAllele(ChromosomeTypeImpl.FERTILITY);
    }

    public int getFertilityValue() {
        return getFertility().getValue();
    }

    public AlleleInteger getSpeed() {
        return (AlleleInteger) getActiveAllele(ChromosomeTypeImpl.SPEED);
    }

    public int getSpeedValue() {
        return getSpeed().getValue();
    }

    @Nonnull
    public String serialize() {
        return Arrays.stream(chromosomes)
                .map(Chromosome::serialize)
                .collect(Collectors.joining("|"));
    }

    @Nonnull
    private Allele getActiveAllele(ChromosomeType type) {
        return chromosomes[type.ordinal()].getActiveAllele();
    }

}
