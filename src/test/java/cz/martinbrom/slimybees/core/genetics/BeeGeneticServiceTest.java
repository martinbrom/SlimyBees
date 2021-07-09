package cz.martinbrom.slimybees.core.genetics;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.seeseemelk.mockbukkit.MockBukkit;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleImpl;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.items.bees.Princess;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO: 27.06.21 Test the rest in BeeGeneticService
public class BeeGeneticServiceTest {

    private static BeeGeneticService beeGeneticService;
    private static Princess princess;

    @Mock
    private CustomItemDataService beeTypeService;

    @Mock
    private BeeLoreService beeLoreService;

    @Mock
    private BeeRegistry beeRegistry;

    @Mock
    private GenomeParser genomeParser;

    @Mock
    private Config config;

    @Mock
    private AlleleRegistry alleleRegistry;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();

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

        when(config.getOrSetDefault(eq("options.breeding_cycle_duration"), any())).thenReturn(BeeGeneticService.DEFAULT_CYCLE_DURATION);

        beeGeneticService = new BeeGeneticService(beeTypeService, beeLoreService, beeRegistry, genomeParser, config, alleleRegistry);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    public void testGetGenomeBeeItem() {
        Genome expected = mock(Genome.class);

        when(beeTypeService.getItemData(princess.getItem())).thenReturn(Optional.of("test"));
        when(genomeParser.parse("test")).thenReturn(expected);

        Genome actual = beeGeneticService.getGenome(princess.getItem());
        assertEquals(expected, actual);
    }

    @Test
    public void testGetGenomeOtherItem() {
        ItemStack item = new ItemStack(Material.COBBLESTONE);

        assertNull(beeGeneticService.getGenome(item));
    }

    @Test
    public void testGetGenomeSpecies() {
        AlleleSpecies species = mock(AlleleSpecies.class);
        Allele[] template = new AlleleImpl[] { new AlleleImpl("uid", "name", false), new AlleleImpl("uid2", "name2", true) };

        when(species.getUid()).thenReturn("species.test");
        when(beeRegistry.getTemplate("species.test")).thenReturn(template);

        Genome genome = beeGeneticService.getGenome(species);
        assertNotNull(genome);
        for (int i = 0; i < 2; i++) {
            Chromosome chromosome = genome.getChromosomes()[i];
            assertEquals(template[i], chromosome.getPrimaryAllele());
            assertEquals(template[i], chromosome.getSecondaryAllele());
        }
    }

    @Test
    public void testUpdateItemGenome() {
        Genome genome = mock(Genome.class);

        when(genomeParser.serialize(genome)).thenReturn("serialized");
        beeGeneticService.updateItemGenome(princess.getItem(), genome);

        verify(beeTypeService).setItemData(eq(princess.getItem()), eq("serialized"));
    }

}
