package cz.martinbrom.slimybees.core;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.categories.FlexCategory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

// impl mostly copied from Slimefun4 MultiCategory
@ParametersAreNonnullByDefault
public class BeeFlexCategory extends FlexCategory {

    private static final int CATEGORY_SIZE = 36;

    private static final ItemStack NOT_DISCOVERED_ITEM = new CustomItem(Material.BARRIER, ChatColor.GRAY + "Undiscovered Species");

    public BeeFlexCategory(NamespacedKey key, ItemStack item) {
        super(key, item);
    }

    @Override
    public boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        return true;
    }

    @Override
    public void open(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        openGuide(p, profile, layout, 1);
    }

    private void openGuide(Player p, PlayerProfile profile, SlimefunGuideMode layout, int page) {
        if (layout == SlimefunGuideMode.SURVIVAL_MODE) {
            profile.getGuideHistory().add(this, page);
        }

        ChestMenu menu = new ChestMenu("Bee Atlas");
        SurvivalSlimefunGuide guide = (SurvivalSlimefunGuide) SlimefunPlugin.getRegistry().getSlimefunGuide(layout);

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), guide.getSound(), 1, 1));
        guide.createHeader(p, profile, menu);

        // remove the search
        menu.addItem(7, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());

        menu.addItem(1, new CustomItem(ChestMenuUtils.getBackButton(p, "", ChatColor.GRAY + SlimefunPlugin.getLocalization().getMessage(p, "guide.back.guide"))));
        menu.addMenuClickHandler(1, (pl, s, is, action) -> {
            SlimefunGuide.openMainMenu(profile, layout, 1);
            return false;
        });

        AlleleRegistry alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();
        List<AlleleSpecies> allSpecies = alleleRegistry.getAllSpecies();

        SlimyBeesPlayerProfile sbProfile = SlimyBeesPlayerProfile.get(p);

        int index = 9;
        int target = (CATEGORY_SIZE * (page - 1)) - 1;

        while (target < (allSpecies.size() - 1) && index < CATEGORY_SIZE + 9) {
            target++;

            AlleleSpecies species = allSpecies.get(target);
            if (layout == SlimefunGuideMode.SURVIVAL_MODE) {
                if (sbProfile.hasDiscovered(species)) {
                    menu.addItem(index, species.getAnalyzedItemStack());
                    menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                        SlimefunGuide.openCategory(profile, new BeeDetailFlexCategory(species), layout, 1);
                        return false;
                    });
                } else {
                    menu.addItem(index, NOT_DISCOVERED_ITEM);
                    menu.addMenuClickHandler(index, ChestMenuUtils.getEmptyClickHandler());
                }
            } else {
                menu.addItem(index, species.getAnalyzedItemStack());
                menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                    pl.getInventory().addItem(species.getAnalyzedItemStack().clone());
                    return false;
                });
            }

            index++;
        }

        int pages = target == allSpecies.size() - 1 ? page : (allSpecies.size() - 1) / CATEGORY_SIZE + 1;

        menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            int next = page - 1;

            if (next != page && next > 0) {
                openGuide(p, profile, layout, next);
            }

            return false;
        });

        menu.addItem(52, ChestMenuUtils.getNextButton(p, page, pages));
        menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
            int next = page + 1;

            if (next != page && next <= pages) {
                openGuide(p, profile, layout, next);
            }

            return false;
        });

        menu.open(p);
    }

}
