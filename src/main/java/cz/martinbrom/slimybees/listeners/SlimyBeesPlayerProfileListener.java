package cz.martinbrom.slimybees.listeners;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;

@ParametersAreNonnullByDefault
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
        // find() instead of get() because we will only delete the profile
        // if there is any, no need to load it for that
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.find(e.getPlayer().getUniqueId());

        if (profile != null) {
            profile.markForDeletion();
        }
    }

}
