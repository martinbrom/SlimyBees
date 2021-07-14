package cz.martinbrom.slimybees.core;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.Genome;

/**
 * This service groups together logic that happens when analyzing a bee {@link ItemStack}.
 */
@ParametersAreNonnullByDefault
public class BeeAnalysisService {

    private final BeeGeneticService geneticService;
    private final BeeDiscoveryService discoveryService;
    private final BeeLoreService beeLoreService;

    public BeeAnalysisService(BeeGeneticService geneticService, BeeDiscoveryService discoveryService, BeeLoreService beeLoreService) {
        this.geneticService = geneticService;
        this.discoveryService = discoveryService;
        this.beeLoreService = beeLoreService;
    }

    /**
     * Tries to analyze an {@link ItemStack}, if it represents an 'unknown' bee,
     * tries to mark the species as discovered for the given {@link Player}
     * and returns the analyzed {@link ItemStack}.
     *
     * @param p The {@link Player} analyzing the {@link ItemStack}
     * @param item The {@link ItemStack} being analyzed
     * @return The analyzed {@link ItemStack}
     */
    @Nullable
    public ItemStack analyze(Player p, ItemStack item) {
        if (beeLoreService.isUnknown(item)) {
            Genome genome = geneticService.getGenome(item);

            if (genome != null) {
                discoveryService.discover(p, genome);

                ItemStack copy = beeLoreService.updateLore(item, genome);
                copy.setAmount(item.getAmount());

                return copy;
            }
        }

        return null;
    }

}
