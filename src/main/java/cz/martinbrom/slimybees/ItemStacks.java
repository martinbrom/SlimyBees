package cz.martinbrom.slimybees;

import org.bukkit.Material;

import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This class holds a static reference to every {@link SlimefunItemStack} found in SlimyBees.
 */
public class ItemStacks {

    // prevent instantiation
    private ItemStacks() {
    }

    public static final SlimefunItemStack COMMON_HONEYCOMB = createHoneycomb("COMMON", "&6Common");
    public static final SlimefunItemStack BEESWAX = new SlimefunItemStack(
            "BEESWAX",
            Material.GLOWSTONE_DUST,
            "&eBeeswax");
    public static final SlimefunItemStack HONEY_DROP = new SlimefunItemStack(
            "HONEY_DROP",
            Material.GOLD_NUGGET,
            "&6Honey Drop");
    public static final SlimefunItemStack ROYAL_JELLY = new SlimefunItemStack(
            "ROYAL_JELLY",
            Material.LIGHT_GRAY_DYE,
            "&6&lRoyal Jelly");

    // machines
    public static final SlimefunItemStack BEE_HIVE = new SlimefunItemStack(
            "BEE_HIVE",
            Material.BEEHIVE,
            "&6Bee Hive",
            "",
            "&fA simple home for your bees"
    );

    // various

    public static final SlimefunItemStack BEE_NET = new SlimefunItemStack(
            "BEE_NET",
            Material.COBWEB,
            "&fBee Net",
            "",
            "&fThis item can be used to collect",
            "&fbees in the wilderness",
            "",
            LoreBuilder.RIGHT_CLICK_TO_USE
    );

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
            LoreBuilder.RIGHT_CLICK_TO_USE
    );

    public static final SlimefunItemStack BEE_ATLAS = new SlimefunItemStack(
            "BEE_ATLAS",
            Material.WRITABLE_BOOK,
            "&6Bee Atlas",
            "",
            "&fThis book contains every bee known to man",
            "&fUse it to track your discoveries",
            "",
            LoreBuilder.RIGHT_CLICK_TO_USE
    );

    public static SlimefunItemStack createBee(String id, String name, String... lore) {
        return new SlimefunItemStack(
                id + "_BEE",
                SlimyBeesHeadTexture.BEE.getAsItemStack(),
                name + " Bee",
                lore);
    }

    public static SlimefunItemStack createHoneycomb(String id, String name) {
        return new SlimefunItemStack(
                id + "_HONEYCOMB",
                Material.HONEYCOMB,
                name + " Honeycomb");
    }

}
