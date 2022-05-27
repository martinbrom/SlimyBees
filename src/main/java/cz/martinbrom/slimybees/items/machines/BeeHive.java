package cz.martinbrom.slimybees.items.machines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeProductionService;
import cz.martinbrom.slimybees.core.BlockSearchService;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.BreedingModifierDTO;
import cz.martinbrom.slimybees.core.genetics.BreedingResultDTO;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.machine.AbstractTickingContainer;
import cz.martinbrom.slimybees.core.machine.BeeBreedingOperation;
import cz.martinbrom.slimybees.core.machine.WaitingOperation;
import cz.martinbrom.slimybees.items.bees.Drone;
import cz.martinbrom.slimybees.items.bees.Princess;
import cz.martinbrom.slimybees.utils.ArrayUtils;
import cz.martinbrom.slimybees.utils.MenuUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;

@ParametersAreNonnullByDefault
public class BeeHive extends AbstractTickingContainer implements MachineProcessHolder<BeeBreedingOperation>, RecipeDisplayItem {

    // TODO: 11.07.21 ItemSetting for this?
    public static final int BREEDING_WAIT_TICKS = 10;
    public static final int MISSING_PLANT_WAIT_TICKS = 20;
    public static final int EFFECT_TICKS = 20;

    private static final int PRINCESS_SLOT = 3;
    private static final int DRONE_SLOT = 5;
    protected static final int STATUS_SLOT = 22;
    protected static final int[] OUTPUT_SLOTS = { 38, 39, 40, 41, 42, 47, 48, 49, 50, 51 };

    private static final int[] INPUT_BORDER_SLOTS = { 2, 4, 6, 11, 12, 13, 14, 15 };
    protected static final int[] OUTPUT_BORDER_SLOTS = { 28, 29, 30, 31, 32, 33, 34, 37, 43, 46, 52 };
    protected static final int[] BACKGROUND_SLOTS = { 0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 35, 36, 44, 45, 53 };

    private final BlockSearchService blockSearchService;
    private final BeeProductionService productionService;
    private final BeeGeneticService geneticService;

    private final List<ItemStack> displayRecipes;
    private final MachineProcessor<BeeBreedingOperation> processor = new MachineProcessor<>(this);
    private final Map<BlockPosition, WaitingOperation> waitingHives = new ConcurrentHashMap<>();

    private final boolean autoFill;

    public BeeHive(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, boolean autoFill) {
        super(category, item, recipeType, recipe);

        blockSearchService = SlimyBeesPlugin.getBlockSearchService();
        productionService = SlimyBeesPlugin.getBeeProductionService();
        geneticService = SlimyBeesPlugin.getBeeGeneticService();

        this.autoFill = autoFill;

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
    protected void tick(BlockMenu menu, Block b) {
        if (isHiveWaiting(b)) {
            return;
        }

        BeeBreedingOperation operation = processor.getOperation(b);
        if (operation != null) {
            if (!operation.isFinished()) {
                if (operation.getProgress() % EFFECT_TICKS == 0) {
                    Slimefun.runSync(() -> operation.applyEffect(b.getLocation()));
                }

                processor.updateProgressBar(menu, STATUS_SLOT, operation);
                operation.addProgress(1);
            } else {
                menu.replaceExistingItem(STATUS_SLOT, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "));

                // if the user removed something during the crafting process, no output will be added
                Map<Integer, ItemStack> missingItems = menu.toInventory().removeItem(operation.getParents());
                if (missingItems.isEmpty()) {
                    addOutputs(menu, b, operation.getPrincess(), operation.getDrones(), operation.getProducts());
                }

                processor.endOperation(b);
            }
        } else {
            startNextOperation(menu, b);
        }

    }

    protected int getPrincessSlot() {
        return PRINCESS_SLOT;
    }

    protected int getDroneSlot() {
        return DRONE_SLOT;
    }

    @Override
    protected void onBreak(BlockBreakEvent e, BlockMenu menu, Location l) {
        super.onBreak(e, menu, l);

        menu.dropItems(l, getPrincessSlot());
        menu.dropItems(l, getDroneSlot());
        menu.dropItems(l, getOutputSlots());

        Block b = e.getBlock();
        processor.endOperation(b);
        resetWait(b);
    }

    @Nonnull
    protected BreedingModifierDTO getBreedingModifier(BlockMenu menu) {
        return BreedingModifierDTO.DEFAULT;
    }

    @Override
    protected void setupMenu(BlockMenuPreset preset) {
        MenuUtils.draw(preset, BACKGROUND_SLOTS, INPUT_BORDER_SLOTS, OUTPUT_BORDER_SLOTS);

        for (int slot : getOutputSlots()) {
            preset.addMenuClickHandler(slot, MenuUtils.getRemoveOnlyClickHandler());
        }
    }

    @Override
    protected void onNewInstance(BlockMenu menu, Block b) {
        super.onNewInstance(menu, b);

        menu.addItem(STATUS_SLOT, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "), (p, s, i, a) -> {
            resetWait(b);
            return false;
        });

        var restartClickHandler = MenuUtils.createAdvancedHandler((e, p, s, i, a) -> {
            handleBeeSlotClick(b, e);
            return true;
        });

        menu.addMenuClickHandler(getPrincessSlot(), restartClickHandler);
        menu.addMenuClickHandler(getDroneSlot(), restartClickHandler);
    }

    private void restartProcess(Block b) {
        resetWait(b);

        BeeBreedingOperation operation = processor.getOperation(b);
        if (operation != null) {
            processor.endOperation(b);
        }
    }

    private void resetWait(Block b) {
        waitingHives.remove(new BlockPosition(b));
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
            leftoverPrincess = menu.pushItem(princess, getPrincessSlot());

            ArrayUtils.shuffle(drones);
            for (int i = 0; i < drones.length; i++) {
                ItemStack leftoverDrone = menu.pushItem(drones[i], getDroneSlot());
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

    // TODO: 12.07.21 Too long and does too many things at once, clean this up
    private void startNextOperation(BlockMenu menu, Block b) {
        ItemStack princessItem = menu.getItemInSlot(getPrincessSlot());
        ItemStack droneItem = menu.getItemInSlot(getDroneSlot());

        SlimefunItem princessSfItem = SlimefunItem.getByItem(menu.getItemInSlot(getPrincessSlot()));
        SlimefunItem droneSfItem = SlimefunItem.getByItem(menu.getItemInSlot(getDroneSlot()));

        // we need exactly one princess and one drone
        if (!(princessSfItem instanceof Princess) || !(droneSfItem instanceof Drone)) {
            waitAndShowError(menu, b, BREEDING_WAIT_TICKS, "Items are not a princess and a drone");
            return;
        }

        // we can skip loading SlimefunItem for the second time using the unsafe method
        Genome princessGenome = geneticService.getGenomeUnsafe(princessItem);
        Genome droneGenome = geneticService.getGenomeUnsafe(droneItem);
        if (princessGenome == null || droneGenome == null) {
            waitAndShowError(menu, b, BREEDING_WAIT_TICKS, "Invalid bee genome");
            return;
        }

        Material material = princessGenome.getPlantValue();
        // air stands for null because allele values cannot be null
        if (!material.isAir() && !blockSearchService.containsBlock(b, princessGenome.getRangeValue(), material)) {
            waitAndShowError(menu, b, MISSING_PLANT_WAIT_TICKS, "The bee is missing its required plant");
            return;
        }

        // breed and produce
        BreedingModifierDTO modifier = getBreedingModifier(menu);
        BreedingResultDTO dto = geneticService.breed(princessGenome, droneGenome, modifier);
        List<ItemStack> products = productionService.produce(princessGenome, modifier);

        // make sure we don't consume multiple drones / princesses
        princessItem = princessItem.clone();
        princessItem.setAmount(1);
        droneItem = droneItem.clone();
        droneItem.setAmount(1);

        Consumer<Location> effect = l -> princessGenome.getEffectValue().accept(l, princessGenome.getRangeValue());
        processor.startOperation(b, new BeeBreedingOperation(princessItem, droneItem, dto, products, effect));
    }

    private boolean isHiveWaiting(Block b) {
        BlockPosition blockPos = new BlockPosition(b);
        WaitingOperation waitingOperation = waitingHives.get(blockPos);
        if (waitingOperation != null) {
            if (!waitingOperation.isFinished()) {
                waitingOperation.addProgress(1);
                return true;
            }

            waitingHives.remove(blockPos);
        }

        return false;
    }

    private void waitAndShowError(BlockMenu menu, Block b, int ticks, String message) {
        waitingHives.put(new BlockPosition(b), new WaitingOperation(ticks));
        CustomItemStack errorItem = new CustomItemStack(Material.RED_CONCRETE_POWDER,
                ChatColor.RED + message,
                "",
                ChatColor.GRAY + "The hive will try again in a few moments...",
                ChatColor.GRAY + "If you don't feel like waiting, click",
                ChatColor.GRAY + "this item to skip the cooldown!");

        menu.replaceExistingItem(STATUS_SLOT, errorItem);
    }

    // TODO: 15.07.21 How to handle COLLECT_TO_CURSOR (it fires the event for the bottom inventory)
    private void handleBeeSlotClick(Block b, InventoryClickEvent e) {
        ItemStack item;
        switch (e.getAction()) {
            // cases which invalidate the running process every time
            case DROP_ALL_SLOT, HOTBAR_SWAP, HOTBAR_MOVE_AND_READD, PICKUP_ALL, MOVE_TO_OTHER_INVENTORY, SWAP_WITH_CURSOR -> restartProcess(b);

            // cases which invalidate the process only when the last bee is removed
            case DROP_ONE_SLOT, PICKUP_HALF, PICKUP_ONE -> {
                item = e.getCurrentItem();
                if (item != null && !item.getType().isAir() && item.getAmount() == 1) {
                    restartProcess(b);
                }
            }
            // cases which should reset the waiting operation but do nothing to the process
            case PLACE_ALL, PLACE_ONE ->
                    // this should really only be called if the player places a bee to an empty slot but since
                    // resetting the wait is just removing an item from a HashMap O(1), there's no need to test the item
                    resetWait(b);

            // other types shouldn't break anything (and PICKUP_SOME shouldn't ever happen)
        }
    }

}
