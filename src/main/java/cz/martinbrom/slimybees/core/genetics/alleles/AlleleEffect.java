package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.Objects;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;

@ParametersAreNonnullByDefault
public class AlleleEffect extends Allele {

    private final BiConsumer<Location, Integer> effectFunction;

    public AlleleEffect(String uid, String name, BiConsumer<Location, Integer> effectFunction, boolean dominant) {
        super(uid, name, dominant);

        Validate.notNull(effectFunction, "The effect function cannot be null!");

        this.effectFunction = effectFunction;
    }

    @Nonnull
    public BiConsumer<Location, Integer> getFunction() {
        return effectFunction;
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

        AlleleEffect that = (AlleleEffect) o;
        return Objects.equals(effectFunction, that.effectFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), effectFunction);
    }

}
