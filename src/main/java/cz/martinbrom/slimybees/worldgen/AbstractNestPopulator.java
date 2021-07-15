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
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;


// TODO: 16.05.21 Javadoc
@ParametersAreNonnullByDefault
public abstract class AbstractNestPopulator extends BlockPopulator {

    protected final Biome[] validBiomes;
    protected final double spawnChance;
    protected final String nestId;

    // TODO: 16.05.21 Javadoc
    public AbstractNestPopulator(Biome[] validBiomes, double spawnChance, SlimefunItemStack nestItem) {
        Validate.notEmpty(validBiomes, "Valid biomes cannot be null or empty!");
        Validate.isTrue(spawnChance > 0 && spawnChance <= 1, "Spawn chance must be between 0% (exclusive) and 100% (inclusive)!");
        Validate.notNull(nestItem, "The bee nest item cannot be null!");

        this.validBiomes = validBiomes;
        this.spawnChance = spawnChance;
        nestId = nestItem.getItemId();
    }

    // TODO: 16.05.21 Javadoc
    @Override
    public void populate(World world, Random random, Chunk source) {
        Block cornerBlock = source.getBlock(0, 64, 0);
        Biome chunkBiome = cornerBlock.getBiome();
        if (ArrayUtils.contains(validBiomes, chunkBiome) && random.nextDouble() < spawnChance) {
            generate(world, random, source);
        }
    }

    // TODO: 16.05.21 Javadoc
    public void register(SlimyBeesPlugin plugin) {
        Validate.notNull(plugin, "The addon cannot be null");
        Validate.notNull(plugin.getJavaPlugin(), "The plugin cannot be null");

        SlimyBeesPlugin.getRegistry().getNestPopulators().add(this);
    }

    @Nonnull
    public Biome[] getValidBiomes() {
        return validBiomes;
    }

    @Nonnull
    public String getNestId() {
        return nestId;
    }

    // TODO: 16.05.21 Javadoc
    protected abstract void generate(World world, Random random, Chunk source);

}
