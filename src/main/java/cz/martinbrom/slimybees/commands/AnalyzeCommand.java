package cz.martinbrom.slimybees.commands;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.martinbrom.slimybees.core.genetics.BeeAnalysisService;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

@ParametersAreNonnullByDefault
public class AnalyzeCommand extends AbstractCommand {

    public AnalyzeCommand() {
        super("analyze", "Analyzes unknown bees and updates the lore.", "slimybess.command.analyze");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            SlimefunPlugin.getLocalization().sendMessage(sender, "messages.only-players");
            return;
        }

        if (args.length != 2) {
            // TODO: 04.06.21 Common method to print usage with colors and stuff
            sender.sendMessage("Usage: /slimybees analyze <hand | all>");
            return;
        }

        Player p = (Player) sender;
        PlayerInventory inventory = p.getInventory();
        int analyzedCount;
        if (args[1].equals("all")) {
            analyzedCount = analyzeInventory(inventory);
        } else if (args[1].equals("hand")) {
            analyzedCount = analyzeHeldItem(inventory);
        } else {
            sender.sendMessage("Usage: /slimybees analyze <hand | all>");
            return;
        }

        if (analyzedCount > 0) {
            sender.sendMessage(ChatColor.GREEN + "Succesfully analyzed "
                    + ChatColor.BOLD + analyzedCount
                    + ChatColor.RESET + ChatColor.GREEN + " bee" + (analyzedCount > 1 ? "s" : "") + "!");
        } else {
            sender.sendMessage(ChatColor.DARK_GRAY + "Did not find any bees to analyze!");
        }
    }

    private int analyzeHeldItem(PlayerInventory inventory) {
        return analyzeSlot(inventory, inventory.getHeldItemSlot());
    }

    private int analyzeInventory(PlayerInventory inventory) {
        int analyzedCount = 0;

        // Indexes 0 through 8 refer to the hotbar. 9 through 35 refer to the main inventory.
        for (int i = 0; i < 36; i++) {
            analyzedCount += analyzeSlot(inventory, i);
        }

        // Index 40 refers to the off hand item slot.
        return analyzedCount + analyzeSlot(inventory, 40);
    }

    private int analyzeSlot(PlayerInventory inventory, int slot) {
        ItemStack item = inventory.getItem(slot);
        if (item != null && !item.getType().isAir()) {
            ItemStack analyzedItem = BeeAnalysisService.analyze(item);
            if (analyzedItem != null) {
                inventory.setItem(slot, analyzedItem);
                return analyzedItem.getAmount();
            }
        }

        return 0;
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        // TODO: 04.06.21 Filter
        return Arrays.asList("all", "hand");
    }

}