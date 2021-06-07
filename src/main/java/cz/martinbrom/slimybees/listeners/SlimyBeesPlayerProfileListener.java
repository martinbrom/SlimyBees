package cz.martinbrom.slimybees.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;

public class SlimyBeesPlayerProfileListener implements Listener {

    public SlimyBeesPlayerProfileListener(SlimyBeesPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        markProfile(e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onKick(PlayerKickEvent e) {
        markProfile(e);
    }

    private void markProfile(PlayerEvent e) {
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.find(e.getPlayer());

        if (profile != null) {
            profile.markForDeletion();
        }
    }

}
