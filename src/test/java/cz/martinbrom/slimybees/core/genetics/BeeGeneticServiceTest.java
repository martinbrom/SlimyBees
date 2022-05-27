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
import cz.martinbrom.slimybees.core.BeeLifespanService;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.items.bees.Princess;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.services.CustomItemDataService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    private AlleleRegistry alleleRegistry;

    @Mock
    private BeeLifespanService lifespanService;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();

        // load Slimefun and SlimyBees
        Slimefun plugin = MockBukkit.load(Slimefun.class);
        MockBukkit.load(SlimyBeesPlugin.class);

        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "test-category"), new ItemStack(Material.HONEY_BLOCK));
        princess = new Princess(category, ItemStacks.createPrincess("TEST", "Test", false, "", "Test lore"), RecipeType.NULL, new ItemStack[9]);
        princess.register(plugin);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        beeGeneticService = new BeeGeneticService(beeTypeService, beeLoreService, beeRegistry, genomeParser,
                alleleRegistry, lifespanService);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testGetGenomeBeeItem() {
        Genome expected = mock(Genome.class);

        when(beeTypeService.getItemData(princess.getItem())).thenReturn(Optional.of("test"));
        when(genomeParser.parse("test")).thenReturn(expected);

        Genome actual = beeGeneticService.getGenome(princess.getItem());
        assertEquals(expected, actual);
    }

    @Test
    void testGetGenomeOtherItem() {
        ItemStack item = new ItemStack(Material.COBBLESTONE);

        assertNull(beeGeneticService.getGenome(item));
    }

    @Test
    void testGetGenomeSpecies() {
        AlleleSpecies species = mock(AlleleSpecies.class);
        Allele[] template = new Allele[] {
                new Allele("test:uid", "NAME", false),
                new Allele("test:uid_second", "NAME_SECOND", true)
        };

        String uid = "species:test";
        when(species.getUid()).thenReturn(uid);
        when(beeRegistry.getFullTemplate(uid)).thenReturn(template);

        Genome genome = beeGeneticService.getGenome(species);
        for (int i = 0; i < 2; i++) {
            Chromosome chromosome = genome.getChromosomes()[i];
            assertEquals(template[i], chromosome.getPrimaryAllele());
            assertEquals(template[i], chromosome.getSecondaryAllele());
        }
    }

    @Test
    void testUpdateItemGenome() {
        Genome genome = mock(Genome.class);

        when(genomeParser.serialize(genome)).thenReturn("serialized");
        beeGeneticService.updateItemGenome(princess.getItem(), genome);

        verify(beeTypeService).setItemData(princess.getItem(), "serialized");
    }

    @Test
    void testAlterItemGenomeOtherItem() {
        ItemStack item = new ItemStack(Material.COBBLESTONE);

        assertNull(beeGeneticService.alterItemGenome(item, ChromosomeType.FERTILITY, "test", true, true));
    }

}
