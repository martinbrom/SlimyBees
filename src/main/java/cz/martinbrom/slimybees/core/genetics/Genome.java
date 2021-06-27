package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleInteger;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;

@ParametersAreNonnullByDefault
public class Genome {

    private final Chromosome[] chromosomes;

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

    @Nonnull
    public AlleleInteger getSpeed() {
        return (AlleleInteger) getActiveAllele(ChromosomeTypeImpl.SPEED);
    }

    public int getSpeedValue() {
        return getSpeed().getValue();
    }

    @Nonnull
    public Allele getActiveAllele(ChromosomeType type) {
        return chromosomes[type.ordinal()].getActiveAllele();
    }

}
