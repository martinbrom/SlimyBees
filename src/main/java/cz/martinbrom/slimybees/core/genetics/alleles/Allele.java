package cz.martinbrom.slimybees.core.genetics.alleles;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Allele {

    private final String uid;
    private final String name;
    private final boolean dominant;

    public Allele(String uid, String name, boolean dominant) {
        this.uid = uid;
        this.name = name;
        this.dominant = dominant;
    }

    @Nonnull
    public String getUid() {
        return uid;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public boolean isDominant() {
        return dominant;
    }

}
