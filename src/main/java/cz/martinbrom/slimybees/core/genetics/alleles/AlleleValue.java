package cz.martinbrom.slimybees.core.genetics.alleles;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AlleleValue<T> {

    private final T value;
    private final boolean dominant;

    public AlleleValue(T value) {
        this(value, false);
    }

    public AlleleValue(T value, boolean dominant) {
        this.value = value;
        this.dominant = dominant;
    }

    @Nonnull
    public T getValue() {
        return value;
    }

    public boolean isDominant() {
        return dominant;
    }

}
