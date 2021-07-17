package cz.martinbrom.slimybees.worldgen;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.SlimyBeesRegistry;
import cz.martinbrom.slimybees.utils.ArrayUtils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

@ParametersAreNonnullByDefault
public class NestPopulator extends BlockPopulator {

    public static final int TRIES_PER_CHUNK = 10;

    private final SlimyBeesRegistry registry;
    private final double chanceModifier;

    public NestPopulator(SlimyBeesRegistry registry, double chanceModifier) {
        this.registry = registry;
        this.chanceModifier = chanceModifier;
    }

    /**
     * Tries to generate a nest for given {@link World} in a given {@link Chunk}.
     *
     * @param world The {@link World} to generate a nest in
     * @param random The random generator to use
     * @param source The chunk to generate a nest in
     */
    @Override
    public void populate(World world, Random random, Chunk source) {
        Block cornerBlock = world.getHighestBlockAt(source.getX() * 16, source.getZ() * 16);
        Biome chunkBiome = cornerBlock.getBiome();

        List<NestDTO> nests = registry.getNestsForBiome(world, chunkBiome);
        Collections.shuffle(nests, random);

        for (NestDTO nest : nests) {
            double spawnChance = nest.getSpawnChance() * chanceModifier;
            if (random.nextDouble() < spawnChance && tryGenerate(world, random, source, nest)) {
                return;
            }
        }
    }

    /**
     * Tries to generate a nest for given {@link World} in a given {@link Chunk}
     * based on data from a given {@link NestDTO}.
     * Each try, random X and Z coordinates are chosen and the highest block
     * at that point is checked. If the highest block is solid and the needed type
     * for the give {@link NestDTO} and the block above is empty, a nest
     * is generated.
     * Because the biome is only checked for the corner of the {@link Chunk},
     * it is possible for the nest to generate in a biome it wouldn't normally generate in.
     *
     * @param world The {@link World} to generate a nest in
     * @param random The random generator to use
     * @param source The chunk to generate a nest in
     * @param nest The {@link NestDTO} containing information about the nest
     * @return True if a nest was generated, false otherwise
     */
    private boolean tryGenerate(World world, Random random, Chunk source, NestDTO nest) {
        int cornerX = source.getX() * 16;
        int cornerZ = source.getZ() * 16;
        for (int i = 0; i < TRIES_PER_CHUNK; i++) {
            int x = cornerX + random.nextInt(16);
            int z = cornerZ + random.nextInt(16);

            Block groundBlock = world.getHighestBlockAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
            Block nestBlock = groundBlock.getRelative(BlockFace.UP);

            if (ArrayUtils.contains(nest.getFloorMaterials(), groundBlock.getType()) && nestBlock.getType().isAir()) {
                createNest(nestBlock, nest);

                // TODO: 16.05.21 Change back to fine or similar logging level
                SlimyBeesPlugin.logger().info("Successfully generated a Ground Nest "
                        + "of type: " + nest.getNestId()
                        + " at [x=" + x
                        + ", y=" + nestBlock.getY()
                        + ", z=" + z);

                return true;
            }
        }

        return false;
    }

    /**
     * Creates a nest in place of the given {@link Block}
     * from information in the given {@link NestDTO}.
     *
     * @param b The {@link Block} where the nest should be generated
     * @param nest The {@link NestDTO} containing information about the nest
     */
    public static void createNest(Block b, NestDTO nest) {
        b.setType(Material.BEEHIVE);
        BlockStorage.store(b, nest.getNestId());
    }

}
