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
            Biome.OLD_GROWTH_BIRCH_FOREST,
    };

    public static final Biome[] PLAINS = {
            Biome.PLAINS,
            Biome.SUNFLOWER_PLAINS,
    };

    public static final Biome[] COLD_FORESTS = {
            Biome.TAIGA,
            Biome.OLD_GROWTH_PINE_TAIGA,
            Biome.OLD_GROWTH_SPRUCE_TAIGA,
            Biome.SNOWY_TAIGA,
    };

    public static final Biome[] DESERTS = {
            Biome.DESERT,
            Biome.BADLANDS,
            Biome.ERODED_BADLANDS,
            Biome.WOODED_BADLANDS
    };

    public static final Biome[] MOUNTAINS = {
            Biome.SNOWY_SLOPES,
            Biome.FROZEN_PEAKS,
            Biome.JAGGED_PEAKS,
            Biome.STONY_PEAKS,
    };

    public static final Biome[] BODIES_OF_WATER = {
            Biome.RIVER,
            Biome.OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.WARM_OCEAN,
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
