package cz.martinbrom.slimybees.worldgen;

import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

// TODO: 16.05.21 Javadoc
public class TreeNestPopulator extends AbstractNestPopulator {

    @ParametersAreNonnullByDefault
    public TreeNestPopulator(Biome[] validBiomes, double spawnChance) {
        super(validBiomes, spawnChance);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void generate(World world, Random random, Chunk source) {
        // TODO: 16.05.21 Implement
    }


}
