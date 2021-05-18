package cz.martinbrom.slimybees.items.bees;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.MenuClickHandler;

@ParametersAreNonnullByDefault
public class Beealyzer extends SimpleSlimefunItem<ItemUseHandler> implements Rechargeable {

    public static final MenuClickHandler EMPTY_CLICK_HANDLER = (p, i, s1, s2, c) -> false;

    private static final int[] BACKGROUND_SLOTS = { 0, 1, 2, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 24, 25, 26 };
    private static final int[] ITEM_BORDER_SLOTS = { 3, 4, 5, 12, 14, 21, 22, 23 };
    private static final int ITEM_SLOT = 13;

    private final ChestMenu menu;
    private final Map<UUID, Integer> tickingMap = new HashMap<>();
    private final CustomItemDataService itemDataService;

    // TODO: 17.05.21 Add double setting for energy consumption
    public Beealyzer(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
        itemDataService = new CustomItemDataService(SlimyBeesPlugin.instance(), "genome");

        menu = createMenu();
    }

    private ChestMenu createMenu() {
        SlimyBeesPlugin plugin = SlimyBeesPlugin.instance();
        ChestMenu menu = new ChestMenu(plugin, "Beealyzer");

        for (int slot : BACKGROUND_SLOTS) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), EMPTY_CLICK_HANDLER);
        }

        SlimefunItemStack itemBorder = new SlimefunItemStack("_UI_BEEALYZER_SLOT_BORDER", Material.YELLOW_STAINED_GLASS_PANE, " ");
        for (int slot : ITEM_BORDER_SLOTS) {
            menu.addItem(slot, itemBorder, EMPTY_CLICK_HANDLER);
        }

        menu.addMenuOpeningHandler(p -> {
            int taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 0L, 5L);

            if (taskId != -1) {
                tickingMap.put(p.getUniqueId(), taskId);
            }
        });

        // TODO: 18.05.21 Fix when kicked
        menu.addMenuCloseHandler(p -> {
            Integer taskId = tickingMap.get(p.getUniqueId());
            if (taskId != null) {
                plugin.getServer().getScheduler().cancelTask(taskId);
            }
        });

        return menu;
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        // TODO: 18.05.21 Check if has charge
        return e -> menu.open(e.getPlayer());
    }

    @Override
    public float getMaxItemCharge(ItemStack itemStack) {
        return 50;
    }

    protected void tick() {
        SlimyBeesPlugin.logger().info("Beealyzer tick");
        ItemStack item = menu.getItemInSlot(ITEM_SLOT);
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem instanceof UnknownBee) {
            Optional<String> data = itemDataService.getItemData(item);
            // analyze item
            menu.consumeItem(ITEM_SLOT, false);

            Pair<AnalyzedBee, UnknownBee> pair = SlimyBeesPlugin.getRegistry().getBeeTypes().get(sfItem.getId());
            if (pair != null) {
                menu.addItem(ITEM_SLOT, pair.getFirstValue().getItem());
            }
        }
    }

}
