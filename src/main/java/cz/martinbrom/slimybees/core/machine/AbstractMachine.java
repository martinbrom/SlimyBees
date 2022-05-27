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

import cz.martinbrom.slimybees.core.recipe.GuaranteedRecipe;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

// TODO: 16.06.21 Document that this is copied from the AContainer class
@ParametersAreNonnullByDefault
public abstract class AbstractMachine extends AbstractTickingContainer implements MachineProcessHolder<CustomCraftingOperation>, RecipeDisplayItem {

    private final MachineProcessor<CustomCraftingOperation> processor = new MachineProcessor<>(this);

    protected AbstractMachine(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
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

    protected void addOutputs(BlockMenu menu, Block b, List<ItemStack> outputs) {
        for (ItemStack output : outputs) {
            if (output != null && !output.getType().isAir()) {
                menu.pushItem(output.clone(), getOutputSlots());
            }
        }
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
    protected void tick(BlockMenu menu, Block b) {
        CustomCraftingOperation currentOperation = processor.getOperation(b);

        if (currentOperation != null) {
            if (checkCraftPreconditions(b)) {
                if (!currentOperation.isFinished()) {
                    processor.updateProgressBar(menu, 22, currentOperation);
                    currentOperation.addProgress(1);
                } else {
                    menu.replaceExistingItem(22, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "));

                    boolean finishedSuccessfully = onCraftFinish(menu, currentOperation.getIngredients());
                    // if the user removed something during the crafting process, no output will be added
                    if (finishedSuccessfully) {
                        addOutputs(menu, b, currentOperation.getOutputs());
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
