package cz.martinbrom.slimybees;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import cz.martinbrom.slimybees.commands.CommandTabExecutor;
import cz.martinbrom.slimybees.core.BeeAnalysisService;
import cz.martinbrom.slimybees.core.BeeDiscoveryService;
import cz.martinbrom.slimybees.core.BeeLifespanService;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeProductionService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.BlockSearchService;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.SlimyBeesRegistry;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.ChromosomeParser;
import cz.martinbrom.slimybees.core.genetics.GenomeParser;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleService;
import cz.martinbrom.slimybees.listeners.BeeEnterListener;
import cz.martinbrom.slimybees.listeners.SlimyBeesPlayerProfileListener;
import cz.martinbrom.slimybees.listeners.TreeGrowListener;
import cz.martinbrom.slimybees.setup.AlleleSetup;
import cz.martinbrom.slimybees.setup.BeeSetup;
import cz.martinbrom.slimybees.setup.CategorySetup;
import cz.martinbrom.slimybees.setup.CommandSetup;
import cz.martinbrom.slimybees.setup.ItemSetup;
import cz.martinbrom.slimybees.worldgen.NestPopulator;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

/**
 * This is the main class for the SlimyBees addon
 *
 * @author martinbrom
 */
@ParametersAreNonnullByDefault
public class SlimyBeesPlugin extends JavaPlugin implements SlimefunAddon {

    private static SlimyBeesPlugin instance;

    private final SlimyBeesRegistry slimyBeesRegistry = new SlimyBeesRegistry();
    private final AlleleRegistry alleleRegistry = new AlleleRegistry();
    private final BeeRegistry beeRegistry = new BeeRegistry();
    private final Config config = new Config(this);

    private final CustomItemDataService beeTypeService = new CustomItemDataService(this, "bee_type");
    private final BeeLoreService beeLoreService = new BeeLoreService();
    private final BlockSearchService blockSearchService = new BlockSearchService();
    private final AlleleService alleleService = new AlleleService(alleleRegistry);
    private final ChromosomeParser chromosomeParser = new ChromosomeParser(beeRegistry, alleleRegistry);
    private final GenomeParser genomeParser = new GenomeParser(chromosomeParser);
    private final BeeLifespanService beeLifespanService = new BeeLifespanService(config);
    private final BeeGeneticService beeGeneticService = new BeeGeneticService(beeTypeService, beeLoreService, beeRegistry,
            genomeParser, alleleRegistry, beeLifespanService);
    private final BeeProductionService beeProductionService = new BeeProductionService(beeLifespanService);
    private final BeeDiscoveryService beeDiscoveryService = new BeeDiscoveryService(alleleRegistry, config);
    private final BeeAnalysisService beeAnalysisService = new BeeAnalysisService(beeGeneticService,
            beeDiscoveryService, beeLoreService);

    private boolean isUnitTest = false;

    // TODO: 03.06.21 Maybe convert to local variable in the CommandSetup class
    private final CommandTabExecutor commandTabExecutor = new CommandTabExecutor();

    public SlimyBeesPlugin() {
        super();
    }

    public SlimyBeesPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);

        isUnitTest = true;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!isUnitTest) {
            onPluginStart();
        }
    }

    public void onPluginStart() {
        logger().info("Started loading SlimyBees");

        // TODO: 15.05.21 Config stuff
        // TODO: 15.05.21 Auto update

        CategorySetup.setUp(this);
        ItemSetup.setUp(this);
        AlleleSetup.setUp();
        BeeSetup.setUp(this);

        beeDiscoveryService.loadGlobalDiscoveries();

        // TODO: 26.06.21 Set up commands for unit tests as well,
        //  but make sure to only register this once, I feel like the onEnable
        //  is called for every unit test class
        CommandSetup.setUp(this);

        registerListeners(this);
        registerNestPopulators();

        int interval = 5;
        getServer().getScheduler().runTaskTimer(this, this::saveAllPlayers, 2000L, interval * 60L * 20L);

        logger().info("Finished loading SlimyBees");
    }

    @Override
    public void onDisable() {
        instance = null;

        Bukkit.getScheduler().cancelTasks(this);

        if (!isUnitTest) {
            slimyBeesRegistry.getPlayerProfiles().values().iterator().forEachRemaining(profile -> {
                if (profile.isDirty()) {
                    profile.save();
                }
            });
        }
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/martinbrom/SlimyBees/issues";
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    /**
     * Utility method for {@link NamespacedKey} creation.
     *
     * @param key String key
     * @return The {@link NamespacedKey} instance
     */
    @Nonnull
    public static NamespacedKey getKey(String key) {
        return new NamespacedKey(instance(), key);
    }

    /**
     * Returns the {@link Logger} instance that SlimyBees uses.
     *
     * @return The {@link Logger} instance
     */
    @Nonnull
    public static Logger logger() {
        return instance().getLogger();
    }

    /**
     * This returns the version of SlimyBees that is currently installed.
     *
     * @return The currently installed version of SlimyBees
     */
    @Nonnull
    public static String getVersion() {
        return instance().getDescription().getVersion();
    }

    // TODO: 29.06.21 Get rid of this static bullshit
    @Nonnull
    public static SlimyBeesRegistry getRegistry() {
        return instance().slimyBeesRegistry;
    }

    @Nonnull
    public static AlleleRegistry getAlleleRegistry() {
        return instance().alleleRegistry;
    }

    @Nonnull
    public static AlleleService getAlleleService() {
        return instance().alleleService;
    }

    @Nonnull
    public static BeeRegistry getBeeRegistry() {
        return instance().beeRegistry;
    }

    @Nonnull
    public static BeeGeneticService getBeeGeneticService() {
        return instance().beeGeneticService;
    }

    @Nonnull
    public static BeeProductionService getBeeProductionService() {
        return instance().beeProductionService;
    }

    @Nonnull
    public static BeeAnalysisService getBeeAnalysisService() {
        return instance().beeAnalysisService;
    }

    @Nonnull
    public static BeeDiscoveryService getBeeDiscoveryService() {
        return instance().beeDiscoveryService;
    }

    @Nonnull
    public static BeeLoreService getBeeLoreService() {
        return instance().beeLoreService;
    }

    @Nonnull
    public static BlockSearchService getBlockSearchService() {
        return instance().blockSearchService;
    }

    /**
     * Returns the global instance of {@link SlimyBeesPlugin}.
     * This may return null if the {@link Plugin} was disabled.
     *
     * @return The {@link SlimyBeesPlugin} instance
     */
    @Nonnull
    public static SlimyBeesPlugin instance() {
        validateInstance();
        return instance;
    }

    @Nonnull
    public CommandTabExecutor getCommandTabExecutor() {
        return commandTabExecutor;
    }

    /**
     * This private static method allows us to throw a proper {@link Exception}
     * whenever someone tries to access a static method while the instance is null.
     * This happens when the method is invoked before {@link #onEnable()} or after {@link #onDisable()}.
     * <p>
     * Use it whenever a null check is needed to avoid a non-descriptive {@link NullPointerException}.
     */
    private static void validateInstance() {
        if (instance == null) {
            throw new IllegalStateException("Cannot invoke static method, SlimyBees instance is null.");
        }
    }

    /**
     * This method registers all of our {@link Listener}s.
     */
    private void registerListeners(SlimyBeesPlugin plugin) {
        new BeeEnterListener(plugin);
        new SlimyBeesPlayerProfileListener(plugin);

        double treeSpawnChance = config.getDouble("nests.tree-growth-chance");
        if (treeSpawnChance > 0) {
            new TreeGrowListener(plugin, slimyBeesRegistry, treeSpawnChance);
        }
    }

    /**
     * This method registers all of our {@link NestPopulator}s.
     */
    private void registerNestPopulators() {
        Logger logger = getLogger();

        double baseNestChance = config.getDouble("nests.world-chance-modifier");
        NestPopulator overworldPopulator = new NestPopulator(slimyBeesRegistry, baseNestChance);
        NestPopulator netherPopulator = new NestPopulator(slimyBeesRegistry, baseNestChance);
        NestPopulator endPopulator = new NestPopulator(slimyBeesRegistry, baseNestChance);

        List<String> worldNames = config.getStringList("nests.worlds");
        for (String worldName : worldNames) {
            World world = getServer().getWorld(worldName);
            if (world != null) {
                World.Environment environment = world.getEnvironment();
                logger.info("Registering nest populators for world: " + worldName);

                List<BlockPopulator> worldPopulators = world.getPopulators();
                switch (environment) {
                    case NORMAL:
                        worldPopulators.add(overworldPopulator);
                        break;
                    case NETHER:
                        worldPopulators.add(netherPopulator);
                        break;
                    case THE_END:
                        worldPopulators.add(endPopulator);
                        break;
                }
            } else {
                logger.warning("Cannot register a nest populator for world: " + worldName + " because it doesn't exist!");
            }
        }
    }

    private void saveAllPlayers() {
        Iterator<SlimyBeesPlayerProfile> iterator = slimyBeesRegistry
                .getPlayerProfiles()
                .values()
                .iterator();

        while (iterator.hasNext()) {
            SlimyBeesPlayerProfile profile = iterator.next();

            if (profile.isDirty()) {
                profile.save();
            }

            if (profile.isMarkedForDeletion()) {
                iterator.remove();
            }
        }
    }

}
