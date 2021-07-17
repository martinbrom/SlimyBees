package cz.martinbrom.slimybees.core.category;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;

@ParametersAreNonnullByDefault
public class BeeAtlasCategoryFactory {

    private final BeeLoreService loreService;
    private final BeeRegistry beeRegistry;
    private final BeeGeneticService geneticService;
    private final AlleleRegistry alleleRegistry;
    private final BeeAtlasNavigationService navigationService;

    public BeeAtlasCategoryFactory(BeeLoreService loreService, BeeRegistry beeRegistry, BeeGeneticService geneticService,
                                   AlleleRegistry alleleRegistry, BeeAtlasNavigationService navigationService) {
        this.loreService = loreService;
        this.beeRegistry = beeRegistry;
        this.geneticService = geneticService;
        this.alleleRegistry = alleleRegistry;
        this.navigationService = navigationService;
    }

    @Nonnull
    public BeeAtlasDetailCategory createDetail(AlleleSpecies species) {
        return new BeeAtlasDetailCategory(loreService, beeRegistry, geneticService, alleleRegistry, navigationService, this, species);
    }

    @Nonnull
    public BeeAtlasListCategory createList(ItemStack displayItem) {
        return new BeeAtlasListCategory(loreService, beeRegistry, geneticService, alleleRegistry, navigationService, this, displayItem);
    }

}
