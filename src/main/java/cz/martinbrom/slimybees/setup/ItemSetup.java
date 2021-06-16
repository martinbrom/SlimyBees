package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.recipe.AbstractRecipe;
import cz.martinbrom.slimybees.items.machines.BeeBreeder;
import cz.martinbrom.slimybees.items.machines.BeeHive;
import cz.martinbrom.slimybees.items.bees.Beealyzer;
import cz.martinbrom.slimybees.items.machines.ElectricCentrifuge;
import cz.martinbrom.slimybees.items.multiblocks.Centrifuge;
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

        // TODO: 04.06.21 Tome of Discovery Sharing
        // </editor-fold>

        // <editor-fold desc="Bee Products" defaultstate="collapsed">
        registerAndHide(ItemStacks.HONEY_COMB, plugin);
        registerAndHide(ItemStacks.DRY_COMB, plugin);
        registerAndHide(ItemStacks.SWEET_COMB, plugin);
        // </editor-fold>

        // <editor-fold desc="Specialty Products" defaultstate="collapsed">
        registerAndHide(ItemStacks.BEESWAX, plugin);
        registerAndHide(ItemStacks.HONEY_DROP, plugin);
        registerAndHide(ItemStacks.ROYAL_JELLY, plugin);
        registerAndHide(ItemStacks.POLLEN, plugin);
        // </editor-fold>

        // <editor-fold desc="Machines" defaultstate="collapsed">
        new BeeHive(Categories.GENERAL, ItemStacks.BEE_HIVE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS),
                new ItemStack(Material.DANDELION), ItemStacks.HONEY_COMB, new ItemStack(Material.POPPY),
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS),
        }).register(plugin);

        new BeeBreeder(Categories.GENERAL, ItemStacks.BEE_BREEDER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS),
                ItemStacks.HONEY_COMB, ItemStacks.HONEY_COMB, ItemStacks.HONEY_COMB,
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS),
        }).register(plugin);

        Centrifuge centrifuge = new Centrifuge(Categories.GENERAL, ItemStacks.CENTRIFUGE);
        centrifuge.register(plugin);

        ElectricCentrifuge elCentrifuge = new ElectricCentrifuge(Categories.GENERAL, ItemStacks.ELECTRIC_CENTRIFUGE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                null, SlimefunItems.MEDIUM_CAPACITOR, null,
                SlimefunItems.NICKEL_INGOT, new ItemStack(Material.IRON_BLOCK), SlimefunItems.COBALT_INGOT,
                null, SlimefunItems.ELECTRIC_MOTOR, null });
        elCentrifuge.setProcessingSpeed(1).setCapacity(128).setEnergyConsumption(6).register(plugin);

        ElectricCentrifuge elCentrifuge2 = new ElectricCentrifuge(Categories.GENERAL, ItemStacks.ELECTRIC_CENTRIFUGE_2, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                null, SlimefunItems.LARGE_CAPACITOR, null,
                SlimefunItems.REINFORCED_PLATE, ItemStacks.ELECTRIC_CENTRIFUGE, SlimefunItems.REINFORCED_PLATE,
                null, SlimefunItems.ELECTRIC_MOTOR, null });
        elCentrifuge2.setProcessingSpeed(4).setCapacity(512).setEnergyConsumption(18).register(plugin);

        for (AbstractRecipe recipe : centrifuge.getCentrifugeRecipes()) {
            elCentrifuge.registerRecipe(recipe);
            elCentrifuge2.registerRecipe(recipe);
        }

        // </editor-fold>
    }

    public static void registerAndHide(SlimefunItemStack itemStack, SlimyBeesPlugin plugin) {
        SlimefunItem item = new SlimefunItem(Categories.GENERAL, itemStack, RecipeType.NULL, new ItemStack[9]);
        item.register(plugin);
        // TODO 18.05.21: Set hidden
//        item.setHidden(true);
    }

}
