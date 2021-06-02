package cz.martinbrom.slimybees.core.genetics.alleles;

import javax.annotation.Nonnull;

public interface Allele {

    @Nonnull
    String getUid();

    @Nonnull
    String getName();

    boolean isDominant();

}
