package cz.martinbrom.slimybees.worldgen;

import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.items.bees.BeeNest;
import cz.martinbrom.slimybees.utils.ArrayUtils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

// TODO: 16.05.21 Javadoc
@ParametersAreNonnullByDefault
public class GroundNestPopulator extends AbstractNestPopulator {

    private final Material[] validFloorMaterials;
    private final String beeNestId;

    /**
     * Constructs a new instance from given arguments.
     *
     * @param validBiomes         {@link Biome}s in which the nest is allowed to generate
     * @param validFloorMaterials {@link Material}s on which the nest is allowed to generate
     * @param spawnChance         Chance for the nest to spawn per chunk regardless of the other spawning conditions.
     *                            Value must be between 0.01 and 1 (inclusive).
     * @param beeNestStack        {@link SlimefunItemStack} describing the type of {@link BeeNest} to generate
     */
    public GroundNestPopulator(Biome[] validBiomes, Material[] validFloorMaterials, double spawnChance, SlimefunItemStack beeNestStack) {
        super(validBiomes, spawnChance);

        this.validFloorMaterials = validFloorMaterials;
        beeNestId = beeNestStack.getItemId();
    }

    @Override
    public void generate(World world, Random random, Chunk source) {
        int cornerX = source.getX() * 16;
        int cornerZ = source.getZ() * 16;
        for (int i = 0; i < 10; i++) {
            int x = cornerX + random.nextInt(16);
            int z = cornerZ + random.nextInt(16);

            Block groundBlock = world.getHighestBlockAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
            Block nestBlock = groundBlock.getRelative(BlockFace.UP);

            if (ArrayUtils.contains(validFloorMaterials, groundBlock.getType()) && nestBlock.getType() == Material.AIR) {
                nestBlock.setType(Material.BEEHIVE);

                BlockStorage.store(nestBlock, beeNestId);

                // TODO: 16.05.21 Change back to fine or similar logging level
                SlimyBeesPlugin.logger().info("Successfully generated a Ground Nest "
                        + "of type: " + beeNestId
                        + " at [x=" + x
                        + ", y=" + nestBlock.getY()
                        + ", z=" + z);

                return;
            }
        }
    }

}
