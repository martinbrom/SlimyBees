package cz.martinbrom.slimybees.items.machines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BreedingResultDTO;
import cz.martinbrom.slimybees.core.machine.AbstractTickingContainer;
import cz.martinbrom.slimybees.core.recipe.BeeBreedingOperation;
import cz.martinbrom.slimybees.utils.ArrayUtils;
import cz.martinbrom.slimybees.utils.RemoveOnlyMenuClickHandler;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.InvUtils;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeBreeder extends AbstractTickingContainer implements MachineProcessHolder<BeeBreedingOperation>, RecipeDisplayItem {

    private static final int PRINCESS_SLOT = 3;
    private static final int DRONE_SLOT = 5;
    private static final int[] OUTPUT_SLOTS = { 38, 39, 40, 41, 42, 47, 48, 49, 50, 51 };

    private static final int[] INPUT_BORDER_SLOTS = { 2, 4, 6, 11, 12, 13, 14, 15 };
    private static final int[] OUTPUT_BORDER_SLOTS = { 28, 29, 30, 31, 32, 33, 34, 37, 43, 46, 52 };
    private static final int[] BACKGROUND_SLOTS = { 0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 35, 36, 44, 45, 53 };

    private final MachineProcessor<BeeBreedingOperation> processor = new MachineProcessor<>(this);

    public BeeBreeder(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

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
                    addOutputs(menu, b, operation.getPrincess(), operation.getDrones());
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

        return new BeeBreedingOperation(firstItem, secondItem, dto.getPrincess(), dto.getDrones(), dto.getTicks());
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

    private void addOutputs(BlockMenu menu, Block b, ItemStack princess, ItemStack[] drones) {
        // try to fit princess and one randomly selected drone back into the breeder
        ItemStack leftoverPrincess = menu.pushItem(princess, PRINCESS_SLOT);

        ArrayUtils.shuffle(drones);
        int droneFitIndex = -1;
        for (int i = 0; i < drones.length; i++) {
            ItemStack leftoverDrone = menu.pushItem(drones[i], DRONE_SLOT);
            if (leftoverDrone == null) {
                droneFitIndex = i;
                break;
            }
        }

        // if both fit and no more drones to insert
        if (leftoverPrincess == null && drones.length == 1 && droneFitIndex != -1) {
            return;
        }

        // try to fit everything into the inventory or the chest
        // on the top of the bee breeder (if there is any)
        Inventory chest = findTopChest(b);
        ItemStack[] items = ArrayUtils.concat(drones, princess);
        List<ItemStack> leftoverItems = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            if (i != droneFitIndex) {
                leftoverItems.add(addOutput(menu, chest, items[i]));
            }
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
    private ItemStack addOutput(BlockMenu menu, @Nullable Inventory chest, ItemStack item) {
        // check the chest first and then the breeder inventory
        if (chest != null && InvUtils.fits(chest, item)) {
            HashMap<Integer, ItemStack> leftoverItemMap = chest.addItem(item);
            return leftoverItemMap.isEmpty() ? null : leftoverItemMap.get(0);
        }

        return menu.pushItem(item, getOutputSlots());
    }

    @Nullable
    private Inventory findTopChest(Block b) {
        Block potentialChest = b.getRelative(BlockFace.UP);
        if (potentialChest.getType() == Material.CHEST) {
            BlockState state = PaperLib.getBlockState(potentialChest, false).getState();

            // TODO: 03.07.21 Other inventories? (like barrels, shulkers...)
            if (state instanceof Chest) {
                return ((Chest) state).getInventory();
            }
        }

        return null;
    }

}
