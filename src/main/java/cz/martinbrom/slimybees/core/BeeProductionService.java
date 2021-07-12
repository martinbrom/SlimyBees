package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.genetics.BreedingModifierDTO;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;

@ParametersAreNonnullByDefault
public class BeeProductionService {

    private final BeeLifespanService lifespanService;

    public BeeProductionService(BeeLifespanService lifespanService) {
        Validate.notNull(lifespanService, "BeeLifespanService cannot be null!");

        this.lifespanService = lifespanService;
    }

    /**
     * Returns a list of {@link ItemStack}s produced over the working duration
     * by the princess represented by the given {@link Genome}.
     * The amount of items produced is influenced by the princess' productivity allele value
     * and the {@link BreedingModifierDTO}.
     *
     * @param princessGenome The princess' {@link Genome}
     * @param modifier Modifiers applied to the breeding process by the housing and/or frames
     * @return All items produced
     */
    @Nonnull
    public List<ItemStack> produce(Genome princessGenome, BreedingModifierDTO modifier) {
        List<ItemStack> result = new ArrayList<>();

        double productivityValue = princessGenome.getProductivityValue() * modifier.getProductionModifier();

        List<ChanceItemStack> products = princessGenome.getSpecies().getProducts();
        if (products != null) {
            int productionCycleCount = lifespanService.getProductionCycleCount(princessGenome, modifier);
            for (int i = 0; i < productionCycleCount; i++) {
                for (ChanceItemStack product : products) {
                    if (product.shouldGet(productivityValue)) {
                        result.add(product.getItem());
                    }
                }
            }
        }

        // TODO: 06.07.21 Merge identical ItemStacks (or not create duplicates) to improve performance down the line

        return result;
    }

}
