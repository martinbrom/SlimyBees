package cz.martinbrom.slimybees.core.category;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

@ParametersAreNonnullByDefault
public abstract class AbstractBeeAtlasCategory extends FlexItemGroup {

    protected static final ItemStack UNDISCOVERED_SPECIES_ITEM = new CustomItemStack(Material.BARRIER, ChatColor.GRAY + "Undiscovered Species");
    protected static final ItemStack UNDISCOVERED_CHANCE_ITEM = new CustomItemStack(Material.BARRIER, ChatColor.GRAY + "Undiscovered Chance");

    protected final BeeLoreService loreService;
    protected final BeeRegistry beeRegistry;
    protected final BeeGeneticService geneticService;
    protected final AlleleRegistry alleleRegistry;
    protected final BeeAtlasNavigationService navigationService;
    protected final BeeAtlasCategoryFactory factory;

    protected AbstractBeeAtlasCategory(BeeLoreService loreService, BeeRegistry beeRegistry, BeeGeneticService geneticService,
                                       AlleleRegistry alleleRegistry, BeeAtlasNavigationService navigationService,
                                       BeeAtlasCategoryFactory factory, String suffix, ItemStack item) {
        super(SlimyBeesPlugin.getKey("slimybees_atlas." + suffix), item);

        this.loreService = loreService;
        this.beeRegistry = beeRegistry;
        this.geneticService = geneticService;
        this.alleleRegistry = alleleRegistry;
        this.navigationService = navigationService;
        this.factory = factory;
    }

    @Override
    public final void open(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        open(p, profile, layout, 1);
    }

    protected final void open(Player p, PlayerProfile profile, SlimefunGuideMode mode, int page) {
        GuideHistory history = profile.getGuideHistory();
        if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
            history.add(this, page);
        }

        String suffix = getTitleSuffix();
        String title = "Bee Atlas" + (suffix == null ? "" : " - " + suffix);
        ChestMenu menu = createMenu(title);

        SurvivalSlimefunGuide guide = (SurvivalSlimefunGuide) Slimefun.getRegistry().getSlimefunGuide(mode);
        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), guide.getSound(), 1, 1));
        guide.createHeader(p, profile, menu);

        // remove the search
        menu.addItem(7, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());

        // custom back button
        ItemStack backButton = new CustomItemStack(ChestMenuUtils.getBackButton(p,
                "", "&fLeft Click: &7Go back to previous Page", "&fShift + Left Click: &7Go back to Main Menu"));
        menu.addItem(1, backButton, (pl, s, i, a) -> {
            if (a.isShiftClicked()) {
                navigationService.openMainMenu(profile, mode);
            } else {
                navigationService.goBack(profile, mode);
            }

            return false;
        });

        // fill background
        for (int slot : getExtraBackgroundSlots()) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        fillMenu(menu, p, profile, mode, page);

        menu.open(p);
    }

    @Nonnull
    protected ChestMenu createMenu(String title) {
        return new ChestMenu(title);
    }

    /**
     * Adds an {@link ItemStack} to given {@link ChestMenu} to given slot.
     * If the given {@link AlleleSpecies} should either be "always visible" or is discovered
     * by given {@link SlimyBeesPlayerProfile}, a link to its respective bee detail page
     * is created and added to the {@link ChestMenu}.
     * Otherwise an {@link ItemStack} representing an "undiscovered" species is added.
     *
     * @param menu The {@link ChestMenu} to add the link to
     * @param slot The slot to add the link to
     * @param species The {@link AlleleSpecies} the link should point to
     * @param profile The {@link PlayerProfile} containing guide history
     * @param sbProfile The {@link SlimyBeesPlayerProfile} containing information about player's discoveries
     * @return True if the link was added, false if the bee was undiscovered.
     */
    protected boolean addBeeDetailLink(ChestMenu menu, int slot, @Nullable AlleleSpecies species, PlayerProfile profile,
                                       SlimyBeesPlayerProfile sbProfile) {
        // bee should be displayed -> we add the item and a link to the BeeDetail page
        if (species != null && (beeRegistry.isAlwaysDisplayed(species) || sbProfile.hasDiscovered(species))) {
            addBeeItem(menu, slot, species, (pl, clickedSlot, item, action) -> {
                navigationService.openDetailPage(profile, species, factory);
                return false;
            });
            return true;
        }

        // otherwise just add the undiscovered bee item
        addUndiscoveredBeeItem(menu, slot);
        return false;
    }

    /**
     * Creates a generic drone {@link ItemStack} for given {@link AlleleSpecies},
     * adds it to the {@link ChestMenu} to given slot and adds given {@link ChestMenu.MenuClickHandler}.
     *
     * @param menu The {@link ChestMenu} to add the bee to
     * @param slot The slot to add the bee to
     * @param species The {@link AlleleSpecies} to add
     * @param clickHandler The {@link ChestMenu.MenuClickHandler} to add
     */
    protected void addBeeItem(ChestMenu menu, int slot, AlleleSpecies species, ChestMenu.MenuClickHandler clickHandler) {
        ItemStack droneItemStack = loreService.generify(species.getDroneItemStack());
        menu.addItem(slot, droneItemStack, clickHandler);
    }

    /**
     * Adds an {@link ItemStack} representing an undiscovered species to given {@link ChestMenu}.
     *
     * @param menu The {@link ChestMenu} to add the bee to
     * @param slot The slot to add the bee to
     */
    private void addUndiscoveredBeeItem(ChestMenu menu, int slot) {
        menu.addItem(slot, UNDISCOVERED_SPECIES_ITEM, ChestMenuUtils.getEmptyClickHandler());
    }

    /**
     * Creates a {@link String} containing formatted chance information.
     *
     * @param chance The chance to format, should be between 0 and 1.
     * @return Percent-formatted chance
     */
    protected String createChanceText(double chance) {
        int percentage = (int) Math.ceil(chance * 100);
        return ChatColor.WHITE + "Chance: " + ChatColor.GRAY + percentage + "%";
    }

    @Nullable
    protected String getTitleSuffix() {
        return null;
    }

    @Nonnull
    protected int[] getExtraBackgroundSlots() {
        return new int[0];
    }

    protected abstract void fillMenu(ChestMenu menu, Player p, PlayerProfile profile, SlimefunGuideMode layout, int page);

}
