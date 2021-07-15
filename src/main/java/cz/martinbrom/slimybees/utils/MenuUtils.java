package cz.martinbrom.slimybees.utils;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.utils.types.PentaPredicate;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;

/**
 * This class contains useful function(s) for working with Block & Chest menus.
 */
@ParametersAreNonnullByDefault
public class MenuUtils {

    /**
     * Draws the background and input & output borders.
     *
     * @param preset The {@link BlockMenuPreset} to draw onto
     * @param backgroundSlots The background slot numbers
     * @param inputBorderSlots The input border slot numbers
     * @param outputBorderSlots The output border slot numbers
     */
    public static void draw(BlockMenuPreset preset, int[] backgroundSlots, int[] inputBorderSlots, int[] outputBorderSlots) {
        Validate.notNull(preset, "Cannot draw onto a null BlockMenuPreset!");

        for (int slot : backgroundSlots) {
            preset.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int slot : inputBorderSlots) {
            preset.addItem(slot, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int slot : outputBorderSlots) {
            preset.addItem(slot, ChestMenuUtils.getOutputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }
    }

    /**
     * Returns a {@link ChestMenu} click handler which only allows for items
     * to be removed, not inserted.
     *
     * @return A {@link ChestMenu.AdvancedMenuClickHandler} which only allows removal of items
     */
    public static ChestMenu.AdvancedMenuClickHandler getRemoveOnlyClickHandler() {
        return createAdvancedHandler((e, p, s, i, a) -> i.getType().isAir());
    }

    /**
     * Creates a {@link ChestMenu} click handler from given lambda.
     *
     * @param predicate The lambda to test arguments against
     * @return A {@link ChestMenu} click handler from given lambda
     */
    public static ChestMenu.AdvancedMenuClickHandler createAdvancedHandler(
            PentaPredicate<InventoryClickEvent, Player, Integer, ItemStack, ClickAction> predicate) {

        return new ChestMenu.AdvancedMenuClickHandler() {
            @Override
            public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                return predicate.test(e, p, slot, cursor, action);
            }

            @Override
            public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
                return false;
            }
        };
    }

}
