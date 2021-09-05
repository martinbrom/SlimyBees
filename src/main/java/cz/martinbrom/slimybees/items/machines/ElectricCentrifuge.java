package cz.martinbrom.slimybees.items.machines;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.core.machine.AbstractElectricMachine;
import cz.martinbrom.slimybees.utils.MenuUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;

@ParametersAreNonnullByDefault
public class ElectricCentrifuge extends AbstractElectricMachine {

    private static final int[] INPUT_SLOTS = { 19, 20 };
    private static final int[] OUTPUT_SLOTS = { 24, 25 };

    private static final int[] INPUT_BORDER_SLOTS = { 9, 10, 11, 12, 18, 21, 27, 28, 29, 30 };
    private static final int[] OUTPUT_BORDER_SLOTS = { 14, 15, 16, 17, 23, 26, 32, 33, 34, 35 };
    private static final int[] BACKGROUND_SLOTS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 13, 31, 36, 37, 38, 39, 40, 41, 42, 43, 44 };

    private static final ItemStack PROGRESS_ITEM_STACK = new ItemStack(Material.GRINDSTONE);

    public ElectricCentrifuge(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    protected ItemStack getProgressBar() {
        return PROGRESS_ITEM_STACK;
    }

    @Override
    protected void setupMenu(BlockMenuPreset preset) {
        MenuUtils.draw(preset, BACKGROUND_SLOTS, INPUT_BORDER_SLOTS, OUTPUT_BORDER_SLOTS);

        preset.addItem(22, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "), ChestMenuUtils.getEmptyClickHandler());

        for (int slot : getOutputSlots()) {
            preset.addMenuClickHandler(slot, MenuUtils.getRemoveOnlyClickHandler());
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

        CustomItemStack anyComb = new CustomItemStack(Material.HONEYCOMB, ChatColor.YELLOW + "Any comb");
        displayRecipes.add(anyComb);
        displayRecipes.add(ItemStacks.HONEY_DROP);

        displayRecipes.add(anyComb);
        displayRecipes.add(ItemStacks.BEESWAX);

        displayRecipes.add(anyComb);
        displayRecipes.add(new CustomItemStack(Material.DIAMOND,
                ChatColor.YELLOW + "Bee product",
                ChatColor.YELLOW + "Consult the Bee Atlas or the addon wiki",
                ChatColor.YELLOW + "for more information"));

        return displayRecipes;
    }

}
