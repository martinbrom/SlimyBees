package cz.martinbrom.slimybees.utils;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;

/**
 * This class contains useful function(s) for working with BlockMenus.
 */
@ParametersAreNonnullByDefault
public class MenuUtils {

    /**
     * Draws the background and input & output borders.
     *
     * @param preset The {@link BlockMenuPreset} to draw onto
     * @param backgroundSlots The background slot numbers
     * @param inputBorderSlots The input border slot numbers
     * @param outputBorderSlots The output border slot numbers
     */
    public static void draw(BlockMenuPreset preset, int[] backgroundSlots, int[] inputBorderSlots, int[] outputBorderSlots) {
        Validate.notNull(preset, "Cannot draw onto a null BlockMenuPreset!");

        for (int slot : backgroundSlots) {
            preset.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int slot : inputBorderSlots) {
            preset.addItem(slot, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int slot : outputBorderSlots) {
            preset.addItem(slot, ChestMenuUtils.getOutputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }
    }

}
