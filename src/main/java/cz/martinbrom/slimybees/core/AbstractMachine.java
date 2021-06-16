package cz.martinbrom.slimybees.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public abstract class AbstractMachine extends AbstractTickingContainer implements MachineProcessHolder<CraftingOperation>, RecipeDisplayItem {

    private final MachineProcessor<CraftingOperation> processor = new MachineProcessor<>(this);

    public AbstractMachine(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        processor.setProgressBar(getProgressBar());
    }

    @Nonnull
    @Override
    public MachineProcessor<CraftingOperation> getMachineProcessor() {
        return processor;
    }

    @Nullable
    protected abstract MachineRecipe findNextRecipe(BlockMenu menu);

    @Nonnull
    protected abstract ItemStack getProgressBar();

    protected void onCraftFinish(ItemStack[] ingredients) {
        // default impl does nothing
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
        CraftingOperation currentOperation = processor.getOperation(b);

        if (currentOperation != null) {
            if (!currentOperation.isFinished()) {
                processor.updateProgressBar(menu, 22, currentOperation);
                currentOperation.addProgress(1);
            } else {
                menu.replaceExistingItem(22, new CustomItem(Material.BLACK_STAINED_GLASS_PANE, " "));

                for (ItemStack output : currentOperation.getResults()) {
                    if (output != null && !output.getType().isAir()) {
                        menu.pushItem(output.clone(), getOutputSlots());
                    }
                }

                onCraftFinish(currentOperation.getIngredients());
                processor.endOperation(b);
            }
        } else {
            MachineRecipe next = findNextRecipe(menu);

            if (next != null) {
                processor.startOperation(b, new CraftingOperation(next));
            }
        }
    }

}
