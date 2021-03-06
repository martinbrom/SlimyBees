package cz.martinbrom.slimybees;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineTier;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;

/**
 * This class holds a static reference to every {@link SlimefunItemStack} found in SlimyBees.
 */
public class ItemStacks {

    // prevent instantiation
    private ItemStacks() {}

    // <editor-fold desc="Bee Products" defaultstate="collapsed">
    public static final SlimefunItemStack BEESWAX = new SlimefunItemStack(
            "BEESWAX",
            Material.GLOWSTONE_DUST,
            "&eBeeswax");
    public static final SlimefunItemStack HONEY_DROP = new SlimefunItemStack(
            "HONEY_DROP",
            Material.GOLD_NUGGET,
            "&6Honey Drop");

    public static final SlimefunItemStack HONEY_COMB = createHoneycomb("HONEY", "&6Honey");
    public static final SlimefunItemStack DRY_COMB = createHoneycomb("DRY", "&eDry");
    public static final SlimefunItemStack SWEET_COMB = createHoneycomb("SWEET", "&fSweet");
    // </editor-fold>

    // <editor-fold desc="Specialty Products" defaultstate="collapsed">
    // TODO: 03.06.21 Prevent coloring
    public static final SlimefunItemStack ROYAL_JELLY = new SlimefunItemStack(
            "ROYAL_JELLY",
            Material.LIGHT_GRAY_DYE,
            "&6&lRoyal Jelly",
            meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
    // TODO: 03.06.21 Prevent placing
    public static final SlimefunItemStack POLLEN = new SlimefunItemStack(
            "POLLEN",
            Material.PUMPKIN_SEEDS,
            "&6&lPollen",
            meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));

    static {
        ROYAL_JELLY.addUnsafeEnchantment(Enchantment.MENDING, 1);
        POLLEN.addUnsafeEnchantment(Enchantment.MENDING, 1);
    }
    // </editor-fold>

    // <editor-fold desc="Frames" defaultstate="collapsed">
    public static final SlimefunItemStack BASIC_FRAME = new SlimefunItemStack(
            "BASIC_FRAME",
            Material.BIRCH_SIGN,
            "&fBasic Frame",
            "",
            loreProductionModifier(1.25));

    // TODO: 01.07.21 Better name
    public static final SlimefunItemStack ADVANCED_FRAME = new SlimefunItemStack(
            "ADVANCED_FRAME",
            Material.BIRCH_SIGN,
            "&fAdvanced Frame",
            "",
            loreProductionModifier(2));

    public static final SlimefunItemStack SWEET_FRAME = new SlimefunItemStack(
            "SWEET_FRAME",
            Material.DARK_OAK_SIGN,
            "&4Sweet Frame",
            "",
            loreLifespanModifier(0.5));

    public static final SlimefunItemStack DEADLY_FRAME = new SlimefunItemStack(
            "DEADLY_FRAME",
            Material.DARK_OAK_SIGN,
            "&4Deadly Frame",
            "",
            loreLifespanModifier(0.1));
    // </editor-fold>

    // <editor-fold desc="Machines" defaultstate="collapsed">
    public static final SlimefunItemStack HIVE_CASING_PLANK = new SlimefunItemStack(
            "HIVE_CASING_PLANK",
            Material.SPRUCE_SLAB,
            "&6Hive Casing Plank",
            "",
            "&fCan be combined into a Hive Casing block");

    public static final SlimefunItemStack HIVE_CASING = new SlimefunItemStack(
            "HIVE_CASING",
            Material.SPRUCE_PLANKS,
            "&6Hive Casing",
            "",
            "&fAn essential part of any",
            "&findustrial-grade bee hive");

    public static final SlimefunItemStack BEE_HIVE = new SlimefunItemStack(
            "BEE_HIVE",
            Material.OAK_PLANKS,
            "&6Bee Hive",
            "",
            "&fA simple home for your bees",
            "&fManual labor required!",
            "",
            getTieredHiveString(MachineTier.BASIC));

    public static final SlimefunItemStack AUTO_BEE_HIVE = new SlimefunItemStack(
            "AUTO_BEE_HIVE",
            Material.STRIPPED_OAK_LOG,
            "&6Automatic Bee Hive",
            "",
            "&fAn automatic home for your bees",
            "&fThe bred princesses and drones are",
            "&fautomatically moved into the input",
            "&fto continue breeding indefinitely",
            "",
            getTieredHiveString(MachineTier.GOOD));

    public static final SlimefunItemStack INDUSTRIAL_BEE_HIVE = new SlimefunItemStack(
            "INDUSTRIAL_BEE_HIVE",
            Material.STRIPPED_CRIMSON_STEM,
            "&6&lIndustrial Bee Hive",
            "",
            "&fA top of the line home for your bees",
            "&fFrames can be used to drastically improve",
            "&fthe performance of your buzzy workers",
            "",
            getTieredHiveString(MachineTier.END_GAME));

    @Nonnull
    private static String getTieredHiveString(MachineTier basic) {
        return basic + " Hive";
    }

    public static final String EXTRACTS_MATERIALS_FROM_COMBS = "&fExtracts materials from combs";
    public static final SlimefunItemStack CENTRIFUGE = new SlimefunItemStack(
            "CENTRIFUGE",
            Material.GRINDSTONE,
            "&7Centrifuge",
            "",
            EXTRACTS_MATERIALS_FROM_COMBS);

    public static final SlimefunItemStack ELECTRIC_CENTRIFUGE = new SlimefunItemStack(
            "ELECTRIC_CENTRIFUGE",
            Material.IRON_BLOCK,
            "&7Electric Centrifuge",
            "",
            EXTRACTS_MATERIALS_FROM_COMBS,
            "",
            LoreBuilder.machine(MachineTier.BASIC, MachineType.MACHINE),
            LoreBuilder.speed(1),
            LoreBuilder.powerPerSecond(12));

    public static final SlimefunItemStack ELECTRIC_CENTRIFUGE_2 = new SlimefunItemStack(
            "ELECTRIC_CENTRIFUGE_2",
            Material.IRON_BLOCK,
            "&7Electric Centrifuge (&eII&7)",
            "",
            EXTRACTS_MATERIALS_FROM_COMBS,
            "",
            LoreBuilder.machine(MachineTier.ADVANCED, MachineType.MACHINE),
            LoreBuilder.speed(4),
            LoreBuilder.powerPerSecond(36));
    // </editor-fold>

    // <editor-fold desc="Various" defaultstate="collapsed">
    public static final SlimefunItemStack BEEALYZER = new SlimefunItemStack(
            "BEEALYZER",
            Material.ITEM_FRAME,
            "&7Beealyzer",
            "",
            LoreBuilder.powerCharged(0, 50),
            "",
            "&fAn apiarist's most trusty tool",
            "&fThis item is used to identify various",
            "&ftraits in your bees",
            "",
            LoreBuilder.RIGHT_CLICK_TO_USE);

    public static final SlimefunItemStack TOME_OF_DISCOVERY_SHARING = new SlimefunItemStack(
            "TOME_OF_DISCOVERY_SHARING",
            Material.ENCHANTED_BOOK,
            "&6Tome of Discovery Sharing",
            loreOwner("None"),
            "",
            "&eRight Click&7 to bind this Tome to yourself",
            "",
            "&eRight Click&7 to obtain all Discoveries by",
            "&7the previously assigned Owner");

    public static final CustomItemStack CONSULT_BEE_ATLAS_RECIPE_ITEM = new CustomItemStack(
            Material.ENCHANTED_BOOK,
            ChatColor.WHITE + "Bee Atlas",
            "",
            ChatColor.GRAY + "Consult the Bee Atlas or the addon wiki",
            ChatColor.GRAY + "for more information");

    public static final ItemStack[] CONSULT_BEE_ATLAS_RECIPE = new ItemStack[] {
            null, null, null,
            null, ItemStacks.CONSULT_BEE_ATLAS_RECIPE_ITEM, null,
            null, null, null };

    public static final CustomItemStack CENTRIFUGE_COMB_RECIPE_ITEM = new CustomItemStack(
            Material.HONEYCOMB,
            ChatColor.YELLOW + "Any Comb",
            "",
            ChatColor.GRAY + "Put any comb into a centrifuge");

    public static final ItemStack[] CENTRIFUGE_COMB_RECIPE = new ItemStack[] {
            null, null, null,
            null, ItemStacks.CENTRIFUGE_COMB_RECIPE_ITEM, null,
            null, null, null };

    public static final SlimefunItemStack BEE_BREEDING_STACK = new SlimefunItemStack(
            "_RECIPE_BEE",
            SlimyBeesHeadTexture.PRINCESS.getAsItemStack(),
            "&eAny Princess + Drone");
    public static final SlimefunItemStack BEE_OFFSPRING_STACK = new SlimefunItemStack(
            "_RECIPE_BEE_OFFSPRING",
            Material.HONEYCOMB,
            "&6Bee Offspring");
    public static final SlimefunItemStack BEE_PRODUCT_STACK = new SlimefunItemStack(
            "_RECIPE_BEE_PRODUCT",
            Material.HONEYCOMB,
            "&6Bee Products");
    // </editor-fold>

    public static SlimefunItemStack createDrone(String id, String name, boolean enchanted, String... lore) {
        return createBee(
                id + "_DRONE",
                SlimyBeesHeadTexture.DRONE.getAsItemStack(),
                name + " Drone",
                enchanted,
                lore);
    }

    public static SlimefunItemStack createPrincess(String id, String name, boolean enchanted, String... lore) {
        return createBee(id + "_PRINCESS",
                SlimyBeesHeadTexture.PRINCESS.getAsItemStack(),
                name + " Princess",
                enchanted,
                lore);
    }

    private static SlimefunItemStack createBee(String id, ItemStack itemStack, String name, boolean enchanted, String... lore) {
        SlimefunItemStack item = new SlimefunItemStack(id, itemStack, name, lore);

        if (enchanted) {
            item.addUnsafeEnchantment(Enchantment.MENDING, 1);

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
        }

        return item;
    }

    public static SlimefunItemStack createHoneycomb(String id, String name) {
        return new SlimefunItemStack(
                id + "_COMB",
                Material.HONEYCOMB,
                name + " Comb");
    }

    public static String loreProductionModifier(double ratio) {
        return "&7Production: &8&lx" + String.format("%.1f", ratio);
    }

    public static String loreLifespanModifier(double ratio) {
        return "&7Lifespan: &8&lx" + String.format("%.1f", ratio);
    }

    public static String loreOwner(String owner) {
        return "&7Owner: &b" + owner;
    }

}
