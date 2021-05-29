package cz.martinbrom.slimybees;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.SlimyBeesRegistry;
import cz.martinbrom.slimybees.listeners.BeeEnterListener;
import cz.martinbrom.slimybees.setup.BeeSetup;
import cz.martinbrom.slimybees.setup.CategorySetup;
import cz.martinbrom.slimybees.setup.ItemSetup;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;

/**
 * This is the main class for the SlimyBees addon
 *
 * @author martinbrom
 */
@ParametersAreNonnullByDefault
public class SlimyBeesPlugin extends JavaPlugin implements SlimefunAddon {

    private static SlimyBeesPlugin instance;

    private final SlimyBeesRegistry registry;
    private final CustomItemDataService beeTypeService;

    public SlimyBeesPlugin() {
        super();

        beeTypeService = new CustomItemDataService(this, "bee_type");
        registry = new SlimyBeesRegistry();
    }

    public SlimyBeesPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);

        beeTypeService = new CustomItemDataService(this, "bee_type");
        registry = new SlimyBeesRegistry();
    }

    @Override
    public void onEnable() {
        instance = this;
        logger().info("Started loading SlimyBees");

        // TODO: 15.05.21 Config stuff
        // TODO: 15.05.21 Auto update

        CategorySetup.setUp(this);
        ItemSetup.setUp(this);
        BeeSetup.setUp(this);

        registerListeners(this);

        // TODO: 17.05.21 Add populators to worlds properly
        // TODO: 17.05.21 Add setting to populators limiting world type -> see Environment class
        World world = getServer().getWorld("world");
        World netherWorld = getServer().getWorld("world_nether");
        World endWorld = getServer().getWorld("world_the_end");
        if (world != null) {
            world.getPopulators().addAll(getRegistry().getPopulators());
        }
        if (netherWorld != null) {
            netherWorld.getPopulators().addAll(getRegistry().getPopulators());
        }
        if (endWorld != null) {
            endWorld.getPopulators().addAll(getRegistry().getPopulators());
        }

        int interval = 5;
        getServer().getScheduler().runTaskTimer(this, this::saveAllPlayers, 2000L, interval * 60L * 20L);

        logger().info("Finished loading SlimyBees");
    }

    @Override
    public void onDisable() {
        instance = null;

        Bukkit.getScheduler().cancelTasks(this);

        registry.getPlayerProfiles().values().iterator().forEachRemaining(profile -> {
            if (profile.isDirty()) {
                profile.save();
            }
        });
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

    @Nonnull
    public static SlimyBeesRegistry getRegistry() {
        validateInstance();
        return instance.registry;
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
    public CustomItemDataService getBeeTypeService() {
        return beeTypeService;
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
     * This method registers all of our {@link Listener Listeners}.
     */
    private void registerListeners(SlimyBeesPlugin plugin) {
        new BeeEnterListener(plugin);
    }

    private void saveAllPlayers() {
        Iterator<SlimyBeesPlayerProfile> iterator = SlimyBeesPlugin.getRegistry()
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
