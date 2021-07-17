package cz.martinbrom.slimybees;

import org.bukkit.block.Biome;

/**
 * This class contains arrays of {@link Biome}s to make creating nests in the BeeSetup easier.
 */
public class BiomeSets {

    // prevent instantiation
    private BiomeSets() {}

    public static final Biome[] MILD_FORESTS = {
            Biome.FOREST,
            Biome.BIRCH_FOREST,
            Biome.BIRCH_FOREST_HILLS,
    };

    public static final Biome[] PLAINS = {
            Biome.PLAINS,
            Biome.SUNFLOWER_PLAINS,
    };

    public static final Biome[] COLD_FORESTS = {
            Biome.TAIGA,
            Biome.TAIGA_HILLS,
            Biome.GIANT_SPRUCE_TAIGA,
            Biome.GIANT_SPRUCE_TAIGA_HILLS,
            Biome.SNOWY_TAIGA,
            Biome.SNOWY_TAIGA_HILLS,
    };

    public static final Biome[] DESERTS = {
            Biome.DESERT,
            Biome.DESERT_LAKES,
            Biome.DESERT_HILLS,
            Biome.BADLANDS,
            Biome.BADLANDS_PLATEAU,
            Biome.MODIFIED_BADLANDS_PLATEAU
    };

    public static final Biome[] MOUNTAINS = {
            Biome.MOUNTAINS,
            Biome.MOUNTAIN_EDGE,
            Biome.GRAVELLY_MOUNTAINS
    };

    public static final Biome[] BODIES_OF_WATER = {
            Biome.RIVER,
            Biome.OCEAN,
            Biome.WARM_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.DEEP_OCEAN,
            Biome.DEEP_WARM_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
    };

    public static final Biome[] COLORFUL_NETHER = {
            Biome.NETHER_WASTES,
            Biome.CRIMSON_FOREST,
            Biome.WARPED_FOREST,
    };

    public static final Biome[] OUTER_END = {
            Biome.END_BARRENS,
            Biome.END_HIGHLANDS,
            Biome.END_MIDLANDS,
            Biome.SMALL_END_ISLANDS
    };

}
