package cz.martinbrom.slimybees.items.bees;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeAtlasHistory;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.BeeMutation;
import cz.martinbrom.slimybees.core.genetics.BeeMutationTree;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.utils.GeneticUtil;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.InvUtils;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeAtlas extends SimpleSlimefunItem<ItemUseHandler> {

    public static final int ITEMS_PER_PAGE = 36;

    private static final ItemStack NOT_DISCOVERED_ITEM = new CustomItem(Material.BARRIER, ChatColor.GRAY + "Undiscovered Species");

    private static final int[] LIST_PAGE_BACKGROUND_SLOTS = new int[] {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            45, 47, 48, 49, 50, 51, 53 };
    private static final int[] DETAIL_PAGE_BACKGROUND_SLOTS = new int[] {
            1, 2, 3, 4, 5, 6, 7, 8,
            9, 11, 12, 13, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 30, 35,
            36, 39, 44,
            45, 46, 47, 48, 49, 50, 51, 52, 53 };
    private static final int[] DETAIL_PAGE_PRODUCT_SLOTS = new int[] { 28, 29, 37, 38 };

    public BeeAtlas(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.getInteractEvent().setCancelled(true);

            SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(e.getPlayer());
            BeeAtlasHistory history = profile.getBeeAtlasHistory();
            history.openLast(this);
        };
    }

    // TODO: 05.06.21 Refactor this mess into a separate service
    public void openListPage(@Nullable Player player, int page) {
        if (player == null) {
            return;
        }

        ChestMenu menu = new ChestMenu(SlimyBeesPlugin.instance(), "Bee Atlas");

        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(player);
        BeeAtlasHistory history = profile.getBeeAtlasHistory();

        for (int slot : LIST_PAGE_BACKGROUND_SLOTS) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), InvUtils.EMPTY_CLICK);
        }

        int totalPages = (SlimyBeesPlugin.getAlleleRegistry().getSpeciesCount() - 1) / ITEMS_PER_PAGE + 1;
        menu.addItem(46, ChestMenuUtils.getPreviousButton(player, page, totalPages), (p, slot, item, cursor, action) -> {
            history.openPreviousPage(this);
            return false;
        });

        menu.addItem(52, ChestMenuUtils.getNextButton(player, page, totalPages), (p, slot, item, cursor, action) -> {
            history.openNextPage(this);
            return false;
        });

        List<AlleleSpecies> allSpecies = SlimyBeesPlugin.getAlleleRegistry().getAllSpecies();

        for (int i = 0; i < ITEMS_PER_PAGE && i < allSpecies.size(); i++) {
            int slot = i + 9;
            int index = (page - 1) * ITEMS_PER_PAGE + i;
            if (index >= allSpecies.size()) {
                break;
            }

            AlleleSpecies species = allSpecies.get(index);
            if (profile.hasDiscovered(species)) {
                menu.addItem(slot, species.getAnalyzedItemStack(), (p, clickedSlot, item, cursor, action) -> {
                    history.openDetailPage(this, species);
                    return false;
                });
            } else {
                menu.addItem(slot, NOT_DISCOVERED_ITEM, InvUtils.EMPTY_CLICK);
            }
        }

        menu.open(player);
    }

    public void openDetailPage(@Nullable Player player, AlleleSpecies species) {
        if (player == null) {
            return;
        }

        // TODO: 05.06.21 If invalid item? Or prevent somehow?

        ChestMenu menu = new ChestMenu(SlimyBeesPlugin.instance(), "Bee Atlas - " + species.getName());

        for (int slot : DETAIL_PAGE_BACKGROUND_SLOTS) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), InvUtils.EMPTY_CLICK);
        }

        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(player);
        BeeAtlasHistory history = profile.getBeeAtlasHistory();

        // back button
        menu.addItem(0, ChestMenuUtils.getBackButton(player), (p, slot, item, cursor, action) -> {
            history.back(this);
            return false;
        });

        // detail bee
        menu.addItem(10, species.getAnalyzedItemStack(), InvUtils.EMPTY_CLICK);

        // parents + chance
        menu.addItem(14, ChestMenuUtils.getBackground(), InvUtils.EMPTY_CLICK);
        menu.addItem(15, ChestMenuUtils.getBackground(), InvUtils.EMPTY_CLICK);

        BeeMutationTree mutationTree = SlimyBeesPlugin.getBeeRegistry().getBeeMutationTree();
        List<BeeMutation> mutations = mutationTree.getMutationForChild(species.getUid());
        if (mutations == null) {
            menu.addItem(16, new CustomItem(Material.BEE_NEST, ChatColor.DARK_GREEN + "Found naturally in the world"), InvUtils.EMPTY_CLICK);
        } else if (mutations.size() == 1) {
            BeeMutation mutation = mutations.get(0);

            AlleleSpecies firstSpecies = GeneticUtil.getSpeciesByUid(mutation.getFirstParent());
            if (firstSpecies != null) {
                menu.addItem(14, firstSpecies.getAnalyzedItemStack(), InvUtils.EMPTY_CLICK);
            }

            menu.setPlayerInventoryClickHandler((p, clickedSlot, item, cursor, action) -> clickedSlot == 0);

            AlleleSpecies secondSpecies = GeneticUtil.getSpeciesByUid(mutation.getSecondParent());
            if (secondSpecies != null) {
                menu.addItem(15, secondSpecies.getAnalyzedItemStack(), InvUtils.EMPTY_CLICK);
            }

            menu.addItem(16, new CustomItem(Material.PAPER, createChanceText(mutation.getChance())), InvUtils.EMPTY_CLICK);
        } else {
            menu.addItem(16, new CustomItem(Material.BEEHIVE,
                    "",
                    ChatColor.GOLD + "More than one way to obtain.",
                    ChatColor.GOLD + "Please consult the addon wiki!"), InvUtils.EMPTY_CLICK);
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

                menu.addItem(DETAIL_PAGE_PRODUCT_SLOTS[i], product, InvUtils.EMPTY_CLICK);
            }
        }

        // TODO: 06.06.21 Load the whole genome - load alleles by species uid - BeeRegistry
        // other info slots
        menu.addItem(31, new ItemStack(Material.BEE_SPAWN_EGG), InvUtils.EMPTY_CLICK);
        menu.addItem(32, new ItemStack(Material.HONEYCOMB), InvUtils.EMPTY_CLICK);

        menu.open(player);
    }

    private String createChanceText(double chance) {
        int percentage = (int) Math.ceil(chance * 100);
        return ChatColor.WHITE + "Chance: " + ChatColor.GRAY + percentage + "%";
    }

}
