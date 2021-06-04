package cz.martinbrom.slimybees.items.bees;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeHive extends AContainer {

    private static final int[] BORDER = { 0, 1, 2, 3, 4, 13, 31, 36, 37, 38, 39, 40 };
    private static final int[] INPUT_BORDER = { 9, 10, 11, 12, 18, 21, 27, 28, 29, 30 };
    private static final int[] OUTPUT_BORDER = { 5, 6, 7, 8, 14, 17, 23, 26, 32, 35, 41, 42, 43, 44 };

    private static final int INPUT_SLOT_1 = 19;
    private static final int INPUT_SLOT_2 = 20;

    public BeeHive(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        setCapacity(1);
        setEnergyConsumption(1);
        setProcessingSpeed(1);
    }

    @Override
    public ItemStack getProgressBar() {
        // TODO: 19.05.21 Replace with Damageable item
        return new ItemStack(Material.HONEYCOMB);
    }

    @Nonnull
    @Override
    public String getMachineIdentifier() {
        return "BASIC_BEE_HIVE";
    }

    @Override
    protected boolean takeCharge(Location l) {
        // takes no energy (hacky I know)
        return true;
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        // TODO: 19.05.21 Implement
        SlimefunItemStack beeStack = new SlimefunItemStack(
                "_RECIPE_BEE",
                SlimyBeesHeadTexture.BEE.getAsItemStack(),
                "&eBees");
        SlimefunItemStack productStack = new SlimefunItemStack(
                "_RECIPE_BEE_PRODUCT",
                Material.HONEYCOMB,
                "&6Bee Products");
        return Arrays.asList(beeStack, productStack);
    }

    @Nullable
    @Override
    protected MachineRecipe findNextRecipe(BlockMenu inv) {
        ItemStack firstItem = inv.getItemInSlot(INPUT_SLOT_1);
        ItemStack secondItem = inv.getItemInSlot(INPUT_SLOT_2);

        if (firstItem == null || secondItem == null) {
            return null;
        }

        ItemStack[] output = SlimyBeesPlugin.getBeeGeneticService().getChildren(firstItem, secondItem);
        if (output == null) {
            return null;
        }

        // TODO: 30.05.21 Change time to something reasonable
        return new MachineRecipe(5, new ItemStack[] { firstItem, secondItem }, output);
    }

    @Override
    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : BORDER) {
            preset.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : INPUT_BORDER) {
            preset.addItem(i, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : OUTPUT_BORDER) {
            preset.addItem(i, ChestMenuUtils.getOutputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        preset.addItem(22, new CustomItem(Material.BLACK_STAINED_GLASS_PANE, " "), ChestMenuUtils.getEmptyClickHandler());

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new ChestMenu.AdvancedMenuClickHandler() {

                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return cursor.getType() == Material.AIR;
                }
            });
        }
    }


}
