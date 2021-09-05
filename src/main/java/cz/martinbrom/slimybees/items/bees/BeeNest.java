package cz.martinbrom.slimybees.items.bees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.RecipeTypes;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.RandomizedItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

/**
 * This class represents a naturally generating BeeHive block
 * which drops bees and extra drops when destroyed by a player.
 */
@ParametersAreNonnullByDefault
public class BeeNest extends SlimefunItem {

    private final List<RandomizedItemStack> randomDrops = new ArrayList<>();
    private final List<Pair<ItemStack, Integer>> extraDrops = new ArrayList<>();

    public BeeNest(SlimefunItemStack beeNestStack, ItemStack princessStack, ItemStack droneStack) {
        super(Categories.ITEMS, beeNestStack, RecipeTypes.WILDERNESS, new ItemStack[9]);

        BeeLoreService loreService = SlimyBeesPlugin.getBeeLoreService();
        addExtraDrop(loreService.makeUnknown(princessStack), 1);
        addRandomDrop(new RandomizedItemStack(loreService.makeUnknown(droneStack), 1, 2));
        addItemHandler(onBlockBreak());
    }

    /**
     * Adds an {@link ItemStack} drop to the nest with guaranteed count.
     *
     * @param drop The {@link ItemStack} to drop when broken
     * @param count The amount of the dropped {@link ItemStack}
     */
    public void addExtraDrop(ItemStack drop, int count) {
        Validate.notNull(drop, "BeeNest drop cannot be null!");
        Validate.isTrue(count > 0, "The count must be greater than zero");
        Validate.isTrue(!drop.getType().isAir(), "BeeNest drop cannot be of type AIR!");

        extraDrops.add(new Pair<>(drop, count));
    }

    /**
     * Adds a random {@link ItemStack} drop to the nest.
     *
     * @param drop The {@link RandomizedItemStack} to drop when broken
     */
    public void addRandomDrop(RandomizedItemStack drop) {
        Validate.notNull(drop, "BeeNest drop cannot be null!");
        Validate.isTrue(!drop.getItemStack().getType().isAir(), "BeeNest drop cannot be of type AIR!");

        if (getState() != ItemState.UNREGISTERED) {
            throw new UnsupportedOperationException("You cannot add extra drops after the BeeNest was registered.");
        }

        randomDrops.add(drop);
    }

    @Nonnull
    private BlockBreakHandler onBlockBreak() {
        return new BlockBreakHandler(false, false) {

            @Override
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                Location location = e.getBlock().getLocation();
                World world = e.getBlock().getWorld();

                for (RandomizedItemStack itemStack : randomDrops) {
                    ItemStack drop = itemStack.getRandom();
                    if (drop != null) {
                        world.dropItemNaturally(location, drop);
                    }
                }

                for (Pair<ItemStack, Integer> pair : extraDrops) {
                    ItemStack itemStack = pair.getFirstValue().clone();
                    itemStack.setAmount(pair.getSecondValue());

                    world.dropItemNaturally(location, itemStack);
                }
            }
        };
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getDrops() {
        // Disable any drops (Air is not dropped but still counts as "overridden drops")
        return Collections.singletonList(new ItemStack(Material.AIR));
    }

}
