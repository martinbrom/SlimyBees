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
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

import static cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType.CHROMOSOME_COUNT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BeeRegistryTest {

    private static BeeRegistry beeRegistry;

    @Mock
    private Config config;

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

        beeRegistry = new BeeRegistry(config);

        when(config.getBoolean(anyString())).thenReturn(false);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testRegisterPartialTemplate() {
        Allele[] partialTemplate = createPartialTemplate("species.test");

        beeRegistry.registerPartialTemplate(partialTemplate);
        verify(partialTemplate[ChromosomeType.SPECIES.ordinal()]).getUid();
    }

    @Test
    void testRegisterPartialTemplateMissingSpecies() {
        Allele[] partialTemplate = new Allele[CHROMOSOME_COUNT];

        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerPartialTemplate(partialTemplate));
    }

    @Test
    void testRegisterPartialTemplateNotEnoughChromosomes() {
        Allele[] partialTemplate = new Allele[CHROMOSOME_COUNT - 1];

        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerPartialTemplate(partialTemplate));
    }

    @Test
    void testGetPartialTemplate() {
        String speciesUid = "species.test";
        Allele[] partialTemplate = createPartialTemplate(speciesUid);

        beeRegistry.registerPartialTemplate(partialTemplate);

        Allele[] loadedTemplate = beeRegistry.getPartialTemplate(speciesUid);
        assertNotNull(loadedTemplate);
        assertEquals(speciesUid, loadedTemplate[ChromosomeType.SPECIES.ordinal()].getUid());
        assertNull(loadedTemplate[ChromosomeType.FERTILITY.ordinal()]);
    }

    @Test
    void testGetPartialTemplateMissingSpecies() {
        assertNull(beeRegistry.getPartialTemplate("species.missing"));
    }

    @Test
    void testRegisterDefaultTemplate() {
        assertDoesNotThrow(() -> beeRegistry.registerDefaultTemplate(createFullTemplate()));
    }

    @Test
    void testRegisterDefaultTemplateMissingSpecies() {
        Allele[] template = createFullTemplate();
        template[ChromosomeType.SPECIES.ordinal()] = null;

        assertDoesNotThrow(() -> beeRegistry.registerDefaultTemplate(template));
    }

    @ParameterizedTest
    @MethodSource("getChromosomeTypesWithoutSpecies")
    void testRegisterDefaultTemplateMissingChromosome(ChromosomeType type) {
        Allele[] template = createFullTemplate();
        template[type.ordinal()] = null;

        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerDefaultTemplate(template));
    }

    @Test
    void testRegisterDefaultTemplateNotEnoughChromosomes() {
        Allele[] template = new Allele[CHROMOSOME_COUNT - 1];

        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerDefaultTemplate(template));
    }

    @Test
    void testGetAllele() {
        String speciesUid = "species.test";
        String productivityUid = "productivity.test";
        String lifespanUid = "lifespan.test";
        Allele[] partialTemplate = createPartialTemplate(speciesUid);
        Allele[] defaultTemplate = createFullTemplate();

        Allele productivity = mockAllele(productivityUid);
        Allele lifespan = mockAllele(lifespanUid);
        partialTemplate[ChromosomeType.PRODUCTIVITY.ordinal()] = productivity;
        defaultTemplate[ChromosomeType.LIFESPAN.ordinal()] = lifespan;

        beeRegistry.registerPartialTemplate(partialTemplate);
        beeRegistry.registerDefaultTemplate(defaultTemplate);

        assertEquals(speciesUid, beeRegistry.getAllele(ChromosomeType.SPECIES, speciesUid).getUid());
        assertEquals(productivity, beeRegistry.getAllele(ChromosomeType.PRODUCTIVITY, speciesUid));
        assertEquals(lifespan, beeRegistry.getAllele(ChromosomeType.LIFESPAN, speciesUid));
    }

    private static Allele[] createPartialTemplate(String speciesUid) {
        Allele[] template = new Allele[CHROMOSOME_COUNT];
        Allele species = mockAllele(speciesUid);
        template[ChromosomeType.SPECIES.ordinal()] = species;

        return template;
    }

    private static Allele mockAllele(String uid) {
        Allele allele = mock(Allele.class);
        when(allele.getUid()).thenReturn(uid);

        return allele;
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
