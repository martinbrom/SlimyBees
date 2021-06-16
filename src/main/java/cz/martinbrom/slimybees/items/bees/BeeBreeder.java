package cz.martinbrom.slimybees.items.bees;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.AbstractMachine;
import cz.martinbrom.slimybees.core.BreedingResultDTO;
import cz.martinbrom.slimybees.core.RemoveOnlyMenuClickHandler;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
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
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        ItemStack firstItem = menu.getItemInSlot(INPUT_SLOTS[0]);
        ItemStack secondItem = menu.getItemInSlot(INPUT_SLOTS[1]);

        if (firstItem == null || secondItem == null) {
            return null;
        }

        BreedingResultDTO dto = SlimyBeesPlugin.getBeeGeneticService().breed(firstItem, secondItem);
        if (dto == null) {
            return null;
        }

        // MachineRecipe still uses seconds instead of ticks and just doubles the amount...
        return new MachineRecipe(dto.getTicks() / 2, new ItemStack[] { firstItem, secondItem }, dto.getOutput());
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

    // TODO: 14.06.21 Bees can be removed mid-crafting and it will still finish
    //  but not consume anything
    @Override
    protected void onCraftFinish(ItemStack[] ingredients) {
        for (ItemStack ingredient : ingredients) {
            if (ingredient != null) {
                ItemUtils.consumeItem(ingredient, false);
            }
        }
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
