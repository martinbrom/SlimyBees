package cz.martinbrom.slimybees.core.genetics;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;
import cz.martinbrom.slimybees.items.bees.UnknownBee;
import cz.martinbrom.slimybees.utils.StringUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

@ParametersAreNonnullByDefault
public class BeeAnalysisService {

    private final BeeGeneticService geneticService;

    public BeeAnalysisService(BeeGeneticService geneticService) {
        this.geneticService = geneticService;
    }

    // TODO: 30.05.21 Add BeeDiscoveryService
    @Nullable
    public ItemStack analyze(ItemStack item) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem instanceof UnknownBee) {
            Genome genome = geneticService.getGenome(item);
            if (genome != null) {
                ItemStack itemStack = item.clone();
                // TODO: 04.06.21 This stays as an unknown bee which makes the Beealyzer tick infinitely
                //  Rewrite AnalyzedBee and UnknownBee as one object with a boolean OR check lore?

                if (itemStack.hasItemMeta()) {
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setLore(createLore(genome));
                    itemStack.setItemMeta(meta);
                    itemStack.setAmount(item.getAmount());

                    return itemStack;
                }
            }
        }

        return null;
    }

    @Nonnull
    private static List<String> createLore(Genome genome) {
        List<String> lore = new ArrayList<>();
        lore.add("");   // intentional empty first line

        Chromosome[] chromosomes = genome.getChromosomes();
        for (ChromosomeType type : ChromosomeTypeImpl.values()) {
            lore.add(ChatColor.WHITE + StringUtils.snakeToCamel(type.toString()) + ": "
                    + ChatColor.GRAY + StringUtils.snakeToCamel(chromosomes[type.ordinal()].getPrimaryAllele().getName()) + " / "
                    + StringUtils.snakeToCamel(chromosomes[type.ordinal()].getSecondaryAllele().getName()));
        }

        return lore;
    }

}
