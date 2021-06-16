package cz.martinbrom.slimybees.items.machines;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.core.AbstractElectricMachine;
import cz.martinbrom.slimybees.core.RemoveOnlyMenuClickHandler;
import cz.martinbrom.slimybees.core.recipe.AbstractRecipe;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class ElectricCentrifuge extends AbstractElectricMachine {

    private static final int[] INPUT_SLOTS = { 19, 20 };
    private static final int[] OUTPUT_SLOTS = { 24, 25 };

    private static final int[] INPUT_BORDER_SLOTS = { 9, 10, 11, 12, 18, 21, 27, 28, 29, 30 };
    private static final int[] OUTPUT_BORDER_SLOTS = { 14, 15, 16, 17, 23, 26, 32, 33, 34, 35 };
    private static final int[] BACKGROUND_SLOTS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 13, 31, 36, 37, 38, 39, 40, 41, 42, 43, 44 };

    private static final ItemStack PROGRESS_ITEM_STACK = new ItemStack(Material.GRINDSTONE);

    private final List<AbstractRecipe> recipes = new ArrayList<>();

    public ElectricCentrifuge(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    public void registerRecipe(AbstractRecipe recipe) {
        Validate.notNull(recipe, "Cannot register a null recipe!");

        recipes.add(recipe);
    }

    @Nullable
    @Override
    protected AbstractRecipe findNextRecipe(BlockMenu menu) {
        return null;
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

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();

        CustomItem anyComb = new CustomItem(Material.HONEYCOMB, ChatColor.YELLOW + "Any comb");
        displayRecipes.add(anyComb);
        displayRecipes.add(ItemStacks.HONEY_DROP);

        displayRecipes.add(anyComb);
        displayRecipes.add(new CustomItem(Material.DIAMOND,
                ChatColor.YELLOW + "Bee product",
                ChatColor.YELLOW + "Consult the Bee Atlas or the addon wiki",
                ChatColor.YELLOW + "for more information"));

        return displayRecipes;
    }

}
