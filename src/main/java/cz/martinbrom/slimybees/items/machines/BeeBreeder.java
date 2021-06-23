package cz.martinbrom.slimybees.items.machines;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.machine.AbstractMachine;
import cz.martinbrom.slimybees.core.genetics.BreedingResultDTO;
import cz.martinbrom.slimybees.utils.RemoveOnlyMenuClickHandler;
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
public class BeeBreeder extends AbstractMachine {

    private static final int[] INPUT_SLOTS = { 3, 5 };
    private static final int[] OUTPUT_SLOTS = { 38, 39, 40, 41, 42, 47, 48, 49, 50, 51 };

    private static final int[] INPUT_BORDER_SLOTS = { 2, 4, 6, 11, 12, 13, 14, 15 };
    private static final int[] OUTPUT_BORDER_SLOTS = { 28, 29, 30, 31, 32, 33, 34, 37, 43, 46, 52 };
    private static final int[] BACKGROUND_SLOTS = { 0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 35, 36, 44, 45, 53 };

    private static final ItemStack PROGRESS_ITEM_STACK = new ItemStack(Material.BEE_SPAWN_EGG);

    public BeeBreeder(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nullable
    @Override
    protected GuaranteedRecipe findNextRecipe(BlockMenu menu) {
        ItemStack firstItem = menu.getItemInSlot(INPUT_SLOTS[0]);
        ItemStack secondItem = menu.getItemInSlot(INPUT_SLOTS[1]);

        if (firstItem == null || secondItem == null) {
            return null;
        }

        BreedingResultDTO dto = SlimyBeesPlugin.getBeeGeneticService().breed(firstItem, secondItem);
        if (dto == null) {
            return null;
        }

        // make sure we don't consume multiple drones / princesses
        firstItem = firstItem.clone();
        firstItem.setAmount(1);
        secondItem = secondItem.clone();
        secondItem.setAmount(1);

        GuaranteedRecipe recipe = new GuaranteedRecipe(Arrays.asList(firstItem, secondItem));
        recipe.addOutputs(dto.getOutput());
        recipe.setDuration(dto.getTicks());

        return recipe;
    }

    @Nonnull
    @Override
    protected ItemStack getProgressBar() {
        return PROGRESS_ITEM_STACK;
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

    @Nonnull
    @Override
    protected int[] getInputSlots() {
        return INPUT_SLOTS;
    }

    @Nonnull
    @Override
    protected int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    @Override
    protected boolean onCraftFinish(BlockMenu menu, List<ItemStack> ingredients) {
        Map<Integer, ItemStack> missingItems = menu.toInventory().removeItem(ingredients.toArray(new ItemStack[0]));
        return missingItems.isEmpty();
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        SlimefunItemStack beeStack = new SlimefunItemStack(
                "_RECIPE_BEE",
                SlimyBeesHeadTexture.PRINCESS.getAsItemStack(),
                "&eAny Princess + Drone");
        SlimefunItemStack offspringStack = new SlimefunItemStack(
                "_RECIPE_BEE_OFFSPRING",
                Material.HONEYCOMB,
                "&6Bee Offspring");
        return Arrays.asList(beeStack, offspringStack);
    }

}
