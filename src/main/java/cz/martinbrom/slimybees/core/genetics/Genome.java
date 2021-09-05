package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleDouble;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleEffect;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleInteger;
import cz.martinbrom.slimybees.core.genetics.alleles.AllelePlant;
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
    public AlleleDouble getProductivity() {
        return (AlleleDouble) getActiveAllele(ChromosomeType.PRODUCTIVITY);
    }

    public double getProductivityValue() {
        return getProductivity().getValue();
    }

    @Nonnull
    public AlleleInteger getFertility() {
        return (AlleleInteger) getActiveAllele(ChromosomeType.FERTILITY);
    }

    public int getFertilityValue() {
        return getFertility().getValue();
    }

    @Nonnull
    public AlleleInteger getLifespan() {
        return (AlleleInteger) getActiveAllele(ChromosomeType.LIFESPAN);
    }

    public int getLifespanValue() {
        return getLifespan().getValue();
    }

    @Nonnull
    public AlleleInteger getRange() {
        return (AlleleInteger) getActiveAllele(ChromosomeType.RANGE);
    }

    public int getRangeValue() {
        return getRange().getValue();
    }

    @Nonnull
    public AllelePlant getPlant() {
        return (AllelePlant) getActiveAllele(ChromosomeType.PLANT);
    }

    @Nonnull
    public Material getPlantValue() {
        return getPlant().getValue();
    }

    @Nonnull
    public AlleleEffect getEffect() {
        return (AlleleEffect) getActiveAllele(ChromosomeType.EFFECT);
    }

    @Nonnull
    public AlleleEffect.EffectFunction getEffectValue() {
        return getEffect().getFunction();
    }

    @Nonnull
    public Allele getActiveAllele(ChromosomeType type) {
        return getChromosomes()[type.ordinal()].getActiveAllele();
    }

}
