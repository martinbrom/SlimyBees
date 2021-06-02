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
    private final ItemStack analyzedItemStack;
    private final ItemStack unknownItemStack;

    private List<Pair<ItemStack, Double>> products;

    public AlleleSpeciesImpl(String uid, String name, boolean dominant, ItemStack analyzedItemStack, ItemStack unknownItemStack, boolean enchanted) {
        super(uid, name, dominant);

        this.analyzedItemStack = analyzedItemStack;
        this.unknownItemStack = unknownItemStack;
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

    @Nonnull
    @Override
    public ItemStack getUnknownItemStack() {
        return unknownItemStack;
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
