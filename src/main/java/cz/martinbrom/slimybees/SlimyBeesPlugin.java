package cz.martinbrom.slimybees;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import cz.martinbrom.slimybees.setup.CategorySetup;
import cz.martinbrom.slimybees.setup.ItemSetup;
import cz.martinbrom.slimybees.setup.PopulatorSetup;
import cz.martinbrom.slimybees.setup.ResearchSetup;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

/**
 * This is the main class for the SlimyBees addon
 *
 * @author martinbrom
 */
public class SlimyBeesPlugin extends JavaPlugin implements SlimefunAddon {

    private static SlimyBeesPlugin instance;

    private final List<BlockPopulator> populators = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        logger().info("Started loading SlimyBees");

        // TODO: 15.05.21 Config stuff
        // TODO: 15.05.21 Auto update

        CategorySetup.setUp(this);
        ItemSetup.setUp(this);
        ResearchSetup.setUp(this);
        PopulatorSetup.setUp(this);

        World endWorld = getServer().getWorld("world_the_end");
        if (endWorld != null) {
            endWorld.getPopulators().addAll(populators);
        }

        logger().info("Finished loading SlimyBees");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    // TODO: 16.05.21 Add github URL
    @Override
    public String getBugTrackerURL() {
        return null;
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
    public final NamespacedKey getKey(@Nonnull String key) {
        return new NamespacedKey(this, key);
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
    public List<BlockPopulator> getPopulators() {
        return populators;
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

}
