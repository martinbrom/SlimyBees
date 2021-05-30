package cz.martinbrom.slimybees.core.genetics;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.SlimyBeesRegistry;
import cz.martinbrom.slimybees.items.bees.AnalyzedBee;
import cz.martinbrom.slimybees.items.bees.UnknownBee;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

// TODO: 30.05.21 This class is weird, maybe merge with BeeGeneticService
@ParametersAreNonnullByDefault
public class BeeMutationService {

    private static final ItemStack AIR_STACK = new ItemStack(Material.AIR);

    @Nullable
    public static ItemStack[] getOutput(SlimefunItem firstItem, SlimefunItem secondItem) {
        Genome firstGenome = BeeGeneticService.getForItem(firstItem);
        Genome secondGenome = BeeGeneticService.getForItem(secondItem);

        if (firstGenome != null && secondGenome != null) {
            SlimyBeesRegistry registry = SlimyBeesPlugin.getRegistry();
            BeeMutation mutation = registry
                    .getBeeMutationTree()
                    .getMutationForParents(firstGenome.getSpeciesValue(), secondGenome.getSpeciesValue());

            // TODO: 30.05.21 Use the fertility value as average count for a normal distribution, not THE count
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

                output[i] = createChild(firstGenome, secondGenome);
            }

            return output;
        }

        return null;
    }

    private static ItemStack createChild(Genome firstGenome, Genome secondGenome) {
        Genome outputGenome = BeeGeneticService.combineGenomes(firstGenome, secondGenome);
        Pair<AnalyzedBee, UnknownBee> beePair = SlimyBeesPlugin.getRegistry().getBeeTypes().get(outputGenome.getSpeciesValue());
        if (beePair != null) {
            ItemStack itemStack = beePair.getSecondValue().getItem().clone();
            BeeGeneticService.updateItemGenome(itemStack, outputGenome);
            return itemStack;
        }

        return AIR_STACK;
    }

}
