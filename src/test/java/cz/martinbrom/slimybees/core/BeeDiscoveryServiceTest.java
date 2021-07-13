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
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BeeDiscoveryServiceTest {

    private static ServerMock server;
    private static BeeDiscoveryService beeDiscoveryService;

    private static AlleleSpecies species1;
    private static AlleleSpecies species2;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();

        // load Slimefun and SlimyBees
        MockBukkit.load(SlimefunPlugin.class);
        MockBukkit.load(SlimyBeesPlugin.class);

        AlleleRegistry registry = SlimyBeesPlugin.getAlleleRegistry();
        beeDiscoveryService = new BeeDiscoveryService(registry);

        species1 = new AlleleSpecies("species.test_first", "TEST_FIRST", false);
        registry.register(ChromosomeType.SPECIES, species1);
        species2 = new AlleleSpecies("species.test_second", "TEST_SECOND", false);
        registry.register(ChromosomeType.SPECIES, species2);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    public void testDiscoverNewSpecies() {
        Player p = server.addPlayer();

        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);
        assertFalse(profile.hasDiscovered(species1));
        assertTrue(beeDiscoveryService.discover(p, species1, true));
        assertTrue(profile.hasDiscovered(species1));
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
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);
        assertTrue(profile.hasDiscovered(species1));
        assertTrue(profile.hasDiscovered(species2));
    }

    @Test
    public void testDiscoverAllSpeciesOneLeft() {
        Player p = server.addPlayer();

        beeDiscoveryService.discover(p, species1, true);
        assertEquals(1, beeDiscoveryService.discoverAll(p));
    }

    @Test
    public void testDiscoverAllByOwner() {
        Player p = server.addPlayer();
        Player owner = server.addPlayer();

        beeDiscoveryService.discoverAll(owner);

        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);
        assertEquals(2, beeDiscoveryService.discoverAllByOwner(p, owner.getUniqueId()));
        assertTrue(profile.hasDiscovered(species1));
        assertTrue(profile.hasDiscovered(species2));
    }

    @Test
    public void testDiscoverAllByOwnerOneLeft() {
        Player p = server.addPlayer();
        Player owner = server.addPlayer();

        beeDiscoveryService.discoverAll(owner);
        beeDiscoveryService.discover(p, species1, true);

        assertEquals(1, beeDiscoveryService.discoverAllByOwner(p, owner.getUniqueId()));
    }

    @Test
    public void testUndiscoverAllSpecies() {
        Player p = server.addPlayer();
        beeDiscoveryService.discoverAll(p);

        beeDiscoveryService.undiscoverAll(p);
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);
        assertFalse(profile.hasDiscovered(species1));
        assertFalse(profile.hasDiscovered(species2));
    }

    @Test
    public void testDiscoverGenome() {
        Player p = server.addPlayer();

        Genome genome = mock(Genome.class);
        when(genome.getSpecies()).thenReturn(species1);

        assertTrue(beeDiscoveryService.discover(p, genome, true));
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);
        assertTrue(profile.hasDiscovered(species1));
        assertFalse(profile.hasDiscovered(species2));
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
