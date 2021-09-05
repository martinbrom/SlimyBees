package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AlleleInteger extends Allele {

    private final int value;

    public AlleleInteger(String uid, String name, int value, boolean dominant) {
        super(uid, name, dominant);

        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        AlleleInteger that = (AlleleInteger) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

}
