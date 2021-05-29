package cz.martinbrom.slimybees.core.genetics;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.items.bees.AbstractBee;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

import static cz.martinbrom.slimybees.core.genetics.ChromosomeType.CHROMOSOME_COUNT;

@ParametersAreNonnullByDefault
public class BeeGeneticService {

    @Nullable
    public static Genome getForItem(SlimefunItem item) {
        if (!(item instanceof AbstractBee)) {
            return null;
        }

        CustomItemDataService beeTypeService = SlimyBeesPlugin.instance().getBeeTypeService();
        Optional<String> genomeStr = beeTypeService.getItemData(item.getItem());

        return genomeStr.map(Genome::new).orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static Genome combineGenomes(Genome firstGenome, Genome secondGenome) {
        Chromosome<Object>[] firstChromosomes = firstGenome.getChromosomes();
        Chromosome<Object>[] secondChromosomes = secondGenome.getChromosomes();

        Chromosome<Object>[] finalChromosomes = new Chromosome[CHROMOSOME_COUNT];
        for (int i = 0; i < CHROMOSOME_COUNT; i++) {
            finalChromosomes[i] = combineChromosomes(firstChromosomes[i], secondChromosomes[i]);
        }

        return new Genome(finalChromosomes);
    }

    private static Chromosome<Object> combineChromosomes(Chromosome<Object> firstChromosome, Chromosome<Object> secondChromosome) {
        int randomChromosomeIndex = ThreadLocalRandom.current().nextInt(4);
        Allele<Object> firstAllele = randomChromosomeIndex % 2 == 0
                ? firstChromosome.getActiveAllele()
                : secondChromosome.getActiveAllele();
        Allele<Object> secondAllele = randomChromosomeIndex < 2
                ? firstChromosome.getInactiveAllele()
                : secondChromosome.getInactiveAllele();

        return new Chromosome<>(firstAllele, secondAllele);
    }

}
