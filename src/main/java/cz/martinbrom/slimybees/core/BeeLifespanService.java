package cz.martinbrom.slimybees.core;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.BreedingModifierDTO;
import cz.martinbrom.slimybees.core.genetics.Genome;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

@ParametersAreNonnullByDefault
public class BeeLifespanService {

    public static final int DEFAULT_CYCLE_DURATION = 40;

    private final int cycleDuration;

    public BeeLifespanService(Config config) {
        Validate.notNull(config, "The config cannot be null!");

        cycleDuration = Math.max(1, config.getOrSetDefault("options.breeding-cycle-duration", DEFAULT_CYCLE_DURATION));
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
