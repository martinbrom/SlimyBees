package cz.martinbrom.slimybees.worldgen;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.utils.ArrayUtils;


// TODO: 16.05.21 Javadoc
public abstract class AbstractNestPopulator extends BlockPopulator {

    protected final Biome[] validBiomes;
    protected final double spawnChance;

    // TODO: 16.05.21 Javadoc
    @ParametersAreNonnullByDefault
    public AbstractNestPopulator(Biome[] validBiomes, double spawnChance) {
        Validate.notEmpty(validBiomes, "Valid biomes cannot be null or empty!");
        Validate.isTrue(spawnChance >= 0.01 && spawnChance <= 1, "Spawn chance must be between 1% and 100% inclusive (0.01 and 1)!");

        this.validBiomes = validBiomes;
        this.spawnChance = spawnChance;
    }

    // TODO: 16.05.21 Javadoc
    @Override
    @ParametersAreNonnullByDefault
    public void populate(World world, Random random, Chunk source) {
        Block cornerBlock = source.getBlock(0, 64, 0);
        Biome chunkBiome = cornerBlock.getBiome();
        if (random.nextDouble() < spawnChance && ArrayUtils.contains(validBiomes, chunkBiome)) {
            generate(world, random, source);
        }
    }

    // TODO: 16.05.21 Javadoc
    public void register(@Nonnull SlimyBeesPlugin plugin) {
        Validate.notNull(plugin, "The addon cannot be null");
        Validate.notNull(plugin.getJavaPlugin(), "The plugin cannot be null");

        plugin.getPopulators().add(this);
    }

    // TODO: 16.05.21 Javadoc
    @ParametersAreNonnullByDefault
    protected abstract void generate(World world, Random random, Chunk source);

}
