package cz.martinbrom.slimybees.core.machine;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.recipe.CustomCraftingOperation;
import cz.martinbrom.slimybees.core.recipe.GuaranteedRecipe;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

// TODO: 16.06.21 Document that this is copied from the AContainer class
@ParametersAreNonnullByDefault
public abstract class AbstractMachine extends AbstractTickingContainer implements MachineProcessHolder<CustomCraftingOperation>, RecipeDisplayItem {

    private final MachineProcessor<CustomCraftingOperation> processor = new MachineProcessor<>(this);

    public AbstractMachine(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        processor.setProgressBar(getProgressBar());
    }

    @Nonnull
    @Override
    public MachineProcessor<CustomCraftingOperation> getMachineProcessor() {
        return processor;
    }

    @Nullable
    protected abstract GuaranteedRecipe findNextRecipe(BlockMenu menu);

    @Nonnull
    protected abstract ItemStack getProgressBar();

    protected boolean checkCraftPreconditions(Block b) {
        // default impl does nothing
        return true;
    }

    protected boolean onCraftFinish(BlockMenu menu, List<ItemStack> ingredients) {
        // default impl does nothing
        return true;
    }

    @Override
    protected void onBreak(BlockBreakEvent e, BlockMenu menu, Location l) {
        super.onBreak(e, menu, l);

        menu.dropItems(l, getInputSlots());
        menu.dropItems(l, getOutputSlots());

        processor.endOperation(e.getBlock());
    }

    @Nonnull
    @Override
    protected abstract int[] getInputSlots();

    @Nonnull
    @Override
    protected abstract int[] getOutputSlots();

    @Override
    protected void tick(BlockMenu menu, Block b, Config data) {
        CustomCraftingOperation currentOperation = processor.getOperation(b);

        if (currentOperation != null) {
            if (checkCraftPreconditions(b)) {
                if (!currentOperation.isFinished()) {
                    processor.updateProgressBar(menu, 22, currentOperation);
                    currentOperation.addProgress(1);
                } else {
                    menu.replaceExistingItem(22, new CustomItem(Material.BLACK_STAINED_GLASS_PANE, " "));

                    boolean consumedAll = onCraftFinish(menu, currentOperation.getIngredients());
                    // if the user removed something during the crafting process, no output will be added
                    if (consumedAll) {
                        for (ItemStack output : currentOperation.getOutputs()) {
                            if (output != null && !output.getType().isAir()) {
                                menu.pushItem(output.clone(), getOutputSlots());
                            }
                        }
                    }

                    processor.endOperation(b);
                }
            }
        } else {
            GuaranteedRecipe next = findNextRecipe(menu);

            if (next != null) {
                processor.startOperation(b, new CustomCraftingOperation(next));
            }
        }
    }

}
