package cz.martinbrom.slimybees.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

@ParametersAreNonnullByDefault
public class AlterCommand extends AbstractCommand {

    private final AlleleRegistry alleleRegistry;
    private final BeeGeneticService geneticService;

    private final List<String> chromosomeTypeNames = Arrays.stream(ChromosomeType.values())
            .map(ChromosomeType::name)
            .collect(Collectors.toList());

    public AlterCommand(AlleleRegistry alleleRegistry, BeeGeneticService geneticService) {
        super("alter", "Alters a chromosome of a bee.", "slimybees.command.alter");

        this.alleleRegistry = alleleRegistry;
        this.geneticService = geneticService;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.only-players");
            return;
        }

        Player p = (Player) sender;
        if (args.length != 3 && args.length != 4) {
            p.sendMessage("Usage: /slimybees alter <chromosome> <value uid> [primary | secondary | both]");
            return;
        }

        ChromosomeType type = ChromosomeType.parse(args[1]);
        if (type == null) {
            p.sendMessage(ChatColor.RED + "Did not find any chromosome with the name: " + args[1] + "!");
            return;
        }

        List<String> alleleUids = alleleRegistry.getAllUidsByChromosomeType(type);
        if (!alleleUids.contains(args[2])) {
            p.sendMessage(ChatColor.RED + "Did not find any allele value with the uid: " + args[2] + "!");
            return;
        }

        // if the last argument is missing, we assume the player means "both" as that is the most common use-case
        boolean primary = true;
        boolean secondary = true;
        if (args.length == 4) {
            if (args[3].equalsIgnoreCase("primary")) {
                secondary = false;
            } else if (args[3].equalsIgnoreCase("secondary")) {
                primary = false;
            } else if (!args[3].equalsIgnoreCase("both")) {
                p.sendMessage(ChatColor.RED + "Could not determine which allele to alter. " +
                        "Please use either \"primary\", \"secondary\", or \"both\"!");
                return;
            }
        }

        PlayerInventory inv = p.getInventory();
        ItemStack result = geneticService.alterItemGenome(inv.getItemInMainHand(), type,
                args[2], primary, secondary);
        if (result == null) {
            p.sendMessage(ChatColor.DARK_GRAY + "The item in your hand is not a valid bee!");
        } else {
            inv.setItemInMainHand(result);
            p.sendMessage(ChatColor.GREEN + "Successfully updated the bees allele" + ((primary && secondary) ? "s" : "") + "!");
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return chromosomeTypeNames;
        } else if (args.length == 3) {
            ChromosomeType type = ChromosomeType.parse(args[1]);
            if (type != null) {
                return alleleRegistry.getAllUidsByChromosomeType(type);
            }
        } else if (args.length == 4) {
            return Arrays.asList("primary", "secondary", "both");
        }

        return Collections.emptyList();
    }

}
