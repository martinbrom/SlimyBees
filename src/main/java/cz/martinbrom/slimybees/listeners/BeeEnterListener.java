package cz.martinbrom.slimybees.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEnterBlockEvent;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.items.machines.BeeHive;
import cz.martinbrom.slimybees.items.bees.BeeNest;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

// TODO: 16.05.21 Javadoc
public class BeeEnterListener implements Listener {

    public BeeEnterListener(SlimyBeesPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // TODO: 16.05.21 Javadoc
    @EventHandler
    public void onBeeEnter(EntityEnterBlockEvent e) {
        Material material = e.getBlock().getType();
        if (material != Material.BEE_NEST && material != Material.BEEHIVE) {
            return;
        }

        SlimefunItem sfItem = BlockStorage.check(e.getBlock());
        if (sfItem instanceof BeeNest || sfItem instanceof BeeHive) {
            e.setCancelled(true);
        }
    }

}
