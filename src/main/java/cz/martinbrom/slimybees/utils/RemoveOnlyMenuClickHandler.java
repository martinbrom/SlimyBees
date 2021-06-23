package cz.martinbrom.slimybees.utils;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;

@ParametersAreNonnullByDefault
public class RemoveOnlyMenuClickHandler implements ChestMenu.AdvancedMenuClickHandler {

    @Override
    public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
        return false;
    }

    @Override
    public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
        return cursor.getType() == Material.AIR;
    }

}
