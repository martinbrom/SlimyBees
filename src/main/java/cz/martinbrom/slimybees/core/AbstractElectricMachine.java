package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.recipe.AbstractRecipe;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.InvUtils;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;

@ParametersAreNonnullByDefault
public abstract class AbstractElectricMachine extends AbstractMachine implements EnergyNetComponent {

    private final List<AbstractRecipe> recipes = new ArrayList<>();

    private int energyConsumedPerTick = -1;
    private int energyCapacity = -1;
    private int processingSpeed = -1;

    public AbstractElectricMachine(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected boolean checkCraftPreconditions(Block b) {
        return takeCharge(b.getLocation());
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    /**
     * This method returns the max amount of electricity this machine can hold.
     *
     * @return The max amount of electricity this Block can store.
     */
    public int getCapacity() {
        return energyCapacity;
    }

    /**
     * This method returns the amount of energy that is consumed per operation.
     *
     * @return The rate of energy consumption
     */
    public int getEnergyConsumption() {
        return energyConsumedPerTick;
    }

    /**
     * This method returns the speed at which this machine will operate.
     * This can be implemented on an instantiation-level to create different tiers
     * of machines.
     *
     * @return The speed of this machine
     */
    public int getSpeed() {
        return processingSpeed;
    }

    /**
     * This sets the energy capacity for this machine.
     * This method <strong>must</strong> be called before registering the item
     * and only before registering.
     *
     * @param capacity The amount of energy this machine can store
     * @return This method will return the current instance of {@link AbstractElectricMachine}, so that can be chained.
     */
    public final AbstractElectricMachine setCapacity(int capacity) {
        Validate.isTrue(capacity > 0, "The capacity must be greater than zero!");

        if (getState() == ItemState.UNREGISTERED) {
            this.energyCapacity = capacity;
            return this;
        } else {
            throw new IllegalStateException("You cannot modify the capacity after the Item was registered.");
        }
    }

    /**
     * This sets the speed of this machine.
     *
     * @param speed The speed multiplier for this machine, must be above zero
     * @return This method will return the current instance of {@link AbstractElectricMachine}, so that can be chained.
     */
    public final AbstractElectricMachine setProcessingSpeed(int speed) {
        Validate.isTrue(speed > 0, "The speed must be greater than zero!");

        this.processingSpeed = speed;
        return this;
    }

    /**
     * This method sets the energy consumed by this machine per tick.
     *
     * @param energyConsumption The energy consumed per tick
     * @return This method will return the current instance of {@link AbstractElectricMachine}, so that can be chained.
     */
    public final AbstractElectricMachine setEnergyConsumption(int energyConsumption) {
        Validate.isTrue(energyConsumption > 0, "The energy consumption must be greater than zero!");
        Validate.isTrue(energyCapacity > 0, "You must specify the capacity before you can set the consumption amount.");
        Validate.isTrue(energyConsumption <= energyCapacity, "The energy consumption cannot be higher than the capacity (" + energyCapacity + ')');

        this.energyConsumedPerTick = energyConsumption;
        return this;
    }

    public void registerRecipe(AbstractRecipe recipe) {
        recipe.setDuration(recipe.getDuration() / getSpeed());
        recipes.add(recipe);
    }

    @Override
    public void register(SlimefunAddon addon) {
        super.register(addon);
    }

    /**
     * This method will remove charge from a location if it is chargeable.
     *
     * @param l location to try to remove charge from
     * @return Whether charge was taken if its chargeable
     */
    protected boolean takeCharge(Location l) {
        Validate.notNull(l, "Can't attempt to take charge from a null location!");

        if (isChargeable()) {
            int charge = getCharge(l);

            if (charge < getEnergyConsumption()) {
                return false;
            }

            setCharge(l, charge - getEnergyConsumption());
        }

        return true;
    }

    @Nullable
    @Override
    protected AbstractRecipe findNextRecipe(BlockMenu menu) {
        List<ItemStack> items = new ArrayList<>();

        for (int slot : getInputSlots()) {
            ItemStack item = menu.getItemInSlot(slot);

            if (item != null) {
                items.add(ItemStackWrapper.wrap(item));
            }
        }

        AbstractRecipe recipe = RecipeMatchService.match(items.toArray(new ItemStack[0]), recipes);
        if (recipe == null || !InvUtils.fitAll(menu.toInventory(), recipe.getIngredientArray(), getOutputSlots())) {
            return null;
        }

        for (ItemStack ingredient : recipe.getIngredients()) {
            ItemUtils.consumeItem(ingredient, ingredient.getAmount(), false);
        }

        return null;
    }

}
