package cz.martinbrom.slimybees.core.machine;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotHopperable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

@ParametersAreNonnullByDefault
public abstract class AbstractTickingContainer extends SlimefunItem implements NotHopperable {

    protected AbstractTickingContainer(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        new BlockMenuPreset(getId(), getItemName()) {
            @Override
            public void init() {
                setupMenu(this);
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                return p.hasPermission("slimefun.inventory.bypass")
                        || Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK);
            }

            @Nonnull
            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.INSERT) {
                    return getInputSlots();
                } else {
                    return getOutputSlots();
                }
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                onNewInstance(menu, b);
            }
        };
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(BlockBreakEvent e, ItemStack itemStack, List<ItemStack> list) {
                BlockMenu menu = BlockStorage.getInventory(e.getBlock());
                if (menu != null) {
                    onBreak(e, menu, e.getBlock().getLocation());
                }
            }
        });

        addItemHandler(new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                onPlace(e, e.getBlockPlaced());
            }
        });

        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return AbstractTickingContainer.this.isSynchronized();
            }

            @Override
            public void tick(Block b, SlimefunItem item, Config data) {
                BlockMenu menu = BlockStorage.getInventory(b);
                if (menu != null) {
                    AbstractTickingContainer.this.tick(menu, b);
                }
            }
        });
    }

    protected abstract void setupMenu(BlockMenuPreset preset);

    @Nonnull
    protected abstract int[] getInputSlots();

    @Nonnull
    protected abstract int[] getOutputSlots();

    protected abstract void tick(BlockMenu menu, Block b);

    protected void onNewInstance(BlockMenu menu, Block b) {
        // do nothing, can be overridden
    }

    protected void onBreak(BlockBreakEvent e, BlockMenu menu, Location l) {
        // do nothing, can be overridden
    }

    protected void onPlace(BlockPlaceEvent e, Block b) {
        // do nothing, can be overridden
    }

    protected boolean isSynchronized() {
        return false;
    }

}
