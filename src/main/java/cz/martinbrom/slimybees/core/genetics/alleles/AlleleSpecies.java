package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;

@ParametersAreNonnullByDefault
public class AlleleSpecies extends Allele {

    private ItemStack princessItemStack;
    private ItemStack droneItemStack;
    private List<ChanceItemStack> products;

    public AlleleSpecies(String uid, String name, boolean dominant) {
        super(uid, name, dominant);
    }

    @Nonnull
    public ItemStack getPrincessItemStack() {
        return princessItemStack;
    }

    public void setPrincessItemStack(ItemStack princessItemStack) {
        this.princessItemStack = princessItemStack;
    }

    @Nonnull
    public ItemStack getDroneItemStack() {
        return droneItemStack;
    }

    public void setDroneItemStack(ItemStack droneItemStack) {
        this.droneItemStack = droneItemStack;
    }

    @Nullable
    public List<ChanceItemStack> getProducts() {
        return products;
    }

    public void setProducts(List<ChanceItemStack> products) {
        this.products = products;
    }

}
