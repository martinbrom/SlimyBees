package cz.martinbrom.slimybees.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.seeseemelk.mockbukkit.MockBukkit;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BreedingModifierDTO;
import cz.martinbrom.slimybees.core.genetics.Genome;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BeeLifespanServiceTest {

    private static final int CYCLE_DURATION = 10;

    private static BeeLifespanService lifespanService;

    @Mock
    private Config config;

    private Genome genome;
    private BreedingModifierDTO modifier;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();

        // load Slimefun and SlimyBees
        MockBukkit.load(SlimefunPlugin.class);
        MockBukkit.load(SlimyBeesPlugin.class);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(config.getOrSetDefault(anyString(), anyInt())).thenReturn(CYCLE_DURATION);

        lifespanService = new BeeLifespanService(config);

        modifier = mock(BreedingModifierDTO.class);
        genome = mock(Genome.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    public void testGetLifespan() {
        int cycleCount = 10;
        double lifespanModifier = 2;
        mockLifespan(cycleCount, lifespanModifier);

        int duration = (int) (CYCLE_DURATION * cycleCount * lifespanModifier);
        assertEquals(duration, lifespanService.getLifespan(genome, modifier));
    }

    @Test
    public void testGetLifespanAlwaysAtLeastOneCycle() {
        mockLifespan(10, 0.000001);

        assertEquals(CYCLE_DURATION, lifespanService.getLifespan(genome, modifier));
    }

    @Test
    public void testGetProductionCycleCount() {
        mockLifespan(5, 2);

        assertEquals(10, lifespanService.getProductionCycleCount(genome, modifier));
    }

    @Test
    public void testGetProductionCycleCountAlwaysAtLeastOneCycle() {
        mockLifespan(5, 0.0000001);

        assertEquals(1, lifespanService.getProductionCycleCount(genome, modifier));
    }

    private void mockLifespan(int value, double modifierValue) {
        when(genome.getLifespanValue()).thenReturn(value);
        when(modifier.getLifespanModifier()).thenReturn(modifierValue);
    }

}
