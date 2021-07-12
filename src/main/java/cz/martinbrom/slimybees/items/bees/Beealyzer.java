package cz.martinbrom.slimybees.items.bees;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeAnalysisService;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.settings.DoubleRangeSetting;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.InvUtils;

/**
 * This class represents a Beealyzer item, which allows players to "analyze"
 * unknown bees and discover their various traits.
 *
 * @see BeeAnalysisService
 */
@ParametersAreNonnullByDefault
public class Beealyzer extends SimpleSlimefunItem<ItemUseHandler> implements Rechargeable {

    public static final int MAX_CHARGE_AMOUNT = 50;
    public static final double DEFAULT_COST = 2.5;

    private static final int[] BACKGROUND_SLOTS = { 0, 1, 2, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 24, 25, 26 };
    private static final int[] ITEM_BORDER_SLOTS = { 3, 4, 5, 12, 14, 21, 22, 23 };
    private static final int ITEM_SLOT = 13;

    private final BeeAnalysisService analysisService;

    private final Map<UUID, BukkitRunnable> tickingMap = new HashMap<>();
    private final ItemSetting<Double> analyzeCost = new DoubleRangeSetting(this, "analyze-cost", 0, DEFAULT_COST, MAX_CHARGE_AMOUNT);

    public Beealyzer(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        analysisService = SlimyBeesPlugin.getBeeAnalysisService();

        addItemSetting(analyzeCost);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.getInteractEvent().setCancelled(true);

            Player p = e.getPlayer();
            if (removeItemCharge(e.getItem(), analyzeCost.getValue().floatValue())) {
                createMenu().open(p);
            } else {
                p.sendMessage("The beealyzer doesn't have enough energy to function properly! " +
                        "Charge it using the charging bench.");
            }
        };
    }

    @Override
    public float getMaxItemCharge(ItemStack itemStack) {
        return MAX_CHARGE_AMOUNT;
    }

    @Nonnull
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

        menu.addMenuOpeningHandler(p -> onMenuOpen(menu, p));
        menu.addMenuCloseHandler(p -> onMenuClose(menu, p));

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

                Beealyzer.this.analyze(menu, p);
            }
        };

        runnable.runTaskTimer(SlimyBeesPlugin.instance(), 5L, 5L);
        tickingMap.put(p.getUniqueId(), runnable);
    }

    private void onMenuClose(ChestMenu menu, Player p) {
        BukkitRunnable runnable = tickingMap.get(p.getUniqueId());
        if (runnable != null) {
            runnable.cancel();
            tickingMap.remove(p.getUniqueId());

            // if the player left anything in the Beealyzer
            ItemStack item = menu.getItemInSlot(ITEM_SLOT);
            if (item != null && !item.getType().isAir()) {
                // try to add to inventory
                HashMap<Integer, ItemStack> leftoverItems = p.getInventory().addItem(item);

                // drop leftovers on the ground
                Location location = p.getLocation();
                World world = location.getWorld();
                if (world != null) {
                    for (ItemStack leftoverItem : leftoverItems.values()) {
                        world.dropItemNaturally(location, leftoverItem);
                        p.playSound(location, Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1f, 1f);
                    }
                }

                // and clear the Beealyzer
                menu.consumeItem(ITEM_SLOT, false);
            }
        }
    }

    private void analyze(ChestMenu menu, Player p) {
        ItemStack item = menu.getItemInSlot(ITEM_SLOT);

        ItemStack analyzedItem = analysisService.analyze(p, item);
        if (analyzedItem != null) {
            p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            menu.consumeItem(ITEM_SLOT, analyzedItem.getAmount(), false);
            menu.addItem(ITEM_SLOT, analyzedItem);
        }
    }


}
