package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Allele<T> {

    private final T value;

    public Allele(T value) {
        this.value = value;
    }

    // TODO: 30.05.21 Add dominance to allele values
    public boolean isDominant() {
        return true;
    }

    @Nonnull
    public T getValue() {
        return value;
    }

}
