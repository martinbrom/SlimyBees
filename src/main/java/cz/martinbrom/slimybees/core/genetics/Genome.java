package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleDouble;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleInteger;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

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
        return (AlleleSpecies) getActiveAllele(ChromosomeType.SPECIES);
    }

    @Nonnull
    public AlleleInteger getFertility() {
        return (AlleleInteger) getActiveAllele(ChromosomeType.FERTILITY);
    }

    public int getFertilityValue() {
        return getFertility().getValue();
    }

    @Nonnull
    public AlleleDouble getProductivity() {
        return (AlleleDouble) getActiveAllele(ChromosomeType.PRODUCTIVITY);
    }

    public double getProductivityValue() {
        return getProductivity().getValue();
    }

    @Nonnull
    public Allele getActiveAllele(ChromosomeType type) {
        return chromosomes[type.ordinal()].getActiveAllele();
    }

}
