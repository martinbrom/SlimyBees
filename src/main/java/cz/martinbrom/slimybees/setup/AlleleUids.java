package cz.martinbrom.slimybees.setup;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

/**
 * This class holds a uid for every base allele registered in SlimyBees.
 */
public class AlleleUids {

    // prevent instantiation
    private AlleleUids() {
    }

    public static final String PRODUCTIVITY_VERY_LOW = "productivity.very_low";
    public static final String PRODUCTIVITY_LOW = "productivity.low";
    public static final String PRODUCTIVITY_AVERAGE = "productivity.average";
    public static final String PRODUCTIVITY_GOOD = "productivity.good";
    public static final String PRODUCTIVITY_VERY_GOOD = "productivity.very_good";

    public static final String FERTILITY_LOW = "fertility.low";
    public static final String FERTILITY_NORMAL = "fertility.normal";
    public static final String FERTILITY_HIGH = "fertility.high";
    public static final String FERTILITY_VERY_HIGH = "fertility.very_high";

    public static final String LIFESPAN_VERY_SHORT = "lifespan.very_short";
    public static final String LIFESPAN_SHORT = "lifespan.short";
    public static final String LIFESPAN_NORMAL = "lifespan.normal";
    public static final String LIFESPAN_LONG = "lifespan.long";
    public static final String LIFESPAN_VERY_LONG = "lifespan.very_long";

}
