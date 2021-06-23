package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;
import cz.martinbrom.slimybees.items.bees.Princess;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

@ParametersAreNonnullByDefault
public class BeeProductionService {

    private final BeeGeneticService geneticService;

    public BeeProductionService(BeeGeneticService geneticService) {
        this.geneticService = geneticService;
    }

    @Nullable
    public ProductionResultDTO produce(ItemStack princessItemStack) {
        Validate.notNull(princessItemStack, "The princess must not be null!");

        SlimefunItem princessSfItem = SlimefunItem.getByItem(princessItemStack);
        // TODO: 12.06.21 Rename princesses globally to queen?
        if (princessSfItem instanceof Princess) {
            Genome genome = geneticService.getGenome(princessItemStack);

            if (genome != null) {
                // TODO: 12.06.21 Hardcoded duration for now, will get changed in later updates
                return new ProductionResultDTO(getProducts(genome), 60);
            }
        }

        return null;
    }

    /**
     * Returns a list of {@link ItemStack}s produced over the working duration
     * by the princess represented by the given {@link Genome}.
     * The amount of items produced is influenced by the princess' speed allele value.
     *
     * @param genome The princess' {@link Genome}
     * @return All items produced
     */
    @Nonnull
    private List<ItemStack> getProducts(Genome genome) {
        List<ItemStack> result = new ArrayList<>();

        int speedValue = genome.getSpeedValue();

        List<ChanceItemStack> products = genome.getSpecies().getProducts();
        if (products != null) {
            for (int i = 0; i < speedValue; i++) {
                for (ChanceItemStack product : products) {
                    if (product.shouldGet()) {
                        result.add(product.getItem());
                    }
                }
            }
        }

        // TODO: 11.06.21 Merge identical ItemStacks to reduce list size and in doing so improve performance down the line

        return result;
    }

}
