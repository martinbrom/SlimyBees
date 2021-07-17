package cz.martinbrom.slimybees;

import org.bukkit.Material;

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

}
