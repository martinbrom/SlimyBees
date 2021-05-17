package cz.martinbrom.slimybees;

import org.bukkit.Material;

import cz.martinbrom.slimybees.items.bees.BeeType;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This class holds a static reference to every {@link SlimefunItemStack} found in SlimyBees.
 */
public class Items {

    // prevent instantiation
    private Items() {
    }

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

    // TODO: 16.05.21 Add superclasses for SlimefunItemStack (BeeStack, BeeNestStack) to make sure
    //  correct instances are passed to populators, hives, etc.

    // bees
    public static final SlimefunItemStack ENDER_BEE = createBasicBee(BeeType.ENDER);
    public static final SlimefunItemStack FOREST_BEE = createBasicBee(BeeType.FOREST);

    // nests
    public static final SlimefunItemStack ENDER_BEE_NEST = createBeeNest(BeeType.ENDER);
    public static final SlimefunItemStack FOREST_BEE_NEST = createBeeNest(BeeType.FOREST);

    // TODO: 16.05.21 Update basic bee lore
    private static SlimefunItemStack createBasicBee(BeeType beeType) {
        return new SlimefunItemStack(
                beeType.getType(),
                SlimyBeesHeadTexture.BEE.getAsItemStack(),
                beeType.getDisplayName(),
                "",
                "&fTest lore"
        );
    }

    private static SlimefunItemStack createBeeNest(BeeType beeType) {
        return new SlimefunItemStack(
                beeType.getType() + "_NEST",
                Material.BEE_NEST,
                beeType.getDisplayName() + " Nest",
                "",
                "&fThis block can be found randomly",
                "&fscattered across the wilderness",
                "",
                "&fIt always contains a few bees",
                "&fand sometimes a few extra items"
        );
    }

}
