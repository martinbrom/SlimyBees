package cz.martinbrom.slimybees.items.bees;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.InvUtils;

@ParametersAreNonnullByDefault
public class Beealyzer extends SimpleSlimefunItem<ItemUseHandler> implements Rechargeable {

    public static final int MAX_CHARGE_AMOUNT = 50;

    private static final int[] BACKGROUND_SLOTS = { 0, 1, 2, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 24, 25, 26 };
    private static final int[] ITEM_BORDER_SLOTS = { 3, 4, 5, 12, 14, 21, 22, 23 };
    private static final int ITEM_SLOT = 13;

    private final Map<UUID, BukkitRunnable> tickingMap = new HashMap<>();

    // TODO: 17.05.21 Add double setting for energy consumption
    public Beealyzer(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        // TODO: 18.05.21 Check if has charge
        return e -> {
            e.getInteractEvent().setCancelled(true);
            createMenu().open(e.getPlayer());
        };
    }

    @Override
    public float getMaxItemCharge(ItemStack itemStack) {
        return MAX_CHARGE_AMOUNT;
    }

    private ChestMenu createMenu() {
        SlimyBeesPlugin plugin = SlimyBeesPlugin.instance();
        ChestMenu menu = new ChestMenu(plugin, "Beealyzer");

        for (int slot : BACKGROUND_SLOTS) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), InvUtils.EMPTY_CLICK);
        }

        SlimefunItemStack itemBorder = new SlimefunItemStack("_UI_BEEALYZER_SLOT_BORDER", Material.YELLOW_STAINED_GLASS_PANE, " ");
        for (int slot : ITEM_BORDER_SLOTS) {
            menu.addItem(slot, itemBorder, InvUtils.EMPTY_CLICK);
        }

        // TODO: 03.06.21 When closing menu add item inside back to inventory (if present)
        menu.addMenuOpeningHandler(p -> onMenuOpen(menu, p));
        menu.addMenuCloseHandler(this::onMenuClose);

        return menu;
    }

    private void onMenuOpen(ChestMenu menu, Player p) {
        BukkitRunnable prevRunnable = tickingMap.get(p.getUniqueId());
        if (prevRunnable != null) {
            tickingMap.remove(p.getUniqueId());
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (menu.toInventory().getViewers().isEmpty()) {
                    cancel();
                }

                Beealyzer.this.analyze(menu);
            }
        };

        runnable.runTaskTimer(SlimyBeesPlugin.instance(), 5L, 5L);
        tickingMap.put(p.getUniqueId(), runnable);
    }

    private void onMenuClose(Player p) {
        BukkitRunnable runnable = tickingMap.get(p.getUniqueId());
        if (runnable != null) {
            runnable.cancel();

            tickingMap.remove(p.getUniqueId());
        }
    }

    private void analyze(ChestMenu menu) {
        ItemStack item = menu.getItemInSlot(ITEM_SLOT);

        ItemStack analyzedItem = SlimyBeesPlugin.getBeeAnalysisService().analyze(item);
        if (analyzedItem != null) {
            // TODO: 03.06.21 Play sound / Spawn particle ??
            menu.consumeItem(ITEM_SLOT, analyzedItem.getAmount(), false);
            menu.addItem(ITEM_SLOT, analyzedItem);
        }
    }


}
