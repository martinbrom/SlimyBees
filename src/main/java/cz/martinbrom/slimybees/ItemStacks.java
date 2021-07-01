package cz.martinbrom.slimybees;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineTier;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineType;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

/**
 * This class holds a static reference to every {@link SlimefunItemStack} found in SlimyBees.
 */
public class ItemStacks {

    // prevent instantiation
    private ItemStacks() {}

    // <editor-fold desc="Combs" defaultstate="collapsed">
    public static final SlimefunItemStack HONEY_COMB = createHoneycomb("HONEY", "&6Honey");
    public static final SlimefunItemStack DRY_COMB = createHoneycomb("DRY", "&eDry");
    public static final SlimefunItemStack SWEET_COMB = createHoneycomb("SWEET", "&fSweet");
    // </editor-fold>

    // <editor-fold desc="Specialty Products" defaultstate="collapsed">
    public static final SlimefunItemStack BEESWAX = new SlimefunItemStack(
            "BEESWAX",
            Material.GLOWSTONE_DUST,
            "&eBeeswax");
    public static final SlimefunItemStack HONEY_DROP = new SlimefunItemStack(
            "HONEY_DROP",
            Material.GOLD_NUGGET,
            "&6Honey Drop");
    // TODO: 03.06.21 Enchanted?
    // TODO: 03.06.21 Prevent coloring
    public static final SlimefunItemStack ROYAL_JELLY = new SlimefunItemStack(
            "ROYAL_JELLY",
            Material.LIGHT_GRAY_DYE,
            "&6&lRoyal Jelly");
    // TODO: 03.06.21 Enchanted?
    // TODO: 03.06.21 Prevent placing
    public static final SlimefunItemStack POLLEN = new SlimefunItemStack(
            "POLLEN",
            Material.PUMPKIN_SEEDS,
            "&6&lPollen");

    static {
        ROYAL_JELLY.addUnsafeEnchantment(Enchantment.MENDING, 1);
        ROYAL_JELLY.addFlags(ItemFlag.HIDE_ENCHANTS);

        POLLEN.addUnsafeEnchantment(Enchantment.MENDING, 1);
        POLLEN.addFlags(ItemFlag.HIDE_ENCHANTS);
    }
    // </editor-fold>

    // <editor-fold desc="Machines" defaultstate="collapsed">
    public static final SlimefunItemStack BEE_HIVE = new SlimefunItemStack(
            "BEE_HIVE",
            Material.BEEHIVE,
            "&6Bee Hive",
            "",
            "&fA simple home for your bees to produce",
            "&fresources happily ever after");

    public static final SlimefunItemStack BEE_BREEDER = new SlimefunItemStack(
            "BEE_BREEDER",
            Material.OAK_PLANKS,
            "&6Bee Breeder",
            "",
            "&fCombines genes of a princess",
            "&fand a drone to produce new bees",
            "&fwith a chance for a special mutation");

    public static final SlimefunItemStack CENTRIFUGE = new SlimefunItemStack(
            "CENTRIFUGE",
            Material.GRINDSTONE,
            "&7Centrifuge",
            "",
            "&fExtracts materials from combs");

    public static final SlimefunItemStack ELECTRIC_CENTRIFUGE = new SlimefunItemStack(
            "ELECTRIC_CENTRIFUGE",
            Material.IRON_BLOCK,
            "&7Electric Centrifuge",
            "",
            "&fExtracts materials from combs",
            "",
            LoreBuilder.machine(MachineTier.BASIC, MachineType.MACHINE),
            LoreBuilder.speed(1),
            LoreBuilder.powerPerSecond(12));

    public static final SlimefunItemStack ELECTRIC_CENTRIFUGE_2 = new SlimefunItemStack(
            "ELECTRIC_CENTRIFUGE_2",
            Material.IRON_BLOCK,
            "&7Electric Centrifuge (&eII&7)",
            "",
            "&fExtracts materials from combs",
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

    public static final CustomItem BEE_ATLAS = new CustomItem(
            Material.ENCHANTED_BOOK,
            ChatColor.WHITE + "Bee Atlas",
            "",
            ChatColor.GRAY + "Consult the Bee Atlas or the addon wiki",
            ChatColor.GRAY + "for more information");

    public static final ItemStack[] CONSULT_BEE_ATLAS = new ItemStack[] {
            null, null, null,
            null, ItemStacks.BEE_ATLAS, null,
            null, null, null };
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
            item.addFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return item;
    }

    public static SlimefunItemStack createHoneycomb(String id, String name) {
        return new SlimefunItemStack(
                id + "_COMB",
                Material.HONEYCOMB,
                name + " Comb");
    }

}
