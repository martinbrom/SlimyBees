package cz.martinbrom.slimybees;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

/**
 * This is the main class for the SlimyBees addon
 *
 * @author martinbrom
 */
public class SlimyBees extends JavaPlugin implements SlimefunAddon {

    private static SlimyBees instance;

    @Override
    public void onEnable() {
        instance = this;

        // TODO: 15.05.21 Config stuff
        // TODO: 15.05.21 Auto update

        Categories categories = new Categories(this);
        ItemSetup.setup(this, categories);
        ResearchSetup.setup(this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @Override
    public String getBugTrackerURL() {
        return null;
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nonnull
    public final NamespacedKey getKey(@Nonnull String key) {
        return new NamespacedKey(this, key);
    }

    /**
     * This returns the global instance of {@link SlimyBees}.
     * This may return null if the {@link Plugin} was disabled.
     *
     * @return The {@link SlimyBees} instance
     */
    @Nullable
    public static SlimyBees instance() {
        return instance;
    }

}
