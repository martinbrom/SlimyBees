package cz.martinbrom.slimybees.core.category;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    protected static final ItemStack UNKNOWN_SPECIES_ITEM = new CustomItem(Material.BARRIER, ChatColor.GRAY + "Undiscovered Species");
    protected static final ItemStack UNKNOWN_CHANCE_ITEM = new CustomItem(Material.BARRIER, ChatColor.GRAY + "Unknown Chance");

    public BaseFlexCategory(NamespacedKey key, ItemStack item) {
        super(key, item);
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
