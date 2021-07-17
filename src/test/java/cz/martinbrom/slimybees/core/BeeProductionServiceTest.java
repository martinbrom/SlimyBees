package cz.martinbrom.slimybees.core;

import java.util.Collections;
import java.util.List;

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
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BeeProductionServiceTest {

    private static BeeProductionService productionService;

    @Mock
    private BeeLifespanService lifespanService;

    private Genome genome;
    private BreedingModifierDTO modifier;
    private AlleleSpecies species;

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

        productionService = new BeeProductionService(lifespanService);

        modifier = mock(BreedingModifierDTO.class);
        genome = mock(Genome.class);
        species = mock(AlleleSpecies.class);

        when(modifier.getProductionModifier()).thenReturn(1.0);
        when(genome.getProductivityValue()).thenReturn(1.0);

        when(species.getProducts()).thenReturn(Collections.emptyList());
        when(genome.getSpecies()).thenReturn(species);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testProduceNoCycles() {
        when(lifespanService.getProductionCycleCount(genome, modifier)).thenReturn(0);

        assertEquals(Collections.emptyList(), productionService.produce(genome, modifier));
    }

    @Test
    void testProduceBeeNoProducts() {
        assertEquals(Collections.emptyList(), productionService.produce(genome, modifier));
    }

    @Test
    void testProduce() {
        ChanceItemStack product = mock(ChanceItemStack.class);
        List<ChanceItemStack> products = Collections.singletonList(product);

        int cycleCount = 5;
        when(lifespanService.getProductionCycleCount(genome, modifier)).thenReturn(cycleCount);
        when(species.getProducts()).thenReturn(products);

        productionService.produce(genome, modifier);
        verify(product, times(cycleCount)).shouldGet(anyDouble());
    }

}
