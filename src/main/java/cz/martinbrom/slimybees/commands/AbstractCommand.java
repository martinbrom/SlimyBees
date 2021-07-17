package cz.martinbrom.slimybees.commands;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;

@ParametersAreNonnullByDefault
public abstract class AbstractCommand {

    private final String name;
    private final String description;
    private final String permission;

    protected AbstractCommand(String name, String description) {
        this(name, description, null);
    }

    protected AbstractCommand(String name, String description, @Nullable String permission) {
        this.name = name.toLowerCase(Locale.ROOT);
        this.description = description;
        this.permission = permission;
    }

    public abstract void onExecute(CommandSender sender, String[] args);

    @Nonnull
    public abstract List<String> onTab(CommandSender sender, String[] args);

    public final boolean hasPermission(CommandSender sender) {
        return permission == null || sender.isOp() || sender.hasPermission(permission);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

}
