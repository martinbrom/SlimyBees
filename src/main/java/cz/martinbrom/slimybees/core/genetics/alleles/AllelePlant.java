package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;

@ParametersAreNonnullByDefault
public class AllelePlant extends Allele {

    private final Material value;

    public AllelePlant(String uid, String name, Material value, boolean dominant) {
        super(uid, name, dominant);

        Validate.isTrue(value.isBlock(), "The material of AllelePlant has to be a block, got " + value + "!");
        this.value = value;
    }

    @Nonnull
    public Material getValue() {
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

        AllelePlant that = (AllelePlant) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

}
