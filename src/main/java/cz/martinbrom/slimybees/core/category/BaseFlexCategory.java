package cz.martinbrom.slimybees.core.category;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.categories.FlexCategory;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public abstract class BaseFlexCategory extends FlexCategory {

    protected static final ItemStack UNDISCOVERED_SPECIES_ITEM = new CustomItem(Material.BARRIER, ChatColor.GRAY + "Undiscovered Species");
    protected static final ItemStack UNDISCOVERED_CHANCE_ITEM = new CustomItem(Material.BARRIER, ChatColor.GRAY + "Undiscovered Chance");

    protected final BeeLoreService loreService;
    protected final BeeRegistry beeRegistry;

    protected BaseFlexCategory(NamespacedKey key, ItemStack item) {
        super(key, item);

        loreService = SlimyBeesPlugin.getBeeLoreService();
        beeRegistry = SlimyBeesPlugin.getBeeRegistry();
    }

    @Override
    public final void open(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        open(p, profile, layout, 1);
    }

    protected final void open(Player p, PlayerProfile profile, SlimefunGuideMode layout, int page) {
        GuideHistory history = profile.getGuideHistory();
        if (layout == SlimefunGuideMode.SURVIVAL_MODE) {
            history.add(this, page);
        }

        String suffix = getTitleSuffix();
        String title = "Bee Atlas" + (suffix == null ? "" : " - " + suffix);
        ChestMenu menu = new ChestMenu(title);

        SurvivalSlimefunGuide guide = (SurvivalSlimefunGuide) SlimefunPlugin.getRegistry().getSlimefunGuide(layout);
        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), guide.getSound(), 1, 1));
        guide.createHeader(p, profile, menu);

        // remove the search
        menu.addItem(7, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());

        // custom back button
        ItemStack backButton = new CustomItem(ChestMenuUtils.getBackButton(p,
                "", "&fLeft Click: &7Go back to previous Page", "&fShift + Left Click: &7Go back to Main Menu"));
        menu.addItem(1, backButton, (pl, s, i, a) -> {
            if (a.isShiftClicked()) {
                SlimefunGuide.openMainMenu(profile, layout, history.getMainMenuPage());
            } else {
                history.goBack(guide);
            }

            return false;
        });

        // fill background
        for (int slot : getExtraBackgroundSlots()) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        fillMenu(menu, p, profile, layout, page);

        menu.open(p);
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
                SlimefunGuide.openCategory(profile, new BeeDetailFlexCategory(species), SlimefunGuideMode.SURVIVAL_MODE, 1);
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
