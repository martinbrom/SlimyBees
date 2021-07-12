package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

import static cz.martinbrom.slimybees.utils.GeneticUtil.nameToUid;

/**
 * This class holds a uid for every base allele registered in SlimyBees.
 */
@ParametersAreNonnullByDefault
public class AlleleUids {

    // prevent instantiation
    private AlleleUids() {}

    public static final String PRODUCTIVITY_VERY_LOW = nameToUid(ChromosomeType.PRODUCTIVITY, "very_low");
    public static final String PRODUCTIVITY_LOW = nameToUid(ChromosomeType.PRODUCTIVITY, "low");
    public static final String PRODUCTIVITY_AVERAGE = nameToUid(ChromosomeType.PRODUCTIVITY, "average");
    public static final String PRODUCTIVITY_GOOD = nameToUid(ChromosomeType.PRODUCTIVITY, "good");
    public static final String PRODUCTIVITY_VERY_GOOD = nameToUid(ChromosomeType.PRODUCTIVITY, "very_good");

    public static final String FERTILITY_LOW = nameToUid(ChromosomeType.FERTILITY, "low");
    public static final String FERTILITY_NORMAL = nameToUid(ChromosomeType.FERTILITY, "normal");
    public static final String FERTILITY_HIGH = nameToUid(ChromosomeType.FERTILITY, "high");
    public static final String FERTILITY_VERY_HIGH = nameToUid(ChromosomeType.FERTILITY, "very_high");

    public static final String LIFESPAN_VERY_SHORT = nameToUid(ChromosomeType.LIFESPAN, "very_short");
    public static final String LIFESPAN_SHORT = nameToUid(ChromosomeType.LIFESPAN, "short");
    public static final String LIFESPAN_NORMAL = nameToUid(ChromosomeType.LIFESPAN, "normal");
    public static final String LIFESPAN_LONG = nameToUid(ChromosomeType.LIFESPAN, "long");
    public static final String LIFESPAN_VERY_LONG = nameToUid(ChromosomeType.LIFESPAN, "very_long");

    public static final String RANGE_TINY = nameToUid(ChromosomeType.RANGE, "tiny");
    public static final String RANGE_SMALL = nameToUid(ChromosomeType.RANGE, "short");
    public static final String RANGE_NORMAL = nameToUid(ChromosomeType.RANGE, "normal");
    public static final String RANGE_LONG = nameToUid(ChromosomeType.RANGE, "long");

    public static final String PLANT_NONE = nameToUid(ChromosomeType.PLANT, "none");
    public static final String PLANT_SUNFLOWER = nameToUid(ChromosomeType.PLANT, "sunflower");

}
