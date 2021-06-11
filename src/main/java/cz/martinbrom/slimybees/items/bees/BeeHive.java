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
import cz.martinbrom.slimybees.core.BreedingResultDTO;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeHive extends AContainer {

    private static final int[] BORDER = { 0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 35, 36, 44, 45, 53 };
    private static final int[] INPUT_BORDER = { 2, 4, 6, 11, 12, 13, 14, 15 };
    private static final int[] OUTPUT_BORDER = { 28, 29, 30, 31, 32, 33, 34, 37, 43, 46, 52 };
    private static final int[] OUTPUT_SLOTS = { 38, 39, 40, 41, 42, 47, 48, 49, 50, 51 };

    private static final int INPUT_SLOT_1 = 3;
    private static final int INPUT_SLOT_2 = 5;

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
        SlimefunItemStack beeStack = new SlimefunItemStack(
                "_RECIPE_BEE",
                SlimyBeesHeadTexture.DRONE.getAsItemStack(),
                "&eBees");
        SlimefunItemStack productStack = new SlimefunItemStack(
                "_RECIPE_BEE_PRODUCT",
                Material.HONEYCOMB,
                "&6Bee Products");
        return Arrays.asList(beeStack, productStack);
    }

    @Override
    public int[] getInputSlots() {
        return new int[] { INPUT_SLOT_1, INPUT_SLOT_2 };
    }

    @Override
    public int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    @Nullable
    @Override
    protected MachineRecipe findNextRecipe(BlockMenu inv) {
        ItemStack firstItem = inv.getItemInSlot(INPUT_SLOT_1);
        ItemStack secondItem = inv.getItemInSlot(INPUT_SLOT_2);

        if (firstItem == null || secondItem == null) {
            return null;
        }

        BreedingResultDTO dto = SlimyBeesPlugin.getBeeGeneticService().breed(firstItem, secondItem);
        if (dto == null) {
            return null;
        }

        // TODO: 11.06.21 Consume items after process is finished
        // MachineRecipe still uses seconds instead of ticks and just doubles the amount...
//        return new MachineRecipe(5, new ItemStack[] { firstItem, secondItem }, dto.getOutput());
        return new MachineRecipe(dto.getTicks() / 2, new ItemStack[] { firstItem, secondItem }, dto.getOutput());
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
