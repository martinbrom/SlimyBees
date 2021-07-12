package cz.martinbrom.slimybees.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;

import me.mrCookieSlime.Slimefun.cscorelib2.blocks.BlockPosition;

@ParametersAreNonnullByDefault
public class BlockSearchService {

    private final Map<BlockPosition, BlockPosition> plantPositions = new ConcurrentHashMap<>();

    /**
     * Checks whether an area specified by given center {@link Block} and range on each side
     * contains a {@link Block} which has the same type as the given {@link Material}.
     *
     * @param center The center {@link Block} of the searched area
     * @param range Distance in blocks to check on each side of the center {@link Block}
     * @param material The searched {@link Material}
     * @return True if the area contains given {@link Material}, false otherwise
     */
    public boolean containsBlock(Block center, int range, Material material) {
        Validate.notNull(center, "Cannot search for a block because the center block is null!");
        Validate.notNull(material, "Cannot search for a block because the searched material is null!");

        BlockPosition centerPos = new BlockPosition(center);
        BlockPosition cachedPos = plantPositions.get(centerPos);

        // if we already have a cached position, we simply check the block again
        // to make sure it hasn't changed since the last time we checked
        if (cachedPos != null && cachedPos.getBlock().getType() == material) {
            return true;
        }

        // otherwise look at every block in range
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                Block b = center.getRelative(x, 0, z);
                if (b.getType() == material) {
                    plantPositions.put(centerPos, new BlockPosition(b));
                    return true;
                }
            }
        }

        return false;
    }

}
