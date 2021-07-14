package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.commands.AlterCommand;
import cz.martinbrom.slimybees.commands.AnalyzeCommand;
import cz.martinbrom.slimybees.commands.CommandTabExecutor;
import cz.martinbrom.slimybees.commands.DiscoverCommand;
import cz.martinbrom.slimybees.commands.GlobalProgressCommand;
import cz.martinbrom.slimybees.commands.MakeUnknownCommand;
import cz.martinbrom.slimybees.core.BeeDiscoveryService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;

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

        BeeDiscoveryService discoveryService = SlimyBeesPlugin.getBeeDiscoveryService();
        AlleleRegistry alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();

        CommandTabExecutor tabExecutor = plugin.getCommandTabExecutor();
        tabExecutor.registerCommand(new AlterCommand(alleleRegistry, SlimyBeesPlugin.getBeeGeneticService()));
        tabExecutor.registerCommand(new AnalyzeCommand(SlimyBeesPlugin.getBeeAnalysisService()));
        tabExecutor.registerCommand(new DiscoverCommand(discoveryService, alleleRegistry));
        tabExecutor.registerCommand(new GlobalProgressCommand(discoveryService, alleleRegistry));
        tabExecutor.registerCommand(new MakeUnknownCommand(SlimyBeesPlugin.getBeeLoreService()));

        plugin.getCommand("slimybees").setExecutor(tabExecutor);
        plugin.getCommand("slimybees").setTabCompleter(tabExecutor);
    }

}
