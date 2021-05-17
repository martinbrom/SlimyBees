package cz.martinbrom.slimybees.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityEnterBlockEvent;

import cz.martinbrom.slimybees.items.bees.BeeNest;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

// TODO: 16.05.21 Javadoc
public class BeeEnterListener {

    // TODO: 16.05.21 Javadoc
    @EventHandler
    public void onBeeEnter(EntityEnterBlockEvent e) {
        if (e.getBlock().getType() != Material.BEE_NEST) {
            return;
        }

        SlimefunItem sfItem = BlockStorage.check(e.getBlock());
        if (sfItem instanceof BeeNest) {
            e.setCancelled(true);
        }
    }

}
