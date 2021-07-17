package cz.martinbrom.slimybees.core.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeMutationDTO;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;
import cz.martinbrom.slimybees.setup.BeeSetup;
import cz.martinbrom.slimybees.setup.SpeciesUids;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

import static cz.martinbrom.slimybees.core.category.BeeAtlasDetailCategory.CHANCE_ITEM_SLOT;
import static cz.martinbrom.slimybees.core.category.BeeAtlasDetailCategory.FIRST_PARENT_SLOT;
import static cz.martinbrom.slimybees.core.category.BeeAtlasDetailCategory.PRODUCT_SLOTS;
import static cz.martinbrom.slimybees.core.category.BeeAtlasDetailCategory.SECOND_PARENT_SLOT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParametersAreNonnullByDefault
public class BeeAtlasDetailCategoryTest {

    private static ServerMock server;
    private static MockedStatic<SlimyBeesPlayerProfile> sbppStaticMock;

    private BeeAtlasDetailCategory detailCategory;

    private AlleleSpecies species;
    private Player p;
    private SlimyBeesPlayerProfile sbProfile;
    private ChestMenu menu;

    @Spy
    private BeeLoreService loreService;

    @Mock
    private BeeRegistry beeRegistry;

    @Mock
    private BeeGeneticService geneticService;

    @Mock
    private AlleleRegistry alleleRegistry;

    @Mock
    private BeeAtlasNavigationService navigationService;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();

        // load Slimefun and SlimyBees
        MockBukkit.load(SlimefunPlugin.class);
        MockBukkit.load(SlimyBeesPlugin.class);

        sbppStaticMock = mockStatic(SlimyBeesPlayerProfile.class);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        species = spy(registerSpecies("species:test", "TEST"));

        BeeAtlasCategoryFactory factory = new BeeAtlasCategoryFactory(loreService, beeRegistry, geneticService,
                alleleRegistry, navigationService);
        detailCategory = spy(factory.createDetail(species));
        menu = new ChestMenu("test");
        when(detailCategory.createMenu(anyString())).thenReturn(menu);

        p = server.addPlayer();

        sbProfile = mock(SlimyBeesPlayerProfile.class);
        when(SlimyBeesPlayerProfile.get(p)).thenReturn(sbProfile);
        when(sbProfile.hasDiscovered(species)).thenReturn(true);
        when(beeRegistry.isAlwaysDisplayed(species)).thenReturn(true);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();

        sbppStaticMock.close();
    }

    @Test
    public void testRedirectsToMainMenuInCheatMode() throws InterruptedException {
        PlayerProfile profile = openMenu(SlimefunGuideMode.CHEAT_MODE);
        verify(navigationService).openMainMenu(profile, SlimefunGuideMode.CHEAT_MODE);
    }

    @Test
    public void testRedirectsToMainMenuIfUndiscoveredAndNotAlwaysDisplayed() throws InterruptedException {
        when(sbProfile.hasDiscovered(species)).thenReturn(false);
        when(beeRegistry.isAlwaysDisplayed(species)).thenReturn(false);

        PlayerProfile profile = openMenu(SlimefunGuideMode.SURVIVAL_MODE);
        verify(navigationService).openMainMenu(profile, SlimefunGuideMode.SURVIVAL_MODE);
    }

    @ParameterizedTest
    @MethodSource("getMainMenuNoRedirectConditionBooleans")
    public void testNoRedirectIfUndiscoveredAndOrAlwaysDisplayed(boolean discovered, boolean displayed) throws InterruptedException {
        when(sbProfile.hasDiscovered(species)).thenReturn(discovered);
        when(beeRegistry.isAlwaysDisplayed(species)).thenReturn(displayed);
        mockMutationsReturned(0);

        PlayerProfile profile = openMenu(SlimefunGuideMode.SURVIVAL_MODE);
        verify(navigationService, never()).openMainMenu(profile, SlimefunGuideMode.SURVIVAL_MODE);
    }

    @Test
    public void testBeeDetailIsDisplayed() throws InterruptedException {
        mockMutationsReturned(0);

        openMenu(SlimefunGuideMode.SURVIVAL_MODE);

        ItemStack detailItem = menu.getItemInSlot(BeeAtlasDetailCategory.DETAIL_BEE_SLOT);
        assertDisplayName(species.getDisplayName() + " Bee", detailItem);
    }

    @Test
    public void testObtainSectionNesting() throws InterruptedException {
        mockMutationsReturned(0);

        openMenu(SlimefunGuideMode.SURVIVAL_MODE);

        ItemStack background = ChestMenuUtils.getBackground();
        assertEquals(background, menu.getItemInSlot(FIRST_PARENT_SLOT));
        assertEquals(background, menu.getItemInSlot(SECOND_PARENT_SLOT));
        assertEquals(BeeAtlasDetailCategory.OBTAINED_NEST_ITEM, menu.getItemInSlot(CHANCE_ITEM_SLOT));
    }

    @Test
    public void testObtainSectionOneMutation() throws InterruptedException {
        mockForObtainSection(true);

        assertDisplayName("First Bee", menu.getItemInSlot(FIRST_PARENT_SLOT));
        assertDisplayName("Second Bee", menu.getItemInSlot(SECOND_PARENT_SLOT));
        assertDisplayName(detailCategory.createChanceText(0.5), menu.getItemInSlot(CHANCE_ITEM_SLOT));
    }

    @Test
    public void testObtainSectionOneMutationOneUndiscovered() throws InterruptedException {
        mockForObtainSection(false);

        assertDisplayName("First Bee", menu.getItemInSlot(FIRST_PARENT_SLOT));
        assertEquals(BeeAtlasDetailCategory.UNDISCOVERED_SPECIES_ITEM, menu.getItemInSlot(SECOND_PARENT_SLOT));
        assertEquals(BeeAtlasDetailCategory.UNDISCOVERED_CHANCE_ITEM, menu.getItemInSlot(CHANCE_ITEM_SLOT));
    }

    private void mockForObtainSection(boolean secondDiscovered) throws InterruptedException {
        String firstUid = "species:first";
        String secondUid = "species:second";
        String childUid = "species:child";
        double mutationChance = 0.5;

        BeeMutationDTO mutation = new BeeMutationDTO(firstUid, secondUid, childUid, mutationChance);
        when(beeRegistry.getMutationForChild(anyString())).thenReturn(Collections.singletonList(mutation));

        AlleleSpecies firstSpecies = registerSpecies(firstUid, "FIRST");
        AlleleSpecies secondSpecies = registerSpecies(secondUid, "SECOND");

        when(sbProfile.hasDiscovered(firstSpecies)).thenReturn(true);
        when(sbProfile.hasDiscovered(secondSpecies)).thenReturn(secondDiscovered);

        openMenu(SlimefunGuideMode.SURVIVAL_MODE);
    }

    @Test
    public void testObtainSectionCommonSpecies() throws InterruptedException {
        when(species.getUid()).thenReturn(SpeciesUids.COMMON);
        mockMutationsReturned(2);

        openMenu(SlimefunGuideMode.SURVIVAL_MODE);

        assertDisplayName(ChatColor.GRAY + "Any Nest Bee", menu.getItemInSlot(FIRST_PARENT_SLOT));
        assertDisplayName(ChatColor.GRAY + "Any Other Nest Bee", menu.getItemInSlot(SECOND_PARENT_SLOT));

        ItemStack chanceItem = menu.getItemInSlot(CHANCE_ITEM_SLOT);
        assertDisplayName(detailCategory.createChanceText(BeeSetup.COMMON_MUTATION_CHANCE), chanceItem);
    }

    @Test
    public void testObtainSectionCultivatedSpecies() throws InterruptedException {
        when(species.getUid()).thenReturn(SpeciesUids.CULTIVATED);
        mockMutationsReturned(2);

        AlleleSpecies commonSpecies = registerSpecies(SpeciesUids.COMMON, "COMMON");
        when(alleleRegistry.get(ChromosomeType.SPECIES, SpeciesUids.COMMON)).thenReturn(commonSpecies);
        when(sbProfile.hasDiscovered(argThat(s -> s.getUid().equals(SpeciesUids.COMMON)))).thenReturn(true);

        openMenu(SlimefunGuideMode.SURVIVAL_MODE);

        assertDisplayName("Common Bee", menu.getItemInSlot(FIRST_PARENT_SLOT));
        assertDisplayName(ChatColor.GRAY + "Any Nest Bee", menu.getItemInSlot(SECOND_PARENT_SLOT));

        ItemStack chanceItem = menu.getItemInSlot(CHANCE_ITEM_SLOT);
        assertDisplayName(detailCategory.createChanceText(BeeSetup.CULTIVATED_MUTATION_CHANCE), chanceItem);
    }

    @Test
    public void testObtainSectionMultipleMutations() throws InterruptedException {
        mockMutationsReturned(2);

        openMenu(SlimefunGuideMode.SURVIVAL_MODE);

        ItemStack background = ChestMenuUtils.getBackground();
        assertEquals(background, menu.getItemInSlot(FIRST_PARENT_SLOT));
        assertEquals(background, menu.getItemInSlot(SECOND_PARENT_SLOT));
    }

    @Test
    public void testProductsSectionNoProducts() throws InterruptedException {
        when(species.getProducts()).thenReturn(Collections.emptyList());

        openMenu(SlimefunGuideMode.SURVIVAL_MODE);

        for (int slot : PRODUCT_SLOTS) {
            assertNull(menu.getItemInSlot(slot));
        }
    }

    @Test
    public void testProductsSectionTooManyProducts() {
        List<ChanceItemStack> products = new ArrayList<>();
        for (int i = 0; i < PRODUCT_SLOTS.length + 1; i++) {
            products.add(new ChanceItemStack(new ItemStack(Material.COBBLESTONE), 0.1 * i));
        }

        when(species.getProducts()).thenReturn(products);

        assertDoesNotThrow(() -> openMenu(SlimefunGuideMode.SURVIVAL_MODE));
        for (int i = 0; i < PRODUCT_SLOTS.length; i++) {
            int slot = PRODUCT_SLOTS[i];
            ItemStack item = menu.getItemInSlot(slot);

            assertNotNull(item);
            assertNotNull(item.getItemMeta());
            assertNotNull(item.getItemMeta().getLore());
            assertEquals("", item.getItemMeta().getLore().get(0));
            assertEquals(detailCategory.createChanceText(products.get(i).getChance()), item.getItemMeta().getLore().get(1));
        }
    }

    @Nonnull
    private PlayerProfile openMenu(SlimefunGuideMode mode) throws InterruptedException {
        PlayerProfile profile = awaitProfile(p);
        detailCategory.open(p, profile, mode);

        return profile;
    }

    @Nonnull
    private PlayerProfile awaitProfile(OfflinePlayer player) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<PlayerProfile> ref = new AtomicReference<>();

        // This loads the profile asynchronously
        Assertions.assertFalse(PlayerProfile.get(player, profile -> {
            ref.set(profile);
            latch.countDown();
        }));

        latch.await(2, TimeUnit.SECONDS);
        return ref.get();
    }

    private void mockMutationsReturned(int count) {
        List<BeeMutationDTO> mutations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            mutations.add(mock(BeeMutationDTO.class));
        }

        when(beeRegistry.getMutationForChild(anyString())).thenReturn(mutations);
    }

    private AlleleSpecies registerSpecies(String uid, String name) {
        AlleleSpecies species = new AlleleSpecies(uid, name, false);
        ItemStack droneItem = new CustomItem(SlimyBeesHeadTexture.DRONE.getAsItemStack(), species.getDisplayName() + " Drone");
        species.setDroneItemStack(droneItem);

        when(alleleRegistry.get(ChromosomeType.SPECIES, uid)).thenReturn(species);

        return species;
    }

    private void assertDisplayName(String expected, @Nullable ItemStack item) {
        assertNotNull(item);
        assertNotNull(item.getItemMeta());
        assertEquals(expected, item.getItemMeta().getDisplayName());
    }

    private static Stream<Arguments> getMainMenuNoRedirectConditionBooleans() {
        return Stream.of(Arguments.of(true, false), Arguments.of(true, true), Arguments.of(false, true));
    }

}
