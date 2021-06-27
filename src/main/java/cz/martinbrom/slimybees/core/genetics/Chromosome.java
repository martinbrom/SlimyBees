package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;

@ParametersAreNonnullByDefault
public class Chromosome {

    private final Allele primary;
    private final Allele secondary;

    Chromosome(Allele allele) {
        primary = allele;
        secondary = allele;
    }

    Chromosome(Allele primary, Allele secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Nonnull
    public Allele getPrimaryAllele() {
        return primary;
    }

    @Nonnull
    public Allele getSecondaryAllele() {
        return secondary;
    }

    @Nonnull
    public Allele getActiveAllele() {
        if (!primary.isDominant() && secondary.isDominant()) {
            return secondary;
        }

        return primary;
    }

    @Nonnull
    public Allele getInactiveAllele() {
        if (!primary.isDominant() && secondary.isDominant()) {
            return primary;
        }

        return secondary;
    }

}
