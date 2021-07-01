package cz.martinbrom.slimybees;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.NamespacedKey;

import cz.martinbrom.slimybees.core.category.BeeFlexCategory;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.core.categories.MultiCategory;
import io.github.thebusybiscuit.slimefun4.core.categories.SubCategory;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

/**
 * This class holds a static reference to every {@link Category} found in SlimyBees.
 */
public class Categories {

    // prevent instantiation
    private Categories() {}

    public static final MultiCategory MAIN_CATEGORY = new MultiCategory(
            getKey(null),
            new CustomItem(SlimyBeesHeadTexture.DRONE.getAsItemStack(), getTitle(null)));

    public static final Category GENERAL = new SubCategory(
            getKey("general"),
            MAIN_CATEGORY,
            new CustomItem(SlimyBeesHeadTexture.HIVE.getAsItemStack(), getTitle("General")));

    public static final BeeFlexCategory BEE_CATEGORY = new BeeFlexCategory(
            getKey("bees"),
            new CustomItem(SlimyBeesHeadTexture.DRONE.getAsItemStack(), getTitle("Bees")));

    private static final String CATEGORY_KEY = "slimybees";
    private static final String CATEGORY_TITLE = "Slimy Bees";

    @Nonnull
    private static NamespacedKey getKey(@Nullable String suffix) {
        String keyStr = CATEGORY_KEY;
        if (suffix != null && !suffix.isEmpty()) {
            keyStr += "_" + suffix;
        }

        return SlimyBeesPlugin.getKey(keyStr);
    }

    @Nonnull
    private static String getTitle(@Nullable String suffix) {
        String title = CATEGORY_TITLE;
        if (suffix != null && !suffix.isEmpty()) {
            title += " - " + suffix;
        }

        return title;
    }

}
