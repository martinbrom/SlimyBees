package cz.martinbrom.slimybees.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;

@ParametersAreNonnullByDefault
public class CommandTabExecutor implements TabExecutor {

    private final Map<String, AbstractCommand> commandMap = new LinkedHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command commandStr, String label, String[] args) {
        if (args.length > 0) {
            AbstractCommand command = commandMap.get(args[0]);
            if (command != null && command.hasPermission(sender)) {
                command.onExecute(sender, args);
                return true;
            }
        }

        sendHelp(sender);
        return !commandMap.isEmpty();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> strings = commandMap.values().stream()
                    .filter(c -> c.hasPermission(sender))
                    .map(AbstractCommand::getName)
                    .toList();

            return createReturnList(strings, args[0]);
        } else if (args.length > 1) {
            AbstractCommand cmd = commandMap.get(args[0]);
            if (cmd != null && cmd.hasPermission(sender)) {
                return createReturnList(cmd.onTab(sender, args), args[args.length - 1]);
            }
        }

        return Collections.emptyList();
    }

    public void registerCommand(AbstractCommand command) {
        commandMap.put(command.getName(), command);
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColors.color("&aSlimyBees &2v" + SlimyBeesPlugin.getVersion()));
        sender.sendMessage("");

        boolean found = false;
        for (Map.Entry<String, AbstractCommand> entry : commandMap.entrySet()) {
            AbstractCommand cmd = entry.getValue();

            if (cmd.hasPermission(sender)) {
                sender.sendMessage(ChatColors.color("&3/sb " + cmd.getName() + " &b") + cmd.getDescription());
                found = true;
            }
        }

        if (!found) {
            sender.sendMessage(ChatColors.color("&3No available commands!"));
        }
    }

    @Nonnull
    private static List<String> createReturnList(List<String> strings, String string) {
        if (string.length() == 0) {
            return strings;
        }

        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new ArrayList<>();

        for (String item : strings) {
            if (item.toLowerCase(Locale.ROOT).contains(input)) {
                returnList.add(item);

                if (returnList.size() >= 64) {
                    break;
                }
            } else if (item.equalsIgnoreCase(input)) {
                return Collections.emptyList();
            }
        }

        return returnList;
    }

}
