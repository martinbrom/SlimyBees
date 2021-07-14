package cz.martinbrom.slimybees.core;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import be.seeseemelk.mockbukkit.MockBukkit;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleService;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

import static cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType.CHROMOSOME_COUNT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
        Allele[] partialTemplate = createPartialTemplate("species.test");

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
        Allele[] partialTemplate = createPartialTemplate(speciesUid);

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
    public void testRegisterDefaultTemplate() {
        assertDoesNotThrow(() -> beeRegistry.registerDefaultTemplate(createFullTemplate()));
    }

    @Test
    public void testRegisterDefaultTemplateMissingSpecies() {
        Allele[] template = createFullTemplate();
        template[ChromosomeType.SPECIES.ordinal()] = null;

        assertDoesNotThrow(() -> beeRegistry.registerDefaultTemplate(template));
    }

    @ParameterizedTest
    @MethodSource("getChromosomeTypesWithoutSpecies")
    public void testRegisterDefaultTemplateMissingChromosome(ChromosomeType type) {
        Allele[] template = createFullTemplate();
        template[type.ordinal()] = null;

        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerDefaultTemplate(template));
    }

    @Test
    public void testRegisterDefaultTemplateNotEnoughChromosomes() {
        Allele[] template = new Allele[CHROMOSOME_COUNT - 1];

        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerDefaultTemplate(template));
    }

    @Test
    public void testGetAllele() {
        String speciesUid = "species.test";
        String productivityUid = "productivity.test";
        Allele[] partialTemplate = createPartialTemplate(speciesUid);

        Allele productivity = mock(Allele.class);
        partialTemplate[ChromosomeType.PRODUCTIVITY.ordinal()] = productivity;
        when(productivity.getUid()).thenReturn(productivityUid);

        beeRegistry.registerPartialTemplate(partialTemplate);

        assertEquals(speciesUid, beeRegistry.getAllele(ChromosomeType.SPECIES, speciesUid).getUid());
        assertEquals(productivity, beeRegistry.getAllele(ChromosomeType.PRODUCTIVITY, speciesUid));
        // TODO: 14.07.21 Testing the default template (which is basically impl not core)
//        assertEquals(BeeRegistry.DEFAULT_LIFESPAN_UID, beeRegistry.getAllele(ChromosomeType.LIFESPAN, speciesUid).getUid());
    }

    private static Allele[] createPartialTemplate(String speciesUid) {
        Allele[] template = new Allele[CHROMOSOME_COUNT];
        Allele species = mock(Allele.class);
        template[ChromosomeType.SPECIES.ordinal()] = species;

        when(species.getUid()).thenReturn(speciesUid);

        return template;
    }

    private static Allele[] createFullTemplate() {
        Allele[] template = new Allele[CHROMOSOME_COUNT];
        Allele allele = mock(Allele.class);
        for (int i = 0; i < CHROMOSOME_COUNT; i++) {
            template[i] = allele;
        }

        return template;
    }

    private static Stream<Arguments> getChromosomeTypesWithoutSpecies() {
        return Arrays.stream(ChromosomeType.values())
                .filter(type -> type != ChromosomeType.SPECIES)
                .map(Arguments::of);
    }

}
