package cz.martinbrom.slimybees.core.genetics;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public enum ChromosomeType {

    SPECIES(s -> s, null),
    FERTILITY(Integer::parseInt, AlleleFertilityValue.AVERAGE),
    RANGE(Integer::parseInt, AlleleRangeValue.AVERAGE),
    SPEED(Integer::parseInt, AlleleSpeedValue.AVERAGE);

    public static final int CHROMOSOME_COUNT = values().length;

    private final Function<String, Object> parser;
    private final Object defaultValue;

    ChromosomeType(Function<String, Object> parser, @Nullable Object defaultValue) {
        this.parser = parser;
        this.defaultValue = defaultValue;
    }

    @Nonnull
    public Function<String, Object> getParser() {
        return parser;
    }

    @Nonnull
    public Object getDefaultValue() {
        if (this == SPECIES) {
            throw new IllegalArgumentException("Species chromosome has no default value!");
        }

        return defaultValue;
    }

}
