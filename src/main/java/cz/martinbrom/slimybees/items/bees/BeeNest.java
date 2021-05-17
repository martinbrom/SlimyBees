package cz.martinbrom.slimybees.items.bees;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.utils.RecipeTypes;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

// TODO: 16.05.21 Javadoc
public class BeeNest extends SlimefunItem {

    private final SlimefunItemStack beeStack;

    // TODO: 16.05.21 Javadoc
    @ParametersAreNonnullByDefault
    public BeeNest(SlimefunItemStack beeNestStack, SlimefunItemStack beeStack) {
        super(Categories.GENERAL, beeNestStack, RecipeTypes.WILDERNESS, new ItemStack[9]);

        this.beeStack = beeStack;
        addItemHandler(onBlockBreak());
    }

    // TODO: 16.05.21 Javadoc
    @Nonnull
    private BlockBreakHandler onBlockBreak() {
        return new BlockBreakHandler(false, false) {

            @Override
            @ParametersAreNonnullByDefault
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                Location location = e.getBlock().getLocation();
                e.getBlock().getWorld().dropItemNaturally(location, beeStack);
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
