package cz.martinbrom.slimybees;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;

import io.github.thebusybiscuit.slimefun4.core.categories.MultiCategory;
import io.github.thebusybiscuit.slimefun4.core.categories.SubCategory;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

/**
 * Instance of this class holds a reference to every {@link Category} found in SlimyBees.
 */
public class Categories {

    public static final String CATEGORY_KEY = "slimybees";
    public static final String CATEGORY_TITLE = "Slimy Bees";

    public final MultiCategory MAIN_CATEGORY;
    public final Category GENERAL;

    private final SlimyBees plugin;

    public Categories(@Nonnull SlimyBees plugin) {
        this.plugin = plugin;

        MAIN_CATEGORY = new MultiCategory(
                getKey(null),
                new CustomItem(SlimyBeesHeadTexture.BEE.getAsItemStack(), getTitle(null))
        );
        MAIN_CATEGORY.register(plugin);

        GENERAL = new SubCategory(
                getKey("general"),
                MAIN_CATEGORY,
                new CustomItem(SlimyBeesHeadTexture.HIVE.getAsItemStack(), getTitle("General"))
        );
        GENERAL.register(plugin);
    }

    @Nonnull
    private NamespacedKey getKey(@Nullable String suffix) {
        String keyStr = CATEGORY_KEY;
        if (suffix != null && !suffix.isEmpty()) {
            keyStr += "_" + suffix;
        }

        return plugin.getKey(keyStr);
    }

    @Nonnull
    String getTitle(@Nullable String suffix) {
        String title = CATEGORY_TITLE;
        if (suffix != null && !suffix.isEmpty()) {
            title += " - " + suffix;
        }

        return title;
    }
}
