package cz.martinbrom.slimybees.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.martinbrom.slimybees.core.BeeDiscoveryService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

@ParametersAreNonnullByDefault
public class GlobalProgressCommand extends AbstractCommand {

    private final BeeDiscoveryService discoveryService;
    private final AlleleRegistry alleleRegistry;

    public GlobalProgressCommand(BeeDiscoveryService discoveryService, AlleleRegistry alleleRegistry) {
        super("globalprogress", "Shows the global discovery progress.");

        this.discoveryService = discoveryService;
        this.alleleRegistry = alleleRegistry;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.only-players");
            return;
        }

        Player p = (Player) sender;
        int totalSpecies = alleleRegistry.getAllSpecies().size();
        Map<String, String> discoveries = discoveryService.getDiscoveryInfo();

        // sort players by their global discovery count
        int discoveredSpecies = 0;
        TreeMap<String, Integer> countsByPlayerName = new TreeMap<>();
        for (String playerName : discoveries.values()) {
            discoveredSpecies++;

            int value = countsByPlayerName.get(playerName) == null ? 0 : countsByPlayerName.get(playerName);
            countsByPlayerName.put(playerName, value + 1);
        }

        // add global discovery header
        int size = countsByPlayerName.size();
        List<String> messages = new ArrayList<>();
        messages.add(ChatColor.GRAY + "Global discovery progress" + ChatColor.WHITE + " (" + discoveredSpecies +  " / " + totalSpecies + ")");
        messages.add("");

        // create a list of all players who hold a global discovery
        String playerName = p.getName();
        if (size > 0) {
            for (Map.Entry<String, Integer> entry : countsByPlayerName.entrySet()) {
                // bold if the player who sent the command is the same as this one
                String color = "" + ChatColor.GRAY + (entry.getKey().equals(playerName) ? ChatColor.BOLD : "");
                messages.add(color + entry.getKey() + ": " + entry.getValue());
            }
        } else {
            messages.add(ChatColor.DARK_GRAY + "No discoveries yet. Good luck!");
        }

        p.sendMessage(messages.toArray(new String[0]));
    }

    @Nonnull
    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
