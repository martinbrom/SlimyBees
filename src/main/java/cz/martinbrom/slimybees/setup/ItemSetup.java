package cz.martinbrom.slimybees.setup;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.RecipeTypes;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.recipe.AbstractRecipe;
import cz.martinbrom.slimybees.items.machines.BeeHive;
import cz.martinbrom.slimybees.items.bees.Beealyzer;
import cz.martinbrom.slimybees.items.machines.ElectricCentrifuge;
import cz.martinbrom.slimybees.items.machines.HiveFrame;
import cz.martinbrom.slimybees.items.machines.IndustrialBeeHive;
import cz.martinbrom.slimybees.items.multiblocks.Centrifuge;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
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
    private ItemSetup() {}

    public static void setUp(SlimyBeesPlugin plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees items can only be registered once!");
        }

        initialized = true;

        // <editor-fold desc="Bee Products" defaultstate="collapsed">
        new SlimefunItem(Categories.GENERAL, ItemStacks.BEESWAX, RecipeTypes.CENTRIFUGE, ItemStacks.CENTRIFUGE_COMB_RECIPE).register(plugin);
        new SlimefunItem(Categories.GENERAL, ItemStacks.HONEY_DROP, RecipeTypes.CENTRIFUGE, ItemStacks.CENTRIFUGE_COMB_RECIPE).register(plugin);

        registerBeeProduct(ItemStacks.HONEY_COMB, plugin, true);
        registerBeeProduct(ItemStacks.DRY_COMB, plugin, true);
        registerBeeProduct(ItemStacks.SWEET_COMB, plugin, true);

        VanillaItem honeyBlock = new VanillaItem(Categories.GENERAL, new ItemStack(Material.HONEY_BLOCK), "HONEY_BLOCK", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                ItemStacks.HONEY_DROP, ItemStacks.HONEY_DROP, ItemStacks.HONEY_DROP,
                ItemStacks.HONEY_DROP, ItemStacks.HONEY_DROP, ItemStacks.HONEY_DROP,
                ItemStacks.HONEY_DROP, ItemStacks.HONEY_DROP, ItemStacks.HONEY_DROP
        });
        honeyBlock.setRecipeOutput(new ItemStack(Material.HONEY_BLOCK, 2));
        honeyBlock.register(plugin);

        new VanillaItem(Categories.GENERAL, new ItemStack(Material.HONEY_BOTTLE), "HONEY_BOTTLE", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                new ItemStack(Material.GLASS_BOTTLE), ItemStacks.HONEY_DROP, null,
                null, null, null,
                null, null, null,
        }).register(plugin);

        VanillaItem honeycomb = new VanillaItem(Categories.GENERAL, new ItemStack(Material.HONEYCOMB), "HONEYCOMB", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                ItemStacks.BEESWAX, ItemStacks.BEESWAX, ItemStacks.BEESWAX,
                ItemStacks.BEESWAX, ItemStacks.HONEY_DROP, ItemStacks.BEESWAX,
                ItemStacks.BEESWAX, ItemStacks.BEESWAX, ItemStacks.BEESWAX
        });
        honeycomb.setRecipeOutput(new ItemStack(Material.HONEYCOMB, 4));
        honeycomb.register(plugin);
        // </editor-fold>

        // <editor-fold desc="Specialty Products" defaultstate="collapsed">
        registerBeeProduct(ItemStacks.ROYAL_JELLY, plugin, false);
        registerBeeProduct(ItemStacks.POLLEN, plugin, false);
        // </editor-fold>

        // <editor-fold desc="Frames" defaultstate="collapsed">
        new HiveFrame(Categories.GENERAL, ItemStacks.BASIC_FRAME, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                new ItemStack(Material.STICK), new ItemStack(Material.STICK), new ItemStack(Material.STICK),
                new ItemStack(Material.STICK), new ItemStack(Material.STRING), new ItemStack(Material.STICK),
                new ItemStack(Material.STICK), new ItemStack(Material.STICK), new ItemStack(Material.STICK),
        }, new SlimefunItemStack(ItemStacks.BASIC_FRAME, 4)) {
            @Override
            public double getProductionModifier() {
                return 1.3;
            }
        }.register(plugin);

        // TODO: 01.07.21 What would be a good material to use? (slightly expensive)
        new HiveFrame(Categories.GENERAL, ItemStacks.ADVANCED_FRAME, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                ItemStacks.BASIC_FRAME, new ItemStack(Material.BONE_MEAL), null,
                null, null, null,
                null, null, null,
        }) {
            @Override
            public double getProductionModifier() {
                return 2;
            }
        }.register(plugin);
        // </editor-fold>

        // <editor-fold desc="Machines" defaultstate="collapsed">
        new SlimefunItem(Categories.GENERAL, ItemStacks.HIVE_CASING_PLANK, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                ItemStacks.HONEY_DROP, ItemStacks.ROYAL_JELLY, ItemStacks.HONEY_DROP,
                ItemStacks.POLLEN, new ItemStack(Material.STICK), ItemStacks.POLLEN,
                ItemStacks.HONEY_DROP, ItemStacks.ROYAL_JELLY, ItemStacks.HONEY_DROP
        }).register(plugin);

        new UnplaceableBlock(Categories.GENERAL, ItemStacks.HIVE_CASING, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                ItemStacks.HIVE_CASING_PLANK, ItemStacks.HIVE_CASING_PLANK, null,
                ItemStacks.HIVE_CASING_PLANK, ItemStacks.HIVE_CASING_PLANK, null,
                null, null, null
        }).register(plugin);

        new BeeHive(Categories.GENERAL, ItemStacks.BEE_HIVE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS),
                new ItemStack(Material.DANDELION), ItemStacks.BEESWAX, new ItemStack(Material.POPPY),
                new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.OAK_PLANKS),
        }, false).register(plugin);

        // TODO: 06.07.21 More expensive?
        new BeeHive(Categories.GENERAL, ItemStacks.AUTO_BEE_HIVE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                new ItemStack(Material.STRIPPED_OAK_LOG), SlimefunItems.CARGO_MOTOR, new ItemStack(Material.STRIPPED_OAK_LOG),
                ItemStacks.BEESWAX, ItemStacks.BEE_HIVE, ItemStacks.BEESWAX,
                new ItemStack(Material.STRIPPED_OAK_LOG), SlimefunItems.BASIC_CIRCUIT_BOARD, new ItemStack(Material.STRIPPED_OAK_LOG),
        }, true).register(plugin);

        // TODO: 06.07.21 More/less expensive?
        new IndustrialBeeHive(Categories.GENERAL, ItemStacks.INDUSTRIAL_BEE_HIVE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                ItemStacks.HIVE_CASING, SlimefunItems.PLASTIC_SHEET, ItemStacks.HIVE_CASING,
                SlimefunItems.PLASTIC_SHEET, ItemStacks.AUTO_BEE_HIVE, SlimefunItems.PLASTIC_SHEET,
                ItemStacks.HIVE_CASING, SlimefunItems.ADVANCED_CIRCUIT_BOARD, ItemStacks.HIVE_CASING,
        }).register(plugin);

        Centrifuge centrifuge = new Centrifuge(Categories.GENERAL, ItemStacks.CENTRIFUGE);
        centrifuge.register(plugin);

        ElectricCentrifuge elCentrifuge = new ElectricCentrifuge(Categories.GENERAL, ItemStacks.ELECTRIC_CENTRIFUGE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                SlimefunItems.PLASTIC_SHEET, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.PLASTIC_SHEET,
                SlimefunItems.NICKEL_INGOT, new ItemStack(Material.IRON_BLOCK), SlimefunItems.COBALT_INGOT,
                SlimefunItems.PLASTIC_SHEET, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.PLASTIC_SHEET });
        elCentrifuge.setProcessingSpeed(1).setCapacity(128).setEnergyConsumption(6).register(plugin);

        ElectricCentrifuge elCentrifuge2 = new ElectricCentrifuge(Categories.GENERAL, ItemStacks.ELECTRIC_CENTRIFUGE_2, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                SlimefunItems.PLASTIC_SHEET, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.PLASTIC_SHEET,
                SlimefunItems.STEEL_PLATE, ItemStacks.ELECTRIC_CENTRIFUGE, SlimefunItems.STEEL_PLATE,
                SlimefunItems.PLASTIC_SHEET, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.PLASTIC_SHEET });
        elCentrifuge2.setProcessingSpeed(4).setCapacity(512).setEnergyConsumption(18).register(plugin);

        for (AbstractRecipe recipe : centrifuge.getCentrifugeRecipes()) {
            elCentrifuge.registerRecipe(recipe.copy());
            elCentrifuge2.registerRecipe(recipe.copy());
        }
        // </editor-fold>

        // <editor-fold desc="Various" defaultstate="collapsed">
        new Beealyzer(Categories.GENERAL, ItemStacks.BEEALYZER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                SlimefunItems.PLASTIC_SHEET, new ItemStack(Material.WHITE_STAINED_GLASS), SlimefunItems.PLASTIC_SHEET,
                SlimefunItems.ELECTRO_MAGNET, ItemStacks.HONEY_DROP, SlimefunItems.ELECTRO_MAGNET,
                SlimefunItems.PLASTIC_SHEET, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.PLASTIC_SHEET
        }).register(plugin);

        // TODO: 04.06.21 Tome of Discovery Sharing
        // </editor-fold>
    }

    public static void registerBeeProduct(SlimefunItemStack itemStack, SlimyBeesPlugin plugin, boolean hide) {
        SlimefunItem item = new SlimefunItem(Categories.GENERAL, itemStack, RecipeTypes.BEE_PRODUCT, ItemStacks.CONSULT_BEE_ATLAS_RECIPE);
        item.register(plugin);

        if (hide) {
            item.setHidden(true);
        }
    }

}
