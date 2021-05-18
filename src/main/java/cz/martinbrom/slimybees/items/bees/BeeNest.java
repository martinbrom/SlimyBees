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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.utils.RandomizedSlimefunItemStack;
import cz.martinbrom.slimybees.utils.RecipeTypes;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

// TODO: 16.05.21 Javadoc
public class BeeNest extends SlimefunItem {

    private final List<SlimefunItemStack> extraDrops = new ArrayList<>();
    private final List<RandomizedSlimefunItemStack> randomDrops = new ArrayList<>();

    // TODO: 16.05.21 Javadoc
    @ParametersAreNonnullByDefault
    public BeeNest(SlimefunItemStack beeNestStack, SlimefunItemStack beeStack) {
        super(Categories.GENERAL, beeNestStack, RecipeTypes.WILDERNESS, new ItemStack[9]);

        randomDrops.add(new RandomizedSlimefunItemStack(beeStack, 1, 2));
        addItemHandler(onBlockBreak());
    }

    // builder style for fluent register calls during setup
    public BeeNest addExtraDrop(SlimefunItemStack drop) {
        Validate.isTrue(drop != null && drop.getType() != Material.AIR, "You cannot add null or AIR drops to a BeeNest!");
        if (getState() != ItemState.UNREGISTERED) {
            throw new UnsupportedOperationException("You cannot add extra drops after the BeeNest was registered.");
        }

        extraDrops.add(drop);

        return this;
    }

    public BeeNest addRandomDrop(RandomizedSlimefunItemStack drop) {
        Validate.isTrue(drop != null && drop.getItemStack().getType() != Material.AIR, "You cannot add null or AIR drops to a BeeNest!");
        if (getState() != ItemState.UNREGISTERED) {
            throw new UnsupportedOperationException("You cannot add extra drops after the BeeNest was registered.");
        }

        randomDrops.add(drop);

        return this;
    }

    // TODO: 16.05.21 Javadoc
    @Nonnull
    private BlockBreakHandler onBlockBreak() {
        return new BlockBreakHandler(false, false) {

            @Override
            @ParametersAreNonnullByDefault
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                Location location = e.getBlock().getLocation();

                for (SlimefunItemStack itemStack : extraDrops) {
                    e.getBlock().getWorld().dropItemNaturally(location, itemStack);
                }

                for (RandomizedSlimefunItemStack itemStack : randomDrops) {
                    SlimefunItemStack drop = itemStack.getRandom();
                    if (drop != null) {
                        e.getBlock().getWorld().dropItemNaturally(location, drop);
                    }
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
