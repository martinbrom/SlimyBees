package cz.martinbrom.slimybees.listeners;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.SlimyBeesRegistry;
import cz.martinbrom.slimybees.worldgen.NestDTO;
import cz.martinbrom.slimybees.worldgen.NestPopulator;

@ParametersAreNonnullByDefault
public class TreeGrowListener implements Listener {

    private static final BlockFace[] BLOCK_FACES = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    private final SlimyBeesRegistry registry;
    private final double spawnChance;

    public TreeGrowListener(SlimyBeesPlugin plugin, SlimyBeesRegistry registry, double spawnChance) {
        Validate.isTrue(spawnChance >= 0 && spawnChance <= 1, "The chance to spawn a nest while growing a tree" +
                " must be between 0 and 1 (inclusive)!");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.registry = registry;
        this.spawnChance = spawnChance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent e) {
        // TODO: 15.07.21 Check the TreeType as well
        if (!e.isFromBonemeal() || ThreadLocalRandom.current().nextDouble() > spawnChance) {
            return;
        }

        World world = e.getWorld();
        Block b = world.getBlockAt(e.getLocation());
        Biome biome = b.getBiome();

        List<NestDTO> nests = registry.getNestsForBiome(world, biome);
        Collections.shuffle(nests);
        for (NestDTO nest : nests) {
            if (tryGenerateNest(b, nest)) {
                return;
            }
        }
    }

    private boolean tryGenerateNest(Block b, NestDTO nest) {
        for (BlockFace blockFace : BLOCK_FACES) {
            Block nestBlock = b.getRelative(blockFace);
            Block groundBlock = nestBlock.getRelative(BlockFace.DOWN);
            if (nestBlock.getType().isAir() && groundBlock.getType().isSolid()) {
                NestPopulator.createNest(nestBlock, nest);

                return true;
            }
        }

        return false;
    }

}
