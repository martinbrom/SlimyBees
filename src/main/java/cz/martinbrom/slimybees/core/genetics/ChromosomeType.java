package cz.martinbrom.slimybees.core.genetics;

import java.util.function.Function;

public enum ChromosomeType {

    SPECIES(s -> s),
    FERTILITY(Integer::parseInt),
    RANGE(Integer::parseInt),
    SPEED(Integer::parseInt);

    public static final int CHROMOSOME_COUNT = values().length;

    private final Function<String, Object> parser;

    ChromosomeType(Function<String, Object> parser) {
        this.parser = parser;
    }

    public Function<String, Object> getParser() {
        return parser;
    }

}
