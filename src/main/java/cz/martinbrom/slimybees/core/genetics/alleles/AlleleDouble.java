package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AlleleDouble extends Allele {

    private final double value;

    public AlleleDouble(String uid, String name, double value, boolean dominant) {
        super(uid, name, dominant);

        this.value = value;
    }

    public double getValue() {
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

        AlleleDouble that = (AlleleDouble) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

}
