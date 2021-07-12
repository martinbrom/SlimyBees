package cz.martinbrom.slimybees.core;

import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.items.bees.Princess;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class BeeAnalysisServiceTest {

    private static ServerMock server;
    private static BeeAnalysisService beeAnalysisService;

    private static Princess princess;

    @Mock
    private BeeGeneticService geneticService;

    @Mock
    private BeeDiscoveryService discoveryService;

    @Mock
    private BeeLoreService beeLoreService;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();

        // load Slimefun and SlimyBees
        SlimefunPlugin plugin = MockBukkit.load(SlimefunPlugin.class);
        MockBukkit.load(SlimyBeesPlugin.class);

        Category category = new Category(new NamespacedKey(plugin, "test-category"), new ItemStack(Material.HONEY_BLOCK));
        princess = new Princess(category, ItemStacks.createPrincess("TEST", "Test", false, "", "Test lore"), RecipeType.NULL, new ItemStack[9]);
        princess.register(plugin);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        beeAnalysisService = new BeeAnalysisService(geneticService, discoveryService, beeLoreService);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    public void testAnalyzeUnknownBee() {
        Player p = server.addPlayer();
        ItemStack item = princess.getItem().clone();
        item.setAmount(22);
        Genome genome = mock(Genome.class);

        when(beeLoreService.isUnknown(item)).thenReturn(true);
        when(beeLoreService.updateLore(item, genome)).thenCallRealMethod();
        when(beeLoreService.createLore(genome)).thenReturn(Collections.singletonList(""));
        when(geneticService.getGenome(item)).thenReturn(genome);
        ItemStack analyzedItem = beeAnalysisService.analyze(p, item);

        verify(discoveryService).discover(eq(p), any(Genome.class), eq(true));
        verify(beeLoreService).updateLore(eq(item), eq(genome));
        assertEquals(item.getAmount(), analyzedItem.getAmount());
        assertFalse(beeLoreService.isUnknown(analyzedItem));
    }

    @Test
    public void testAnalyzeAnalyzedBee() {
        Player p = server.addPlayer();
        ItemStack item = princess.getItem();

        when(beeLoreService.isUnknown(item)).thenReturn(false);
        ItemStack analyzedItem = beeAnalysisService.analyze(p, item);

        assertNull(analyzedItem);
        verifyNoInteractions(discoveryService);
        verifyNoInteractions(geneticService);
    }

}
