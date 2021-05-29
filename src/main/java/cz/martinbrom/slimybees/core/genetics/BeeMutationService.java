package cz.martinbrom.slimybees.core.genetics;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.SlimyBeesRegistry;
import cz.martinbrom.slimybees.items.bees.AnalyzedBee;
import cz.martinbrom.slimybees.items.bees.UnknownBee;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

@ParametersAreNonnullByDefault
public class BeeMutationService {

    @Nullable
    public static ItemStack[] getOutput(SlimefunItem firstItem, SlimefunItem secondItem) {
        Genome firstGenome = BeeGeneticService.getForItem(firstItem);
        Genome secondGenome = BeeGeneticService.getForItem(secondItem);

        if (firstGenome != null && secondGenome != null) {
            SlimyBeesRegistry registry = SlimyBeesPlugin.getRegistry();
            BeeMutation mutation = registry
                    .getBeeMutationTree()
                    .getMutationForParents(firstGenome.getSpeciesValue(), secondGenome.getSpeciesValue());

            int childrenCount = ThreadLocalRandom.current().nextBoolean()
                    ? firstGenome.getFertilityValue()
                    : secondGenome.getFertilityValue();

            ItemStack[] output = new ItemStack[childrenCount];
            for (int i = 0; i < childrenCount; i++) {
                if (mutation != null && ThreadLocalRandom.current().nextDouble() < mutation.getChance()) {
                    Pair<AnalyzedBee, UnknownBee> beePair = registry.getBeeTypes().get(mutation.getChild());
                    if (beePair != null) {
                        output[i] = beePair.getSecondValue().getItem();
                        continue;
                    }
                }

                Genome outputGenome = BeeGeneticService.combineGenomes(firstGenome, secondGenome);
                output[i] = registry.getBeeTypes().get(outputGenome.getSpeciesValue()).getSecondValue().getItem();
            }

            return output;
        }

        return null;
    }

}
