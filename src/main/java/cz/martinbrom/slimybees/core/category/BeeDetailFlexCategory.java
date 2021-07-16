package cz.martinbrom.slimybees.core.category;

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
import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.BeeMutation;
import cz.martinbrom.slimybees.core.genetics.BeeMutationTree;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;
import cz.martinbrom.slimybees.setup.BeeSetup;
import cz.martinbrom.slimybees.setup.SpeciesUids;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeDetailFlexCategory extends BaseFlexCategory {

    private final BeeLoreService loreService;
    private final BeeRegistry beeRegistry;
    private final BeeGeneticService geneticService;
    private final AlleleRegistry alleleRegistry;

    private final AlleleSpecies species;

    private static final int[] BACKGROUND_SLOTS = new int[] {
            9, 11, 12, 13, 14, 15, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 30, 35,
            36, 39, 44 };
    private static final int[] PRODUCT_SLOTS = new int[] { 28, 29, 37, 38 };
    private static final int[] ALLELE_SLOTS = new int[] { 31, 32, 33, 34, 40, 41, 42, 43 };

    public BeeDetailFlexCategory(AlleleSpecies species) {
        super(SlimyBeesPlugin.getKey("bee_detail." + species.getUid().replace(':', '.')),
                SlimyBeesPlugin.getBeeLoreService().generify(species.getDroneItemStack()));

        loreService = SlimyBeesPlugin.getBeeLoreService();
        beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        geneticService = SlimyBeesPlugin.getBeeGeneticService();
        alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();

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

    @Nullable
    @Override
    protected String getTitleSuffix() {
        return species.getDisplayName();
    }

    // TODO: 16.07.21 This method is a mess
    @Override
    protected void fillMenu(ChestMenu menu, Player p, PlayerProfile profile, SlimefunGuideMode layout, int page) {
        SlimyBeesPlayerProfile sbProfile = SlimyBeesPlayerProfile.get(p);
        boolean shouldDisplay = sbProfile.hasDiscovered(species) || beeRegistry.isAlwaysDisplayed(species);
        if (layout == SlimefunGuideMode.CHEAT_MODE || !shouldDisplay) {
            SlimefunGuide.openMainMenu(profile, layout, profile.getGuideHistory().getMainMenuPage());
            return;
        }

        // detail bee
        menu.addItem(10, loreService.generify(species.getDroneItemStack()), ChestMenuUtils.getEmptyClickHandler());

        BeeMutationTree mutationTree = beeRegistry.getBeeMutationTree();
        List<BeeMutation> mutations = mutationTree.getMutationForChild(species.getUid());
        if (mutations == null) {
            menu.addItem(16, new CustomItem(Material.BEE_NEST, ChatColor.DARK_GREEN + "Found naturally in the world"), ChestMenuUtils.getEmptyClickHandler());
        } else if (mutations.size() == 1) {
            BeeMutation mutation = mutations.get(0);

            addChanceItem(menu, mutation.getChance());
            addBeeParent(menu, profile, sbProfile, layout, mutation.getFirstParent(), 14);
            addBeeParent(menu, profile, sbProfile, layout, mutation.getSecondParent(), 15);
        } else {
            ItemStack anyNestBee = new CustomItem(SlimyBeesHeadTexture.DRONE.getAsItemStack(), ChatColor.GRAY + "Any Nest Bee");
            if (species.getUid().equals(SpeciesUids.COMMON)) {
                ItemStack otherBee = new CustomItem(SlimyBeesHeadTexture.DRONE.getAsItemStack(), ChatColor.GRAY + "Any Other Nest Bee");

                menu.addItem(14, anyNestBee, ChestMenuUtils.getEmptyClickHandler());
                menu.addItem(15, otherBee, ChestMenuUtils.getEmptyClickHandler());
                addChanceItem(menu, BeeSetup.COMMON_MUTATION_CHANCE);
            } else if (species.getUid().equals(SpeciesUids.CULTIVATED)) {
                AlleleSpecies commonSpecies = (AlleleSpecies) alleleRegistry.get(ChromosomeType.SPECIES, SpeciesUids.COMMON);
                if (commonSpecies != null && beeRegistry.isAlwaysDisplayed(commonSpecies)) {
                    ItemStack commonBee = loreService.generify(commonSpecies.getDroneItemStack());

                    menu.addItem(14, commonBee, (pl, clickedSlot, item, action) -> {
                        SlimefunGuide.openCategory(profile, new BeeDetailFlexCategory(commonSpecies), layout, 1);
                        return false;
                    });
                    addChanceItem(menu, BeeSetup.CULTIVATED_MUTATION_CHANCE);
                } else {
                    menu.addItem(14, UNKNOWN_SPECIES_ITEM, ChestMenuUtils.getEmptyClickHandler());
                    menu.addItem(16, UNKNOWN_CHANCE_ITEM, ChestMenuUtils.getEmptyClickHandler());
                }

                menu.addItem(15, anyNestBee, ChestMenuUtils.getEmptyClickHandler());
            } else {
                menu.addItem(16, new CustomItem(Material.BEEHIVE,
                        ChatColor.GOLD + "More than one way to obtain.",
                        ChatColor.GOLD + "Please consult the addon wiki!"), ChestMenuUtils.getEmptyClickHandler());
            }
        }

        // product slots
        List<ChanceItemStack> products = species.getProducts();
        if (products != null) {
            for (int i = 0; i < PRODUCT_SLOTS.length && i < products.size(); i++) {
                ChanceItemStack itemStack = products.get(i);

                ItemStack product = itemStack.getItem().clone();
                List<String> lore = Arrays.asList("", createChanceText(itemStack.getChance()));

                ItemMeta meta = product.getItemMeta();
                if (meta != null) {
                    meta.setLore(lore);
                    product.setItemMeta(meta);
                }

                menu.addItem(PRODUCT_SLOTS[i], product, ChestMenuUtils.getEmptyClickHandler());
            }
        }


        // other info slots
        Genome genome = geneticService.getGenome(species.getDroneItemStack());

        if (genome != null) {
            // -1 because we dont show species in the allele info box
            int alleleCount = ChromosomeType.CHROMOSOME_COUNT - 1;
            for (int i = 0; i < ALLELE_SLOTS.length && i < alleleCount; i++) {
                addAlleleInfo(menu, i, genome);
            }
        }
    }

    private void addChanceItem(ChestMenu menu, double chance) {
        menu.addItem(16,
                new CustomItem(Material.PAPER, createChanceText(chance)),
                ChestMenuUtils.getEmptyClickHandler());
    }

    private void addBeeParent(ChestMenu menu, PlayerProfile profile, SlimyBeesPlayerProfile sbProfile,
                              SlimefunGuideMode layout, String parentUid, int slot) {
        AlleleSpecies species = ((AlleleSpecies) alleleRegistry.get(ChromosomeType.SPECIES, parentUid));
        if (species != null && (sbProfile.hasDiscovered(species) || beeRegistry.isAlwaysDisplayed(species))) {
            ItemStack droneItemStack = loreService.generify(species.getDroneItemStack());
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
        // +1 to skip species
        ChromosomeType type = ChromosomeType.values()[index + 1];
        String displayName = genome.getActiveAllele(type).getDisplayName();

        String[] lore = type.shouldDisplayAllValues()
                ? createAlleleInfoLore(type, displayName)
                : new String[] { "" + ChatColor.DARK_GREEN + ChatColor.BOLD + displayName };

        CustomItem item = new CustomItem(type.getDisplayItem(),
                "" + ChatColor.WHITE + ChatColor.BOLD + type.getDisplayName(), lore);
        menu.addItem(ALLELE_SLOTS[index], item, ChestMenuUtils.getEmptyClickHandler());
    }

    @Nonnull
    private String[] createAlleleInfoLore(ChromosomeType type, String displayName) {
        List<String> alleleDisplayNames = alleleRegistry.getAllDisplayNamesByChromosomeType(type);

        int size = alleleDisplayNames.size();
        String[] lore = new String[size];
        for (int i = 0; i < size; i++) {
            String alleleDisplayName = alleleDisplayNames.get(i);

            lore[i] = alleleDisplayName.equals(displayName)
                    ? "" + ChatColor.DARK_GREEN + ChatColor.BOLD + alleleDisplayName
                    : ChatColor.GREEN + alleleDisplayName;
        }

        return lore;
    }

}
