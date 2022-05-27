package cz.martinbrom.slimybees.items.machines;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.genetics.BreedingModifierDTO;
import cz.martinbrom.slimybees.utils.MenuUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;

@ParametersAreNonnullByDefault
public class IndustrialBeeHive extends BeeHive {

    private static final int PRINCESS_SLOT = 1;
    private static final int DRONE_SLOT = 3;
    private static final int[] FRAME_SLOTS = { 6, 7, 8 };

    private static final int[] INPUT_BORDER_SLOTS = { 0, 2, 4, 9, 10, 11, 12, 13 };
    private static final int[] FRAME_BORDER_SLOTS = { 5, 14, 15, 16, 17 };
    protected static final int[] BACKGROUND_SLOTS = { 9, 10, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 35, 36, 44, 45, 53 };

    private static final ItemStack FRAME_BORDER = new CustomItemStack(Material.LIME_STAINED_GLASS_PANE, " ");

    public IndustrialBeeHive(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe, true);
    }

    @Override
    protected int getPrincessSlot() {
        return PRINCESS_SLOT;
    }

    @Override
    protected int getDroneSlot() {
        return DRONE_SLOT;
    }

    @Nonnull
    @Override
    protected int[] getInputSlots() {
        return FRAME_SLOTS;
    }

    @Nonnull
    @Override
    protected BreedingModifierDTO getBreedingModifier(BlockMenu menu) {
        BreedingModifierDTO modifier = BreedingModifierDTO.DEFAULT;
        for (int slot : FRAME_SLOTS) {
            ItemStack item = menu.getItemInSlot(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }

            SlimefunItem sfItem = SlimefunItem.getByItem(item);
            if (sfItem instanceof HiveFrame frame) {
                modifier = modifier.combine(new BreedingModifierDTO(frame.getProductionModifier(), frame.getLifespanModifier()));
                ItemUtils.consumeItem(item, false);
            }
        }

        return modifier;
    }

    @Override
    protected void onBreak(BlockBreakEvent e, BlockMenu menu, Location l) {
        super.onBreak(e, menu, l);

        menu.dropItems(l, FRAME_SLOTS);
    }

    @Override
    protected void setupMenu(BlockMenuPreset preset) {
        MenuUtils.draw(preset, BACKGROUND_SLOTS, INPUT_BORDER_SLOTS, OUTPUT_BORDER_SLOTS);

        preset.addItem(STATUS_SLOT, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "), ChestMenuUtils.getEmptyClickHandler());

        for (int slot : FRAME_BORDER_SLOTS) {
            preset.addItem(slot, FRAME_BORDER, ChestMenuUtils.getEmptyClickHandler());
        }

        for (int slot : getOutputSlots()) {
            preset.addMenuClickHandler(slot, MenuUtils.getRemoveOnlyClickHandler());
        }
    }

}
