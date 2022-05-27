package cz.martinbrom.slimybees.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;

import static cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType.CHROMOSOME_COUNT;
import static cz.martinbrom.slimybees.test.TestUtils.mockAllele;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        MockBukkit.load(Slimefun.class);
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

    @ParameterizedTest
    @MethodSource("getChromosomeTypesWithoutSpecies")
    void testGetFullTemplate(ChromosomeType type) {
        String defaultUid = "allele:default";
        Allele[] defaultTemplate = new Allele[CHROMOSOME_COUNT];
        for (int i = 1; i < CHROMOSOME_COUNT; i++) {
            defaultTemplate[i] = mockAllele(defaultUid);
        }

        String speciesUid = "species:test";
        String partialUid = "allele:partial";
        Allele[] partialTemplate = createPartialTemplate(speciesUid);
        partialTemplate[type.ordinal()] = mockAllele(partialUid);

        beeRegistry.registerDefaultTemplate(defaultTemplate);
        beeRegistry.registerPartialTemplate(partialTemplate);

        Allele[] fullTemplate = beeRegistry.getFullTemplate(speciesUid);
        for (int i = 1; i < CHROMOSOME_COUNT; i++) {
            if (i == type.ordinal()) {
                assertEquals(partialUid, fullTemplate[i].getUid());
            } else {
                assertEquals(defaultUid, fullTemplate[i].getUid());
            }
        }
    }

    @Test
    void testRegisterMutationRequiresUnique() {
        BeeMutationDTO mutation = new BeeMutationDTO("species:first", "species:second", "species:child", 0.5);

        beeRegistry.registerMutation(mutation);
        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerMutation(mutation));
    }

    @Test
    void testRegisterMutationRequiresUniqueDifferentChance() {
        String firstUid = "species:first";
        String secondUid = "species:second";
        String childUid = "species:child";
        BeeMutationDTO first = new BeeMutationDTO(firstUid, secondUid, childUid, 0.5);
        BeeMutationDTO second = new BeeMutationDTO(firstUid, secondUid, childUid, 0.75);
        BeeMutationDTO swapped = new BeeMutationDTO(secondUid, firstUid, childUid, 0.75);

        beeRegistry.registerMutation(first);
        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerMutation(second));
        assertThrows(IllegalArgumentException.class, () -> beeRegistry.registerMutation(swapped));
    }

    @Test
    void testRegisterMutationSameParentsDifferentChild() {
        String firstParentUid = "species.first";
        String secondParentUid = "species.second";

        BeeMutationDTO first = new BeeMutationDTO(firstParentUid, secondParentUid, "species:child_first", 0.5);
        BeeMutationDTO second = new BeeMutationDTO(firstParentUid, secondParentUid, "species:child_second", 0.5);

        beeRegistry.registerMutation(first);
        assertDoesNotThrow(() -> beeRegistry.registerMutation(second));
    }

    @Test
    void testIsAlwaysDisplayedConfigFalse() {
        AlleleSpecies firstSpecies = new AlleleSpecies("species:first", "FIRST", false);
        AlleleSpecies secondSpecies = new AlleleSpecies("species:second", "SECOND", false);
        beeRegistry.registerAlwaysDisplayedSpecies(secondSpecies);

        assertFalse(beeRegistry.isAlwaysDisplayed(firstSpecies));
        assertFalse(beeRegistry.isAlwaysDisplayed(secondSpecies));
    }

    @Test
    void testGetMutationsForParentsNoMutations() {
        AlleleSpecies firstSpecies = new AlleleSpecies("species:first", "FIRST", false);
        AlleleSpecies secondSpecies = new AlleleSpecies("species:second", "SECOND", false);

        assertEquals(Collections.emptyList(), beeRegistry.getMutationsForParents(firstSpecies, secondSpecies));
    }

    @Test
    void testGetMutationsForParentsIgnoresOrder() {
        String firstUid = "species:first";
        String secondUid = "species:second";
        AlleleSpecies firstSpecies = new AlleleSpecies(firstUid, "FIRST", false);
        AlleleSpecies secondSpecies = new AlleleSpecies(secondUid, "SECOND", false);

        BeeMutationDTO expected = new BeeMutationDTO(firstUid, secondUid, "species:child", 0.5);
        beeRegistry.registerMutation(expected);

        List<BeeMutationDTO> expectedList = Collections.singletonList(expected);
        assertEquals(expectedList, beeRegistry.getMutationsForParents(firstSpecies, secondSpecies));
        assertEquals(expectedList, beeRegistry.getMutationsForParents(secondSpecies, firstSpecies));
    }

    @Test
    void testGetMutationsForChildNoMutations() {
        BeeMutationDTO mutation = new BeeMutationDTO("species:first", "species:second", "species:child_other", 0.5);

        assertEquals(Collections.emptyList(), beeRegistry.getMutationsForChild("species.child"));
        beeRegistry.registerMutation(mutation);
        assertEquals(Collections.emptyList(), beeRegistry.getMutationsForChild("species.child"));
    }

    @Test
    void testGetMutationsForChild() {
        String childUid = "species:child";
        BeeMutationDTO firstMutation = new BeeMutationDTO("species:first", "species:second", childUid, 0.5);
        BeeMutationDTO secondMutation = new BeeMutationDTO("species:second", "species:third", childUid, 0.5);

        beeRegistry.registerMutation(firstMutation);
        beeRegistry.registerMutation(secondMutation);

        assertEquals(Arrays.asList(firstMutation, secondMutation), beeRegistry.getMutationsForChild(childUid));
    }

    private static Allele[] createPartialTemplate(String speciesUid) {
        Allele[] template = new Allele[CHROMOSOME_COUNT];
        template[ChromosomeType.SPECIES.ordinal()] = mockAllele(speciesUid);

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
