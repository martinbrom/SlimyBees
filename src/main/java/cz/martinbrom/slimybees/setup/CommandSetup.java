package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.commands.AnalyzeCommand;
import cz.martinbrom.slimybees.commands.CommandTabExecutor;

@ParametersAreNonnullByDefault
public class CommandSetup {

    private static boolean initialized = false;

    // prevent instantiation
    private CommandSetup() {
    }

    public static void setUp(SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees Commands can only be registered once!");
        }

        initialized = true;

        CommandTabExecutor tabExecutor = plugin.getCommandTabExecutor();
        tabExecutor.registerCommand(new AnalyzeCommand());

        plugin.getCommand("slimybees").setExecutor(tabExecutor);
        plugin.getCommand("slimybees").setTabCompleter(tabExecutor);
    }

}
