package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.items.bees.BeeHive;
import cz.martinbrom.slimybees.items.bees.Beealyzer;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This is the place where all items from SlimyBees are registered.
 */
@ParametersAreNonnullByDefault
public class ItemSetup {

    private static boolean initialized = false;

    // prevent instantiation
    private ItemSetup() {
    }

    public static void setUp(SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees items can only be registered once!");
        }

        initialized = true;

        // <editor-fold desc="Various" defaultstate="collapsed">
        new Beealyzer(Categories.GENERAL, ItemStacks.BEEALYZER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                SlimefunItems.PLASTIC_SHEET, new ItemStack(Material.WHITE_STAINED_GLASS), SlimefunItems.PLASTIC_SHEET,
                SlimefunItems.ELECTRO_MAGNET, ItemStacks.HONEY_COMB, SlimefunItems.ELECTRO_MAGNET,
                SlimefunItems.PLASTIC_SHEET, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.PLASTIC_SHEET
        }).register(plugin);
        // </editor-fold>

        // <editor-fold desc="Bee Products" defaultstate="collapsed">
        registerAndHide(ItemStacks.HONEY_COMB, plugin);
        registerAndHide(ItemStacks.DRY_COMB, plugin);
        registerAndHide(ItemStacks.SWEET_COMB, plugin);
        // </editor-fold>

        // <editor-fold desc="Specialty Products" defaultstate="collapsed">
        registerAndHide(ItemStacks.BEESWAX, plugin);
        registerAndHide(ItemStacks.HONEY_DROP, plugin);
        // TODO: 03.06.21 Enchanted?
        registerAndHide(ItemStacks.ROYAL_JELLY, plugin);
        // TODO: 03.06.21 Enchanted?
        registerAndHide(ItemStacks.POLLEN, plugin);
        // </editor-fold>

        // <editor-fold desc="Machines" defaultstate="collapsed">
        new BeeHive(Categories.GENERAL, ItemStacks.BEE_HIVE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS),
                ItemStacks.HONEY_COMB, ItemStacks.HONEY_COMB, ItemStacks.HONEY_COMB,
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS),
        }).register(plugin);
        // </editor-fold>
    }

    public static void registerAndHide(SlimefunItemStack itemStack, SlimyBeesPlugin plugin) {
        SlimefunItem item = new SlimefunItem(Categories.GENERAL, itemStack, RecipeType.NULL, new ItemStack[9]);
        item.register(plugin);
        // TODO 18.05.21: Set hidden
//        item.setHidden(true);
    }

}
