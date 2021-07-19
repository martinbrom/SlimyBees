package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

import static cz.martinbrom.slimybees.utils.StringUtils.nameToUid;

/**
 * This class holds a uid for every base allele registered in SlimyBees.
 */
@ParametersAreNonnullByDefault
public class AlleleUids {

    // prevent instantiation
    private AlleleUids() {}

    public static final String PRODUCTIVITY_VERY_LOW = nameToUid(ChromosomeType.PRODUCTIVITY, "very_low");
    public static final String PRODUCTIVITY_LOW = nameToUid(ChromosomeType.PRODUCTIVITY, "low");
    public static final String PRODUCTIVITY_NORMAL = nameToUid(ChromosomeType.PRODUCTIVITY, "normal");
    public static final String PRODUCTIVITY_HIGH = nameToUid(ChromosomeType.PRODUCTIVITY, "high");
    public static final String PRODUCTIVITY_VERY_HIGH = nameToUid(ChromosomeType.PRODUCTIVITY, "very_high");

    public static final String FERTILITY_LOW = nameToUid(ChromosomeType.FERTILITY, "low");
    public static final String FERTILITY_NORMAL = nameToUid(ChromosomeType.FERTILITY, "normal");
    public static final String FERTILITY_HIGH = nameToUid(ChromosomeType.FERTILITY, "high");
    public static final String FERTILITY_VERY_HIGH = nameToUid(ChromosomeType.FERTILITY, "very_high");

    public static final String LIFESPAN_VERY_SHORT = nameToUid(ChromosomeType.LIFESPAN, "very_short");
    public static final String LIFESPAN_SHORT = nameToUid(ChromosomeType.LIFESPAN, "short");
    public static final String LIFESPAN_NORMAL = nameToUid(ChromosomeType.LIFESPAN, "normal");
    public static final String LIFESPAN_LONG = nameToUid(ChromosomeType.LIFESPAN, "long");
    public static final String LIFESPAN_VERY_LONG = nameToUid(ChromosomeType.LIFESPAN, "very_long");

    public static final String RANGE_VERY_SHORT = nameToUid(ChromosomeType.RANGE, "very_short");
    public static final String RANGE_SHORT = nameToUid(ChromosomeType.RANGE, "short");
    public static final String RANGE_NORMAL = nameToUid(ChromosomeType.RANGE, "normal");
    public static final String RANGE_LONG = nameToUid(ChromosomeType.RANGE, "long");
    public static final String RANGE_VERY_LONG = nameToUid(ChromosomeType.RANGE, "very_long");

    public static final String PLANT_NONE = nameToUid(ChromosomeType.PLANT, "none");
    public static final String PLANT_OXEYE_DAISY = nameToUid(ChromosomeType.PLANT, "oxeye_daisy");
    public static final String PLANT_WHEAT = nameToUid(ChromosomeType.PLANT, "wheat");
    public static final String PLANT_SUGAR_CANE = nameToUid(ChromosomeType.PLANT, "sugar_cane");
    public static final String PLANT_MELON = nameToUid(ChromosomeType.PLANT, "melon");
    public static final String PLANT_PUMPKIN = nameToUid(ChromosomeType.PLANT, "pumpkin");
    public static final String PLANT_POTATO = nameToUid(ChromosomeType.PLANT, "potato");
    public static final String PLANT_CARROT = nameToUid(ChromosomeType.PLANT, "carrot");
    public static final String PLANT_BEETROOT = nameToUid(ChromosomeType.PLANT, "beetroot");
    public static final String PLANT_COCOA = nameToUid(ChromosomeType.PLANT, "cocoa");
    public static final String PLANT_BERRY = nameToUid(ChromosomeType.PLANT, "berry");

    public static final String EFFECT_NONE = nameToUid(ChromosomeType.EFFECT, "none");
    public static final String EFFECT_REGENERATION = nameToUid(ChromosomeType.EFFECT, "regeneration");
    public static final String EFFECT_FIREWORK = nameToUid(ChromosomeType.EFFECT, "firework");

}
