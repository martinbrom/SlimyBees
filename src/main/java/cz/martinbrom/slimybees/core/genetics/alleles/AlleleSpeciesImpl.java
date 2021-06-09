package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

@ParametersAreNonnullByDefault
public class AlleleSpeciesImpl extends AlleleImpl implements AlleleSpecies {

    private ItemStack princessItemStack;
    private ItemStack droneItemStack;
    private List<Pair<ItemStack, Double>> products;

    public AlleleSpeciesImpl(String uid, String name, boolean dominant) {
        super(uid, name, dominant);
    }

    @Nonnull
    @Override
    public ItemStack getPrincessItemStack() {
        return princessItemStack;
    }

    @Override
    public void setPrincessItemStack(ItemStack princessItemStack) {
        this.princessItemStack = princessItemStack;
    }

    @Nonnull
    @Override
    public ItemStack getDroneItemStack() {
        return droneItemStack;
    }

    @Override
    public void setDroneItemStack(ItemStack droneItemStack) {
        this.droneItemStack = droneItemStack;
    }

    @Nullable
    @Override
    public List<Pair<ItemStack, Double>> getProducts() {
        return products;
    }

    @Override
    public void setProducts(List<Pair<ItemStack, Double>> products) {
        this.products = products;
    }

}
