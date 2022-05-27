package cz.martinbrom.slimybees.core;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.BreedingModifierDTO;
import cz.martinbrom.slimybees.core.genetics.Genome;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;

@ParametersAreNonnullByDefault
public class BeeLifespanService {

    public static final int DEFAULT_CYCLE_DURATION = 40;

    private final int cycleDuration;

    public BeeLifespanService(Config config) {
        Validate.notNull(config, "The config cannot be null!");

        int duration = config.getInt("options.breeding-cycle-duration");
        cycleDuration = duration < 1 ? DEFAULT_CYCLE_DURATION : duration;
    }

    /**
     * Returns the duration in ticks that the bee represented by given {@link Genome}
     * will live for, taking into account the {@link BreedingModifierDTO}.
     *
     * @param genome The {@link Genome} of the bee
     * @param modifier The {@link BreedingModifierDTO} that changes the bees lifespan
     * @return Duration that the bee will live for in ticks
     */
    public int getLifespan(Genome genome, BreedingModifierDTO modifier) {
        return getProductionCycleCount(genome, modifier) * cycleDuration;
    }

    /**
     * Returns the number of production cycles that the bee represented by given {@link Genome}
     * will work for, taking into account the {@link BreedingModifierDTO}.
     *
     * @param genome The {@link Genome} of the bee
     * @param modifier The {@link BreedingModifierDTO} that changes the bees productivity
     * @return Number of cycles that the bee will work for
     */
    public int getProductionCycleCount(Genome genome, BreedingModifierDTO modifier) {
        // 1 cycle is the minimum duration (avoids deadly frames being too op)
        return (int) Math.max(1, genome.getLifespanValue() * modifier.getLifespanModifier());
    }

}
