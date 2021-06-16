package cz.martinbrom.slimybees.items.machines;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.AbstractMachine;
import cz.martinbrom.slimybees.core.ProductionResultDTO;
import cz.martinbrom.slimybees.core.RemoveOnlyMenuClickHandler;
import cz.martinbrom.slimybees.core.recipe.AbstractRecipe;
import cz.martinbrom.slimybees.core.recipe.GuaranteedRecipe;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeHive extends AbstractMachine {

    private static final int[] INPUT_SLOTS = { 4 };
    private static final int[] OUTPUT_SLOTS = { 38, 39, 40, 41, 42, 47, 48, 49, 50, 51 };

    private static final int[] INPUT_BORDER_SLOTS = { 3, 5, 12, 13, 14 };
    private static final int[] OUTPUT_BORDER_SLOTS = { 28, 29, 30, 31, 32, 33, 34, 37, 43, 46, 52 };
    private static final int[] BACKGROUND_SLOTS = { 0, 1, 2, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 35, 36, 44, 45, 53 };

    private static final ItemStack PROGRESS_ITEM_STACK = new ItemStack(Material.HONEYCOMB);

    public BeeHive(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    public ItemStack getProgressBar() {
        return PROGRESS_ITEM_STACK;
    }

    @Nonnull
    @Override
    public int[] getInputSlots() {
        return INPUT_SLOTS;
    }

    @Nonnull
    @Override
    public int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        SlimefunItemStack beeStack = new SlimefunItemStack(
                "_RECIPE_BEE",
                SlimyBeesHeadTexture.PRINCESS.getAsItemStack(),
                "&eAny Princess");
        SlimefunItemStack productStack = new SlimefunItemStack(
                "_RECIPE_BEE_PRODUCT",
                Material.HONEYCOMB,
                "&6Bee Products");
        return Arrays.asList(beeStack, productStack);
    }

    @Override
    protected void setupMenu(BlockMenuPreset preset) {
        for (int i : BACKGROUND_SLOTS) {
            preset.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : INPUT_BORDER_SLOTS) {
            preset.addItem(i, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : OUTPUT_BORDER_SLOTS) {
            preset.addItem(i, ChestMenuUtils.getOutputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        preset.addItem(22, new CustomItem(Material.BLACK_STAINED_GLASS_PANE, " "), ChestMenuUtils.getEmptyClickHandler());

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new RemoveOnlyMenuClickHandler());
        }
    }

    @Nullable
    @Override
    protected AbstractRecipe findNextRecipe(BlockMenu inv) {
        ItemStack item = inv.getItemInSlot(INPUT_SLOTS[0]);

        if (item == null) {
            return null;
        }

        ProductionResultDTO dto = SlimyBeesPlugin.getBeeProductionService().produce(item);
        if (dto == null) {
            return null;
        }

        return new GuaranteedRecipe(item)
                .addOutputs(dto.getProducts())
                .setDuration(dto.getTicks());
    }

}
