package cz.martinbrom.slimybees.core.category;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryMock;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

import static cz.martinbrom.slimybees.test.TestUtils.assertDisplayName;
import static cz.martinbrom.slimybees.test.TestUtils.awaitProfile;
import static cz.martinbrom.slimybees.test.TestUtils.registerSpecies;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ParametersAreNonnullByDefault
public class BeeAtlasListCategoryTest {

    public static final ClickAction RIGHT_CLICK = new ClickAction(true, false);
    public static final ClickAction LEFT_CLICK = new ClickAction(false, false);

    private static ServerMock server;
    private static MockedStatic<SlimyBeesPlayerProfile> sbppStaticMock;

    private BeeAtlasListCategory listCategory;

    private PlayerMock p;
    private SlimyBeesPlayerProfile sbProfile;
    private ChestMenu menu;

    private BeeAtlasCategoryFactory factory;

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

        factory = spy(new BeeAtlasCategoryFactory(loreService, beeRegistry, geneticService,
                alleleRegistry, navigationService));
        listCategory = spy(factory.createList(new ItemStack(Material.COBBLESTONE)));
        menu = new ChestMenu("test");
        when(listCategory.createMenu(anyString())).thenReturn(menu);

        p = server.addPlayer();

        sbProfile = mock(SlimyBeesPlayerProfile.class);
        when(SlimyBeesPlayerProfile.get(p)).thenReturn(sbProfile);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();

        sbppStaticMock.close();
    }

    @Test
    void testShouldAlwaysBeVisibleInMainMenu() throws InterruptedException {
        PlayerProfile profile = awaitProfile(p);

        assertTrue(listCategory.isVisible(p, profile, SlimefunGuideMode.SURVIVAL_MODE));
        assertTrue(listCategory.isVisible(p, profile, SlimefunGuideMode.CHEAT_MODE));
    }

    @Test
    void testSpeciesShownConditions() throws InterruptedException {
        PlayerProfile profile = awaitProfile(p);

        AlleleSpecies discovered = registerSpecies(alleleRegistry, "species:discovered", "DISCOVERED");
        AlleleSpecies displayed = registerSpecies(alleleRegistry, "species:displayed", "DISPLAYED");
        AlleleSpecies both = registerSpecies(alleleRegistry, "species:both", "BOTH");
        AlleleSpecies neither = registerSpecies(alleleRegistry, "species:neither", "NEITHER");
        when(alleleRegistry.getAllSpecies()).thenReturn(Arrays.asList(discovered, displayed, both, neither));

        when(sbProfile.hasDiscovered(discovered)).thenReturn(true);
        when(sbProfile.hasDiscovered(both)).thenReturn(true);
        when(beeRegistry.isAlwaysDisplayed(displayed)).thenReturn(true);
        when(beeRegistry.isAlwaysDisplayed(both)).thenReturn(true);

        listCategory.open(p, profile, SlimefunGuideMode.SURVIVAL_MODE);

        assertDisplayName("Discovered Bee", menu.getItemInSlot(9));
        assertDisplayName("Displayed Bee", menu.getItemInSlot(10));
        assertDisplayName("Both Bee", menu.getItemInSlot(11));
        assertEquals(AbstractBeeAtlasCategory.UNDISCOVERED_SPECIES_ITEM, menu.getItemInSlot(12));
    }

    @Test
    void testClickingUndiscoveredDoesNothing() throws InterruptedException {
        PlayerProfile profile = awaitProfile(p);

        AlleleSpecies species = registerSpecies(alleleRegistry, "species:test", "TEST");
        when(alleleRegistry.getAllSpecies()).thenReturn(Collections.singletonList(species));

        listCategory.open(p, profile, SlimefunGuideMode.SURVIVAL_MODE);

        menu.getMenuClickHandler(9).onClick(p, 9, species.getDroneItemStack(), LEFT_CLICK);
        menu.getMenuClickHandler(9).onClick(p, 9, species.getDroneItemStack(), RIGHT_CLICK);
        verify(navigationService, never()).openDetailPage(profile, species, factory);
    }

    @Test
    void testClickingDiscoveredOpensDetail() throws InterruptedException {
        PlayerProfile profile = awaitProfile(p);

        AlleleSpecies species = registerSpecies(alleleRegistry, "species:test", "TEST");
        when(alleleRegistry.getAllSpecies()).thenReturn(Collections.singletonList(species));
        when(sbProfile.hasDiscovered(species)).thenReturn(true);

        listCategory.open(p, profile, SlimefunGuideMode.SURVIVAL_MODE);

        menu.getMenuClickHandler(9).onClick(p, 9, species.getDroneItemStack(), LEFT_CLICK);
        menu.getMenuClickHandler(9).onClick(p, 9, species.getDroneItemStack(), RIGHT_CLICK);
        verify(navigationService, times(2)).openDetailPage(profile, species, factory);
    }

    @Test
    void testSecretSpeciesNotShown() throws InterruptedException {
        PlayerProfile profile = awaitProfile(p);

        AlleleSpecies normal = registerSpecies(alleleRegistry, "species:normal", "NORMAL");
        AlleleSpecies secret = registerSpecies(alleleRegistry, "species:secret", "SECRET", true);
        AlleleSpecies secretDiscovered = registerSpecies(alleleRegistry, "species:secret_discovered", "SECRET_DISCOVERED", true);
        when(alleleRegistry.getAllSpecies()).thenReturn(Arrays.asList(normal, secret, secretDiscovered));
        when(sbProfile.hasDiscovered(secretDiscovered)).thenReturn(true);

        listCategory.open(p, profile, SlimefunGuideMode.SURVIVAL_MODE);

        assertEquals(AbstractBeeAtlasCategory.UNDISCOVERED_SPECIES_ITEM, menu.getItemInSlot(9));
        assertNull(menu.getItemInSlot(10));
        assertNull(menu.getItemInSlot(11));
    }

    @Test
    void testCheatModeButtons() throws InterruptedException {
        PlayerProfile profile = awaitProfile(p);

        AlleleSpecies species = registerSpecies(alleleRegistry, "species:test", "TEST");
        when(alleleRegistry.getAllSpecies()).thenReturn(Collections.singletonList(species));

        ItemStack princessItem = new CustomItem(SlimyBeesHeadTexture.PRINCESS.getAsItemStack(), species.getDisplayName() + " Princess");
        species.setPrincessItemStack(princessItem);

        listCategory.open(p, profile, SlimefunGuideMode.CHEAT_MODE);

        int firstBeeSlot = 9;
        ItemStack button = menu.getItemInSlot(firstBeeSlot);
        assertDisplayName("Test Bee", button);

        BiFunction<ItemStack, String, Boolean> similarConditionFn = (i, s) -> {
            ItemMeta meta = i.getItemMeta();
            if (meta == null) {
                return false;
            }

            return meta.getDisplayName().equals(s);
        };

        PlayerInventoryMock inv = (PlayerInventoryMock) p.getInventory();
        menu.getMenuClickHandler(firstBeeSlot).onClick(p, firstBeeSlot, button, RIGHT_CLICK);
        inv.assertTrueForSome(i -> similarConditionFn.apply(i, "Test Drone"));

        menu.getMenuClickHandler(firstBeeSlot).onClick(p, firstBeeSlot, button, LEFT_CLICK);
        inv.assertTrueForSome(i -> similarConditionFn.apply(i, "Test Princess"));
    }

}
