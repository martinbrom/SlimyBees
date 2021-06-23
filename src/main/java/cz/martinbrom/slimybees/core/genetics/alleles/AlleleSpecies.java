package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;

@ParametersAreNonnullByDefault
public interface AlleleSpecies extends Allele {

    @Nonnull
    ItemStack getPrincessItemStack();

    void setPrincessItemStack(ItemStack princessItemStack);

    @Nonnull
    ItemStack getDroneItemStack();

    void setDroneItemStack(ItemStack droneItemStack);

    @Nullable
    List<ChanceItemStack> getProducts();

    void setProducts(List<ChanceItemStack> products);

}
