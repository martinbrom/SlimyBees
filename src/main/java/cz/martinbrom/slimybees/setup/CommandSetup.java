package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.commands.AlterCommand;
import cz.martinbrom.slimybees.commands.AnalyzeCommand;
import cz.martinbrom.slimybees.commands.CommandTabExecutor;
import cz.martinbrom.slimybees.commands.DiscoverCommand;
import cz.martinbrom.slimybees.commands.MakeUnknownCommand;

@ParametersAreNonnullByDefault
public class CommandSetup {

    private static boolean initialized = false;

    // prevent instantiation
    private CommandSetup() {}

    public static void setUp(SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees Commands can only be registered once!");
        }

        initialized = true;

        CommandTabExecutor tabExecutor = plugin.getCommandTabExecutor();
        tabExecutor.registerCommand(new AlterCommand(SlimyBeesPlugin.getAlleleRegistry(), SlimyBeesPlugin.getBeeGeneticService()));
        tabExecutor.registerCommand(new AnalyzeCommand());
        tabExecutor.registerCommand(new DiscoverCommand(SlimyBeesPlugin.getBeeDiscoveryService()));
        tabExecutor.registerCommand(new MakeUnknownCommand(SlimyBeesPlugin.getBeeLoreService()));

        plugin.getCommand("slimybees").setExecutor(tabExecutor);
        plugin.getCommand("slimybees").setTabCompleter(tabExecutor);
    }

}
