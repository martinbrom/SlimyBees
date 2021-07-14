package cz.martinbrom.slimybees.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.seeseemelk.mockbukkit.MockBukkit;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleService;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

import static cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType.CHROMOSOME_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BeeRegistryTest {

    private static BeeRegistry beeRegistry;

    @Mock
    private AlleleService alleleService;

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

        beeRegistry = new BeeRegistry(alleleService);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    public void testRegisterPartialTemplate() {
        Allele[] partialTemplate = mockPartialTemplate("species.test");

        beeRegistry.registerPartialTemplate(partialTemplate);
        verify(partialTemplate[ChromosomeType.SPECIES.ordinal()]).getUid();
    }

    @Test
    public void testRegisterPartialTemplateMissingSpecies() {
        Allele[] partialTemplate = new Allele[CHROMOSOME_COUNT];

        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerPartialTemplate(partialTemplate));
    }

    @Test
    public void testRegisterPartialTemplateNotEnoughChromosomes() {
        Allele[] partialTemplate = new Allele[CHROMOSOME_COUNT - 1];

        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerPartialTemplate(partialTemplate));
    }

    @Test
    public void testGetPartialTemplate() {
        String speciesUid = "species.test";
        Allele[] partialTemplate = mockPartialTemplate(speciesUid);

        beeRegistry.registerPartialTemplate(partialTemplate);

        Allele[] loadedTemplate = beeRegistry.getPartialTemplate(speciesUid);
        assertNotNull(loadedTemplate);
        assertEquals(speciesUid, loadedTemplate[ChromosomeType.SPECIES.ordinal()].getUid());
        assertNull(loadedTemplate[ChromosomeType.FERTILITY.ordinal()]);
    }

    @Test
    public void testGetPartialTemplateMissingSpecies() {
        assertNull(beeRegistry.getPartialTemplate("species.missing"));
    }

    @Test
    public void testGetFullTemplate() {
        // TODO: 14.07.21 Testing the default template (which is basically impl not core)
    }

    @Test
    public void testGetFullTemplateMissingSpecies() {
        // TODO: 14.07.21 Testing the default template (which is basically impl not core)
    }

    @Test
    public void testGetAllele() {
        String speciesUid = "species.test";
        String productivityUid = "productivity.test";
        Allele[] partialTemplate = mockPartialTemplate(speciesUid);

        Allele productivity = mock(Allele.class);
        partialTemplate[ChromosomeType.PRODUCTIVITY.ordinal()] = productivity;
        when(productivity.getUid()).thenReturn(productivityUid);

        beeRegistry.registerPartialTemplate(partialTemplate);

        assertEquals(speciesUid, beeRegistry.getAllele(ChromosomeType.SPECIES, speciesUid).getUid());
        assertEquals(productivity, beeRegistry.getAllele(ChromosomeType.PRODUCTIVITY, speciesUid));
        // TODO: 14.07.21 Testing the default template (which is basically impl not core)
//        assertEquals(BeeRegistry.DEFAULT_LIFESPAN_UID, beeRegistry.getAllele(ChromosomeType.LIFESPAN, speciesUid).getUid());
    }

    private Allele[] mockPartialTemplate(String speciesUid) {
        Allele[] template = new Allele[CHROMOSOME_COUNT];
        Allele species = mock(Allele.class);
        template[ChromosomeType.SPECIES.ordinal()] = species;

        when(species.getUid()).thenReturn(speciesUid);

        return template;
    }

}
