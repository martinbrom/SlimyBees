package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

@ParametersAreNonnullByDefault
public class AlleleSpeciesImpl extends AlleleImpl implements AlleleSpecies {

    private final boolean enchanted;

    private ItemStack analyzedItemStack;
    private ItemStack unknownItemStack;
    private List<Pair<ItemStack, Double>> products;

    public AlleleSpeciesImpl(String uid, String name, boolean dominant, boolean enchanted) {
        super(uid, name, dominant);

        this.enchanted = enchanted;
    }

    @Override
    public boolean isEnchanted() {
        return enchanted;
    }

    @Nonnull
    @Override
    public ItemStack getAnalyzedItemStack() {
        return analyzedItemStack;
    }

    @Override
    public void setAnalyzedItemStack(ItemStack analyzedItemStack) {
        this.analyzedItemStack = analyzedItemStack;
    }

    @Nonnull
    @Override
    public ItemStack getUnknownItemStack() {
        return unknownItemStack;
    }

    @Override
    public void setUnknownItemStack(ItemStack unknownItemStack) {
        this.unknownItemStack = unknownItemStack;
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
