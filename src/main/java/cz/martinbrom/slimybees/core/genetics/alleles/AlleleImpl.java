package cz.martinbrom.slimybees.core.genetics.alleles;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AlleleImpl implements Allele {

    private final String uid;
    private final String name;
    private final boolean dominant;

    public AlleleImpl(String uid, String name, boolean dominant) {
        // TODO: 06.06.21 Validate uid is all lowercase (except the dot(s))
        this.uid = uid;
        this.name = name;
        this.dominant = dominant;
    }

    @Nonnull
    @Override
    public String getUid() {
        return uid;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDominant() {
        return dominant;
    }

}
