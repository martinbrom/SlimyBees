package cz.martinbrom.slimybees.items.machines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BreedingResultDTO;
import cz.martinbrom.slimybees.core.machine.AbstractTickingContainer;
import cz.martinbrom.slimybees.core.recipe.BeeBreedingOperation;
import cz.martinbrom.slimybees.utils.ArrayUtils;
import cz.martinbrom.slimybees.utils.MenuUtils;
import cz.martinbrom.slimybees.utils.RemoveOnlyMenuClickHandler;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeHive extends AbstractTickingContainer implements MachineProcessHolder<BeeBreedingOperation>, RecipeDisplayItem {

    private static final int PRINCESS_SLOT = 3;
    private static final int DRONE_SLOT = 5;
    private static final int[] OUTPUT_SLOTS = { 38, 39, 40, 41, 42, 47, 48, 49, 50, 51 };

    private static final int[] INPUT_BORDER_SLOTS = { 2, 4, 6, 11, 12, 13, 14, 15 };
    private static final int[] OUTPUT_BORDER_SLOTS = { 28, 29, 30, 31, 32, 33, 34, 37, 43, 46, 52 };
    private static final int[] BACKGROUND_SLOTS = { 0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 35, 36, 44, 45, 53 };

    private final List<ItemStack> displayRecipes;
    private final MachineProcessor<BeeBreedingOperation> processor = new MachineProcessor<>(this);

    private final boolean autoFill;
    private final boolean useFrames;

    public BeeHive(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, boolean autoFill, boolean useFrames) {
        super(category, item, recipeType, recipe);

        this.autoFill = autoFill;
        this.useFrames = useFrames;

        displayRecipes = Arrays.asList(ItemStacks.BEE_BREEDING_STACK, ItemStacks.BEE_OFFSPRING_STACK,
                ItemStacks.BEE_BREEDING_STACK, ItemStacks.BEE_PRODUCT_STACK);

        processor.setProgressBar(new ItemStack(Material.BEE_SPAWN_EGG));
    }

    @Nonnull
    @Override
    public MachineProcessor<BeeBreedingOperation> getMachineProcessor() {
        return processor;
    }

    @Override
    protected void tick(BlockMenu menu, Block b, Config data) {
        BeeBreedingOperation operation = processor.getOperation(b);

        if (operation != null) {
            if (!operation.isFinished()) {
                processor.updateProgressBar(menu, 22, operation);
                operation.addProgress(1);
            } else {
                menu.replaceExistingItem(22, new CustomItem(Material.BLACK_STAINED_GLASS_PANE, " "));

                // if the user removed something during the crafting process, no output will be added
                Map<Integer, ItemStack> missingItems = menu.toInventory().removeItem(operation.getParents());
                if (missingItems.isEmpty()) {
                    addOutputs(menu, b, operation.getPrincess(), operation.getDrones(), operation.getProducts());
                }

                processor.endOperation(b);
            }
        } else {
            BeeBreedingOperation next = findNextOperation(menu);

            if (next != null) {
                processor.startOperation(b, next);
            }
        }

    }

    @Override
    protected void onBreak(BlockBreakEvent e, BlockMenu menu, Location l) {
        super.onBreak(e, menu, l);

        menu.dropItems(l, PRINCESS_SLOT);
        menu.dropItems(l, DRONE_SLOT);
        menu.dropItems(l, getOutputSlots());

        processor.endOperation(e.getBlock());
    }

    @Nullable
    protected BeeBreedingOperation findNextOperation(BlockMenu menu) {
        ItemStack firstItem = menu.getItemInSlot(PRINCESS_SLOT);
        ItemStack secondItem = menu.getItemInSlot(DRONE_SLOT);

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

        return new BeeBreedingOperation(firstItem, secondItem, dto);
    }

    @Override
    protected void setupMenu(BlockMenuPreset preset) {
        MenuUtils.draw(preset, BACKGROUND_SLOTS, INPUT_BORDER_SLOTS, OUTPUT_BORDER_SLOTS);

        preset.addItem(22, new CustomItem(Material.BLACK_STAINED_GLASS_PANE, " "), ChestMenuUtils.getEmptyClickHandler());

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new RemoveOnlyMenuClickHandler());
        }
    }

    @Nonnull
    @Override
    protected int[] getInputSlots() {
        // no input slots - intentional
        return new int[0];
    }

    @Nonnull
    @Override
    protected int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        return displayRecipes;
    }

    private void addOutputs(BlockMenu menu, Block b, ItemStack princess, ItemStack[] drones, List<ItemStack> products) {
        ItemStack leftoverPrincess = princess;
        int droneFitIndex = -1;
        if (autoFill) {
            // try to fit princess and one randomly selected drone back into the breeder
            leftoverPrincess = menu.pushItem(princess, PRINCESS_SLOT);

            ArrayUtils.shuffle(drones);
            for (int i = 0; i < drones.length; i++) {
                ItemStack leftoverDrone = menu.pushItem(drones[i], DRONE_SLOT);
                if (leftoverDrone == null) {
                    droneFitIndex = i;
                    break;
                }
            }

            if (leftoverPrincess == null && drones.length == 1 && droneFitIndex != -1) {
                // if both fit and no more drones to insert
                return;
            }
        }

        // try to fit the leftover princess and drones
        List<ItemStack> leftoverItems = new ArrayList<>();
        if (leftoverPrincess != null) {
            leftoverItems.add(addOutput(menu, leftoverPrincess));
        }

        for (int i = 0; i < drones.length; i++) {
            ItemStack drone = drones[i];
            if (i != droneFitIndex) {
                leftoverItems.add(addOutput(menu, drone));
            }
        }

        // try to fit products (bees are more important so this goes second)
        for (ItemStack product : products) {
            leftoverItems.add(addOutput(menu, product));
        }

        // drop any leftover items on the ground
        World world = b.getWorld();
        Location location = b.getLocation();
        for (ItemStack item : leftoverItems) {
            if (item != null) {
                world.dropItemNaturally(location, item);
            }
        }
    }

    @Nullable
    private ItemStack addOutput(BlockMenu menu, ItemStack item) {
        return menu.pushItem(item.clone(), getOutputSlots());
    }

}
