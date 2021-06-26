package cz.martinbrom.slimybees.core;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpeciesImpl;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BeeDiscoveryServiceTest {

    private static ServerMock server;
    private static BeeDiscoveryService beeDiscoveryService;

    private static AlleleSpeciesImpl species1;
    private static AlleleSpeciesImpl species2;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();

        // load Slimefun and SlimyBees
        SlimefunPlugin plugin = MockBukkit.load(SlimefunPlugin.class);
        SlimyBeesPlugin addon = MockBukkit.load(SlimyBeesPlugin.class);

        AlleleRegistry registry = SlimyBeesPlugin.getAlleleRegistry();
        beeDiscoveryService = new BeeDiscoveryService(registry);

        species1 = new AlleleSpeciesImpl("species.test1", "Test 1", false);
        registry.registerAllele(species1, ChromosomeTypeImpl.SPECIES);
        species2 = new AlleleSpeciesImpl("species.test2", "Test 2", false);
        registry.registerAllele(species2, ChromosomeTypeImpl.SPECIES);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    public void testDiscoverNewSpecies() {
        Player p = server.addPlayer();

        assertFalse(SlimyBeesPlayerProfile.get(p).hasDiscovered(species1));
        assertTrue(beeDiscoveryService.discover(p, species1, true));
        assertTrue(SlimyBeesPlayerProfile.get(p).hasDiscovered(species1));
    }

    @Test
    public void testUndiscoverSpecies() {
        Player p = server.addPlayer();
        beeDiscoveryService.discover(p, species1, true);

        assertTrue(beeDiscoveryService.discover(p, species1, false));
        assertFalse(SlimyBeesPlayerProfile.get(p).hasDiscovered(species1));
    }

    @Test
    public void testDiscoverSameSpeciesTwice() {
        Player p = server.addPlayer();

        assertTrue(beeDiscoveryService.discover(p, species1, true));
        assertFalse(beeDiscoveryService.discover(p, species1, true));
    }

    @Test
    public void testUndiscoverSameSpeciesTwice() {
        Player p = server.addPlayer();

        beeDiscoveryService.discover(p, species1, true);

        assertTrue(beeDiscoveryService.discover(p, species1, false));
        assertFalse(beeDiscoveryService.discover(p, species1, false));
    }

    @Test
    public void testDiscoverAllSpecies() {
        Player p = server.addPlayer();

        assertEquals(2, beeDiscoveryService.discoverAll(p));
        assertTrue(SlimyBeesPlayerProfile.get(p).hasDiscovered(species1));
        assertTrue(SlimyBeesPlayerProfile.get(p).hasDiscovered(species2));
    }

    @Test
    public void testUndiscoverAllSpecies() {
        Player p = server.addPlayer();
        beeDiscoveryService.discoverAll(p);

        beeDiscoveryService.undiscoverAll(p);
        assertFalse(SlimyBeesPlayerProfile.get(p).hasDiscovered(species1));
        assertFalse(SlimyBeesPlayerProfile.get(p).hasDiscovered(species2));
    }

    @Test
    public void testDiscoverGenome() {
        Player p = server.addPlayer();

        Genome genome = mock(Genome.class);
        when(genome.getSpecies()).thenReturn(species1);

        assertTrue(beeDiscoveryService.discover(p, genome, true));
        assertTrue(SlimyBeesPlayerProfile.get(p).hasDiscovered(species1));
    }

    @Test
    public void testUndiscoverGenome() {
        Player p = server.addPlayer();

        Genome genome = mock(Genome.class);
        when(genome.getSpecies()).thenReturn(species1);

        beeDiscoveryService.discover(p, genome, true);

        assertTrue(beeDiscoveryService.discover(p, genome, false));
        assertFalse(SlimyBeesPlayerProfile.get(p).hasDiscovered(species1));
    }

}
