package cz.martinbrom.slimybees.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BeeDiscoveryService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.utils.GeneticUtil;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

@ParametersAreNonnullByDefault
public class DiscoverCommand extends AbstractCommand {

    private final BeeDiscoveryService beeDiscoveryService;

    public DiscoverCommand(BeeDiscoveryService beeDiscoveryService) {
        super("discover", "Marks given bee species as discovered", "slimybess.command.discover");

        this.beeDiscoveryService = beeDiscoveryService;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.only-players");
            return;
        }

        if (args.length != 2) {
            // TODO: 04.06.21 Common method to print usage with colors and stuff
            sender.sendMessage("Usage: /slimybees discover <species | all | reset>");
            return;
        }

        // TODO: 05.06.21 Add an argument to execute this for someone else (like /sf research)
        Player p = (Player) sender;
        if (args[1].equals("all")) {
            discoverAll(p);
        } else if (args[1].equals("reset")) {
            beeDiscoveryService.undiscoverAll(p);
            p.sendMessage(ChatColor.GREEN + "Succesfully marked all bees as undiscovered!");
        } else {
            discoverSpecies(p, args[1]);
        }
    }

    private void discoverAll(Player p) {
        long discoveredCount = beeDiscoveryService.discoverAll(p);
        if (discoveredCount > 0) {
            String countSuffix = discoveredCount == 1 ? "" : "s";
            p.sendMessage(ChatColor.GREEN + "Succesfully marked "
                    + ChatColor.BOLD + discoveredCount
                    + ChatColor.RESET + ChatColor.GREEN + " bee" + countSuffix + " as discovered!");
        } else {
            p.sendMessage(ChatColor.DARK_GRAY + "There are no more bees to discover!");
        }
    }

    private void discoverSpecies(Player p, String speciesName) {
        AlleleSpecies species = GeneticUtil.getSpeciesByName(speciesName);
        if (species != null) {
            if (!beeDiscoveryService.discover(p, species, true)) {
                p.sendMessage(ChatColor.DARK_GRAY + "You have already discovered this species!");
            }
        } else {
            p.sendMessage(ChatColor.RED + "Did not find any bee species with the name: "
                    + ChatColor.BOLD + speciesName
                    + ChatColor.RESET + ChatColor.RED + "!");
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        List<String> names = SlimyBeesPlugin.getAlleleRegistry().getAllSpeciesNames();
        names.add("all");
        names.add("reset");

        return names;
    }

}
