package cz.martinbrom.slimybees.commands;

import java.util.List;
import java.util.Locale;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;

@ParametersAreNonnullByDefault
public abstract class AbstractCommand {

    private final String name;
    private final String description;
    private final String permission;

    public AbstractCommand(String name, String description, String permission) {
        this.name = name.toLowerCase(Locale.ROOT);
        this.description = description;
        this.permission = permission;
    }
    public abstract void onExecute(CommandSender sender, String[] args);

    public abstract List<String> onTab(CommandSender sender, String[] args);

    public final boolean hasPermission(CommandSender sender) {
        return sender.isOp() || sender.hasPermission(this.permission);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
