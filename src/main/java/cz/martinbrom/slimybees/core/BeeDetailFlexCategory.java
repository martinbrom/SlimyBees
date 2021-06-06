package cz.martinbrom.slimybees.core;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BeeMutation;
import cz.martinbrom.slimybees.core.genetics.BeeMutationTree;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.utils.GeneticUtil;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.categories.FlexCategory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeDetailFlexCategory extends FlexCategory {

    private final AlleleSpecies species;

    private static final ItemStack NOT_DISCOVERED_ITEM = new CustomItem(Material.BARRIER, ChatColor.GRAY + "Undiscovered Species");

    private static final int[] DETAIL_PAGE_BACKGROUND_SLOTS = new int[] {
            9, 11, 12, 13, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 30, 35,
            36, 39, 44 };
    private static final int[] DETAIL_PAGE_PRODUCT_SLOTS = new int[] { 28, 29, 37, 38 };

    public BeeDetailFlexCategory(AlleleSpecies species) {
        super(SlimyBeesPlugin.getKey("bee_detail." + species.getUid()), species.getAnalyzedItemStack().clone());

        this.species = species;
    }

    @Override
    public boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        return false;
    }

    @Override
    public void open(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        openDetailPage(p, profile, layout);
    }

    private void openDetailPage(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        if (layout == SlimefunGuideMode.SURVIVAL_MODE) {
            profile.getGuideHistory().add(this, 1);
        }
        // TODO: 05.06.21 If not discovered go to the bee list page

        ChestMenu menu = new ChestMenu("Bee Atlas - " + species.getName());

        SurvivalSlimefunGuide guide = (SurvivalSlimefunGuide) SlimefunPlugin.getRegistry().getSlimefunGuide(layout);
        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), guide.getSound(), 1, 1));
        guide.createHeader(p, profile, menu);

        // remove the search
        menu.addItem(7, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());

        // custom back button
        menu.addItem(1, ChestMenuUtils.getBackButton(p), (pl, s, i, a) -> {
            // TODO: 06.06.21 Go back
            profile.getGuideHistory().goBack(guide);
            return false;
        });

        // fill background
        for (int slot : DETAIL_PAGE_BACKGROUND_SLOTS) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        // detail bee
        menu.addItem(10, species.getAnalyzedItemStack(), ChestMenuUtils.getEmptyClickHandler());

        // parents + chance
        menu.addItem(14, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        menu.addItem(15, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());

        BeeMutationTree mutationTree = SlimyBeesPlugin.getBeeRegistry().getBeeMutationTree();
        List<BeeMutation> mutations = mutationTree.getMutationForChild(species.getUid());
        if (mutations == null) {
            menu.addItem(16, new CustomItem(Material.BEE_NEST, ChatColor.DARK_GREEN + "Found naturally in the world"), ChestMenuUtils.getEmptyClickHandler());
        } else if (mutations.size() == 1) {
            BeeMutation mutation = mutations.get(0);
            SlimyBeesPlayerProfile sbProfile = SlimyBeesPlayerProfile.get(p);

            AlleleSpecies firstSpecies = GeneticUtil.getSpeciesByUid(mutation.getFirstParent());
            if (firstSpecies != null && sbProfile.hasDiscovered(firstSpecies)) {
                menu.addItem(14, firstSpecies.getAnalyzedItemStack());
                menu.addMenuClickHandler(14, (pl, slot, item, action) -> {
                    SlimefunGuide.openCategory(profile, new BeeDetailFlexCategory(firstSpecies), layout, 1);
                    return false;
                });
            } else {
                menu.addItem(14, NOT_DISCOVERED_ITEM);
                menu.addMenuClickHandler(14, ChestMenuUtils.getEmptyClickHandler());
            }

//            if (firstSpecies != null) {
//                // TODO: 06.06.21 Add the not discovered item stack when the species is not discovered yet
//                //  and add an empty click handler
//                menu.addItem(14, firstSpecies.getAnalyzedItemStack(), (pl, s, i, a) -> {
//                    SlimefunGuide.openCategory(profile, new BeeDetailFlexCategory(firstSpecies), layout, 1);
//                    return false;
//                });
//            }

            AlleleSpecies secondSpecies = GeneticUtil.getSpeciesByUid(mutation.getSecondParent());
            if (secondSpecies != null && sbProfile.hasDiscovered(secondSpecies)) {
                menu.addItem(15, secondSpecies.getAnalyzedItemStack());
                menu.addMenuClickHandler(15, (pl, slot, item, action) -> {
                    SlimefunGuide.openCategory(profile, new BeeDetailFlexCategory(secondSpecies), layout, 1);
                    return false;
                });
            } else {
                menu.addItem(15, NOT_DISCOVERED_ITEM);
                menu.addMenuClickHandler(15, ChestMenuUtils.getEmptyClickHandler());
            }

//            if (secondSpecies != null) {
//                // TODO: 06.06.21 Add the not discovered item stack when the species is not discovered yet
//                //  and add an empty click handler
//                menu.addItem(15, secondSpecies.getAnalyzedItemStack(), (pl, s, i, a) -> {
//                    SlimefunGuide.openCategory(profile, new BeeDetailFlexCategory(secondSpecies), layout, 1);
//                    return false;
//                });
//            }

            menu.addItem(16, new CustomItem(Material.PAPER, createChanceText(mutation.getChance())), ChestMenuUtils.getEmptyClickHandler());
        } else {
            menu.addItem(16, new CustomItem(Material.BEEHIVE,
                    "",
                    ChatColor.GOLD + "More than one way to obtain.",
                    ChatColor.GOLD + "Please consult the addon wiki!"), ChestMenuUtils.getEmptyClickHandler());
        }

        // product slots
        List<Pair<ItemStack, Double>> products = species.getProducts();
        if (products != null) {
            for (int i = 0; i < 4 && i < products.size(); i++) {
                Pair<ItemStack, Double> pair = products.get(i);

                ItemStack product = pair.getFirstValue().clone();
                if (product.hasItemMeta()) {
                    List<String> lore = Arrays.asList("", createChanceText(pair.getSecondValue()));

                    ItemMeta meta = product.getItemMeta();
                    meta.setLore(lore);
                    product.setItemMeta(meta);
                }

                menu.addItem(DETAIL_PAGE_PRODUCT_SLOTS[i], product, ChestMenuUtils.getEmptyClickHandler());
            }
        }

        // TODO: 06.06.21 Load the whole genome - load alleles by species uid - BeeRegistry
        // other info slots
        menu.addItem(31, new ItemStack(Material.BEE_SPAWN_EGG), ChestMenuUtils.getEmptyClickHandler());
        menu.addItem(32, new ItemStack(Material.HONEYCOMB), ChestMenuUtils.getEmptyClickHandler());

        menu.open(p);

    }

    private String createChanceText(double chance) {
        int percentage = (int) Math.ceil(chance * 100);
        return ChatColor.WHITE + "Chance: " + ChatColor.GRAY + percentage + "%";
    }

}
