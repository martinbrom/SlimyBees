package cz.martinbrom.slimybees.core;

import java.util.Map;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BeeDiscoveryServiceTest {

    private static final String FIRST_SPECIES_UID = "species:test_first";
    private static final String SECOND_SPECIES_UID = "species:test_second";

    private static ServerMock server;
    private static BeeDiscoveryService beeDiscoveryService;

    @Mock
    private Config config;

    private AlleleSpecies species1;
    private AlleleSpecies species2;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();

        // load Slimefun and SlimyBees
        MockBukkit.load(SlimefunPlugin.class);
        MockBukkit.load(SlimyBeesPlugin.class);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // we don't mock here because we just use too much of the actual behavior
        // and we don't need to modify anything
        AlleleRegistry alleleRegistry = new AlleleRegistry();
        beeDiscoveryService = new BeeDiscoveryService(alleleRegistry, config);

        species1 = new AlleleSpecies(FIRST_SPECIES_UID, "TEST_FIRST", false);
        species2 = new AlleleSpecies(SECOND_SPECIES_UID, "TEST_SECOND", false);

        alleleRegistry.register(ChromosomeType.SPECIES, species1);
        alleleRegistry.register(ChromosomeType.SPECIES, species2);
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
        assertTrue(beeDiscoveryService.discover(p, species1));
        assertTrue(profile.hasDiscovered(species1));
    }

    @Test
    public void testUndiscoverSpecies() {
        Player p = server.addPlayer();
        beeDiscoveryService.discover(p, species1);

        assertTrue(beeDiscoveryService.undiscover(p, species1));
        assertFalse(SlimyBeesPlayerProfile.get(p).hasDiscovered(species1));
    }

    @Test
    public void testDiscoverSameSpeciesTwice() {
        Player p = server.addPlayer();

        assertTrue(beeDiscoveryService.discover(p, species1));
        assertFalse(beeDiscoveryService.discover(p, species1));
    }

    @Test
    public void testUndiscoverSameSpeciesTwice() {
        Player p = server.addPlayer();

        beeDiscoveryService.discover(p, species1);

        assertTrue(beeDiscoveryService.undiscover(p, species1));
        assertFalse(beeDiscoveryService.undiscover(p, species1));
    }

    @Test
    public void testDiscoverAllSpeciesOneLeft() {
        Player p = server.addPlayer();

        beeDiscoveryService.discover(p, species1);
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
        beeDiscoveryService.discover(p, species1);

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

        assertTrue(beeDiscoveryService.discover(p, genome));
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);
        assertTrue(profile.hasDiscovered(species1));
        assertFalse(profile.hasDiscovered(species2));
    }

    @Test
    public void testDiscoverGlobal() {
        Player p = server.addPlayer();
        Player p2 = server.addPlayer();

        Genome genome = mock(Genome.class);
        when(genome.getSpecies()).thenReturn(species1);

        assertEquals(0, beeDiscoveryService.getDiscoveryInfo().size());
        beeDiscoveryService.discover(p, genome);
        Map<String, String> discoveryInfo = beeDiscoveryService.getDiscoveryInfo();
        assertEquals(p.getName(), discoveryInfo.get(FIRST_SPECIES_UID));

        beeDiscoveryService.discover(p2, genome);
        assertEquals(p.getName(), discoveryInfo.get(FIRST_SPECIES_UID));
    }

    @Test
    public void testDiscoverSpeciesDoesNotInfluenceGlobal() {
        Player p = server.addPlayer();

        Genome genome = mock(Genome.class);
        when(genome.getSpecies()).thenReturn(species1);

        assertEquals(0, beeDiscoveryService.getDiscoveryInfo().size());
        beeDiscoveryService.discover(p, genome.getSpecies());
        assertEquals(0, beeDiscoveryService.getDiscoveryInfo().size());
    }

    @Test
    public void testDiscoverAllDoesNotInfluenceGlobal() {
        Player p = server.addPlayer();

        assertEquals(0, beeDiscoveryService.getDiscoveryInfo().size());
        beeDiscoveryService.discoverAll(p);
        assertEquals(0, beeDiscoveryService.getDiscoveryInfo().size());
    }

    @Test
    public void testDiscoverAllByOwnerDoesNotInfluenceGlobal() {
        Player p = server.addPlayer();
        Player p2 = server.addPlayer();

        Genome genome = mock(Genome.class);
        when(genome.getSpecies()).thenReturn(species1);

        beeDiscoveryService.discover(p, genome);
        beeDiscoveryService.discoverAllByOwner(p2, p.getUniqueId());
        assertEquals(p.getName(), beeDiscoveryService.getDiscoveryInfo().get(FIRST_SPECIES_UID));
    }

}
