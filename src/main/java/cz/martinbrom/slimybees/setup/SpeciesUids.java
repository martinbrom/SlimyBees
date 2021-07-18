package cz.martinbrom.slimybees.setup;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

import static cz.martinbrom.slimybees.utils.StringUtils.nameToUid;

/**
 * This class holds a uid for every base species in SlimyBees.
 */
public class SpeciesUids {

    // prevent instantiation
    private SpeciesUids() {}

    public static final String FOREST = nameToUid(ChromosomeType.SPECIES, "forest");
    public static final String MEADOWS = nameToUid(ChromosomeType.SPECIES, "meadows");
    public static final String STONE = nameToUid(ChromosomeType.SPECIES, "stone");
    public static final String SANDY = nameToUid(ChromosomeType.SPECIES, "sandy");
    public static final String WATER = nameToUid(ChromosomeType.SPECIES, "water");
    public static final String NETHER = nameToUid(ChromosomeType.SPECIES, "nether");
    public static final String ENDER = nameToUid(ChromosomeType.SPECIES, "ender");

    public static final String COMMON = nameToUid(ChromosomeType.SPECIES, "common");
    public static final String CULTIVATED = nameToUid(ChromosomeType.SPECIES, "cultivated");
    public static final String NOBLE = nameToUid(ChromosomeType.SPECIES, "noble");
    public static final String MAJESTIC = nameToUid(ChromosomeType.SPECIES, "majestic");
    public static final String IMPERIAL = nameToUid(ChromosomeType.SPECIES, "imperial");
    public static final String DILIGENT = nameToUid(ChromosomeType.SPECIES, "diligent");
    public static final String UNWEARY = nameToUid(ChromosomeType.SPECIES, "unweary");
    public static final String INDUSTRIOUS = nameToUid(ChromosomeType.SPECIES, "industrious");

    public static final String FARMER = nameToUid(ChromosomeType.SPECIES, "farmer");
    public static final String WHEAT = nameToUid(ChromosomeType.SPECIES, "wheat");
    public static final String SUGAR_CANE = nameToUid(ChromosomeType.SPECIES, "sugar_cane");
    public static final String MELON = nameToUid(ChromosomeType.SPECIES, "melon");
    public static final String PUMPKIN = nameToUid(ChromosomeType.SPECIES, "pumpkin");
    public static final String POTATO = nameToUid(ChromosomeType.SPECIES, "potato");
    public static final String CARROT = nameToUid(ChromosomeType.SPECIES, "carrot");
    public static final String BEETROOT = nameToUid(ChromosomeType.SPECIES, "beetroot");
    public static final String COCOA = nameToUid(ChromosomeType.SPECIES, "cocoa");
    public static final String BERRY = nameToUid(ChromosomeType.SPECIES, "berry");
    public static final String GLOW_BERRY = nameToUid(ChromosomeType.SPECIES, "glow_berry");

    public static final String SECRET = nameToUid(ChromosomeType.SPECIES, "secret");

}
