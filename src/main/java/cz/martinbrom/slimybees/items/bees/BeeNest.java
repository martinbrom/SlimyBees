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
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

// TODO: 16.05.21 Javadoc
@ParametersAreNonnullByDefault
public class BeeNest extends SlimefunItem {

    private final List<RandomizedItemStack> randomDrops = new ArrayList<>();
    private final List<Pair<ItemStack, Integer>> extraDrops = new ArrayList<>();

    // TODO: 16.05.21 Javadoc
    public BeeNest(SlimefunItemStack beeNestStack, ItemStack princessStack, ItemStack droneStack) {
        super(Categories.ITEMS, beeNestStack, RecipeTypes.WILDERNESS, new ItemStack[9]);

        BeeLoreService loreService = SlimyBeesPlugin.getBeeLoreService();
        addExtraDrop(loreService.makeUnknown(princessStack), 1);
        addRandomDrop(new RandomizedItemStack(loreService.makeUnknown(droneStack), 1, 2));
        addItemHandler(onBlockBreak());
    }

    public void addExtraDrop(ItemStack drop, int count) {
        Validate.notNull(drop, "BeeNest drop cannot be null!");
        Validate.isTrue(count > 0, "The count must be greater than zero");
        Validate.isTrue(drop.getType() != Material.AIR, "BeeNest drop cannot be of type AIR!");

        extraDrops.add(new Pair<>(drop, count));
    }

    public void addRandomDrop(RandomizedItemStack drop) {
        Validate.notNull(drop, "BeeNest drop cannot be null!");
        Validate.isTrue(drop.getItemStack().getType() != Material.AIR, "BeeNest drop cannot be of type AIR!");
        if (getState() != ItemState.UNREGISTERED) {
            throw new UnsupportedOperationException("You cannot add extra drops after the BeeNest was registered.");
        }

        randomDrops.add(drop);
    }

    // TODO: 16.05.21 Javadoc
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
