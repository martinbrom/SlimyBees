package cz.martinbrom.slimybees.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.martinbrom.slimybees.core.BeeLoreService;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

@ParametersAreNonnullByDefault
public class MakeUnknownCommand extends AbstractCommand {

    private final BeeLoreService loreService;

    public MakeUnknownCommand(BeeLoreService loreService) {
        super("makeunknown", "Makes the bee unknown.", "slimybees.command.make_unknown");

        this.loreService = loreService;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.only-players");
            return;
        }

        Player p = (Player) sender;
        PlayerInventory inv = p.getInventory();

        ItemStack result = loreService.makeUnknown(inv.getItemInMainHand());
        if (loreService.isUnknown(result)) {
            inv.setItemInMainHand(result);
            p.sendMessage(ChatColor.GREEN + "Successfully made the bee unknown!");
        } else {
            p.sendMessage(ChatColor.DARK_GRAY + "The item in your hand is not a valid bee!");
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
