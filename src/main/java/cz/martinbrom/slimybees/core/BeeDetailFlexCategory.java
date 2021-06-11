package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
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
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.BeeMutation;
import cz.martinbrom.slimybees.core.genetics.BeeMutationTree;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;
import cz.martinbrom.slimybees.utils.GeneticUtil;
import cz.martinbrom.slimybees.utils.StringUtils;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeDetailFlexCategory extends BaseFlexCategory {

    private final AlleleSpecies species;

    private static final int[] BACKGROUND_SLOTS = new int[] {
            9, 11, 12, 13, 14, 15, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 30, 35,
            36, 39, 44 };
    private static final int[] PRODUCT_SLOTS = new int[] { 28, 29, 37, 38 };
    private static final int[] ALLELE_SLOTS = new int[] { 31, 32, 33, 34, 40, 41, 42, 43 };
    private static final Material[] ALLELE_MATERIALS = new Material[] {Material.HONEYCOMB, Material.BEE_SPAWN_EGG};

    public BeeDetailFlexCategory(AlleleSpecies species) {
        super(SlimyBeesPlugin.getKey("bee_detail." + species.getUid()),
                SlimyBeesPlugin.getBeeLoreService().generify(species.getDroneItemStack()));

        this.species = species;
    }

    @Override
    public boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        return false;
    }

    @Nonnull
    @Override
    protected int[] getExtraBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    @Override
    protected void fillMenu(ChestMenu menu, Player p, PlayerProfile profile, SlimefunGuideMode layout, int page) {
        SlimyBeesPlayerProfile sbProfile = SlimyBeesPlayerProfile.get(p);
        if (layout == SlimefunGuideMode.CHEAT_MODE || !sbProfile.hasDiscovered(species)) {
            SlimefunGuide.openMainMenu(profile, layout, profile.getGuideHistory().getMainMenuPage());
            return;
        }

        // detail bee
        BeeLoreService beeLoreService = SlimyBeesPlugin.getBeeLoreService();
        menu.addItem(10, beeLoreService.generify(species.getDroneItemStack()), ChestMenuUtils.getEmptyClickHandler());

        BeeMutationTree mutationTree = SlimyBeesPlugin.getBeeRegistry().getBeeMutationTree();
        List<BeeMutation> mutations = mutationTree.getMutationForChild(species.getUid());
        if (mutations == null) {
            menu.addItem(16, new CustomItem(Material.BEE_NEST, ChatColor.DARK_GREEN + "Found naturally in the world"), ChestMenuUtils.getEmptyClickHandler());
        } else if (mutations.size() == 1) {
            BeeMutation mutation = mutations.get(0);

            menu.addItem(16, new CustomItem(Material.PAPER, createChanceText(mutation.getChance())), ChestMenuUtils.getEmptyClickHandler());
            addBeeParent(menu, profile, sbProfile, layout, mutation.getFirstParent(), 14);
            addBeeParent(menu, profile, sbProfile, layout, mutation.getSecondParent(), 15);
        } else {
            menu.addItem(16, new CustomItem(Material.BEEHIVE,
                    ChatColor.GOLD + "More than one way to obtain.",
                    ChatColor.GOLD + "Please consult the addon wiki!"), ChestMenuUtils.getEmptyClickHandler());
        }

        // product slots
        List<Pair<ItemStack, Double>> products = species.getProducts();
        if (products != null) {
            for (int i = 0; i < PRODUCT_SLOTS.length && i < products.size(); i++) {
                Pair<ItemStack, Double> pair = products.get(i);

                ItemStack product = pair.getFirstValue().clone();
                if (product.hasItemMeta()) {
                    List<String> lore = Arrays.asList("", createChanceText(pair.getSecondValue()));

                    ItemMeta meta = product.getItemMeta();
                    meta.setLore(lore);
                    product.setItemMeta(meta);
                }

                menu.addItem(PRODUCT_SLOTS[i], product, ChestMenuUtils.getEmptyClickHandler());
            }
        }


        // other info slots
        BeeGeneticService geneticService = SlimyBeesPlugin.getBeeGeneticService();
        Genome genome = geneticService.getGenome(species.getDroneItemStack());

        if (genome != null) {
            // -1 because we dont show species in the allele info box
            int alleleCount = ChromosomeTypeImpl.CHROMOSOME_COUNT - 1;
            for (int i = 0; i < ALLELE_SLOTS.length && i < alleleCount && i < ALLELE_MATERIALS.length; i++) {
                addAlleleInfo(menu, i, genome);
            }
        }
    }

    @Nullable
    @Override
    protected String getTitleSuffix() {
        return species.getName();
    }

    private void addBeeParent(ChestMenu menu, PlayerProfile profile, SlimyBeesPlayerProfile sbProfile,
                              SlimefunGuideMode layout, String parentUid, int slot) {
        AlleleSpecies species = GeneticUtil.getSpeciesByUid(parentUid);
        if (species != null && sbProfile.hasDiscovered(species)) {
            ItemStack droneItemStack = SlimyBeesPlugin.getBeeLoreService().generify(species.getDroneItemStack());
            menu.addItem(slot, droneItemStack, (pl, clickedSlot, item, action) -> {
                SlimefunGuide.openCategory(profile, new BeeDetailFlexCategory(species), layout, 1);
                return false;
            });
        } else {
            menu.addItem(slot, UNKNOWN_SPECIES_ITEM, ChestMenuUtils.getEmptyClickHandler());

            // if either one of the parents is unknown, we won't show the chance
            menu.addItem(16, UNKNOWN_CHANCE_ITEM, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    private void addAlleleInfo(ChestMenu menu, int index, Genome genome) {
        AlleleRegistry alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();

        // +1 to skip species
        ChromosomeTypeImpl type = ChromosomeTypeImpl.values()[index + 1];
        List<String> alleleNames = alleleRegistry.getAllNamesByChromosomeType(type);

        String name = genome.getActiveAllele(type).getName();
        List<String> lore = new ArrayList<>();

        for (String alleleName : alleleNames) {
            String formattedName = StringUtils.snakeToCamel(alleleName);

            lore.add(alleleName.equals(name)
                    ? "" + ChatColor.DARK_GREEN + ChatColor.BOLD + formattedName
                    : ChatColor.GREEN + formattedName);
        }

        CustomItem item = new CustomItem(ALLELE_MATERIALS[index],
                "" + ChatColor.WHITE + ChatColor.BOLD + StringUtils.capitalize(type.name()), lore);
        menu.addItem(ALLELE_SLOTS[index], item, ChestMenuUtils.getEmptyClickHandler());
    }

}
