package cz.martinbrom.slimybees.items.bees;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeDiscoveryService;
import cz.martinbrom.slimybees.utils.PatternUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;

@ParametersAreNonnullByDefault
public class TomeOfDiscoverySharing extends SimpleSlimefunItem<ItemUseHandler> {

    private final NamespacedKey ownerKey = new NamespacedKey(SlimyBeesPlugin.instance(), "owner");

    private final BeeDiscoveryService discoveryService;

    public TomeOfDiscoverySharing(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        this.discoveryService = SlimyBeesPlugin.getBeeDiscoveryService();
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();
            ItemStack item = e.getItem();

            e.setUseBlock(Result.DENY);

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                String ownerStringUUID = pdc.get(ownerKey, PersistentDataType.STRING);
                if (ownerStringUUID == null) {
                    // assign current player as the owner and update PDC and lore
                    pdc.set(ownerKey, PersistentDataType.STRING, p.getUniqueId().toString());
                    updateItemLore(item, meta, p.getName());
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                } else {
                    // discover every discovery of the assigned owner and consume the tome
                    long count = discoveryService.discoverAllByOwner(p, UUID.fromString(ownerStringUUID));
                    if (count == 0) {
                        p.sendMessage(ChatColor.DARK_GRAY + "There were no more bees to discover from this tome!");
                    }

                    if (p.getGameMode() != GameMode.CREATIVE) {
                        ItemUtils.consumeItem(item, false);
                    }
                }
            }
        };
    }

    @ParametersAreNonnullByDefault
    private void updateItemLore(ItemStack item, ItemMeta meta, String owner) {
        List<String> lore = meta.getLore();

        String newLine = ChatColors.color(ItemStacks.loreOwner(owner));
        if (lore != null && !lore.isEmpty()) {
            // find the correct line
            for (int i = 0; i < lore.size(); i++) {
                if (PatternUtil.TOME_OWNER_LORE.matcher(lore.get(i)).matches()) {
                    lore.set(i, newLine);
                    break;
                }
            }
        } else {
            lore = Collections.singletonList(newLine);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

}
