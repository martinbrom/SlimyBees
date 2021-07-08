package cz.martinbrom.slimybees;

import org.bukkit.Material;

import cz.martinbrom.slimybees.core.category.BeeFlexCategory;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

/**
 * This class holds a static reference to every {@link Category} found in SlimyBees.
 */
public class Categories {

    // prevent instantiation
    private Categories() {}

    public static final Category ITEMS = new Category(
            SlimyBeesPlugin.getKey("slimybees_items"),
            new CustomItem(Material.HONEYCOMB, "Slimy Bees"));

    public static final BeeFlexCategory BEE_ATLAS = new BeeFlexCategory(
            SlimyBeesPlugin.getKey("slimybees_atlas"),
            new CustomItem(SlimyBeesHeadTexture.DRONE.getAsItemStack(), "Bee Atlas"));

}
