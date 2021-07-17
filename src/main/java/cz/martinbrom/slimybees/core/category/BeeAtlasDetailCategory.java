package cz.martinbrom.slimybees.core.category;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.BeeMutationDTO;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.recipe.ChanceItemStack;
import cz.martinbrom.slimybees.setup.BeeSetup;
import cz.martinbrom.slimybees.setup.SpeciesUids;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class BeeAtlasDetailCategory extends AbstractBeeAtlasCategory {

    protected static final int DETAIL_BEE_SLOT = 10;
    protected static final int FIRST_PARENT_SLOT = 14;
    protected static final int SECOND_PARENT_SLOT = 15;
    protected static final int CHANCE_ITEM_SLOT = 16;

    protected static final CustomItem ANY_NEST_BEE = new CustomItem(
            SlimyBeesHeadTexture.DRONE.getAsItemStack(),
            ChatColor.GRAY + "Any Nest Bee");

    protected static final CustomItem OTHER_NEST_BEE = new CustomItem(
            SlimyBeesHeadTexture.DRONE.getAsItemStack(),
            ChatColor.GRAY + "Any Other Nest Bee");

    protected static final CustomItem OBTAINED_NEST_ITEM = new CustomItem(
            Material.BEE_NEST,
            ChatColor.DARK_GREEN + "Found naturally in the world");

    private final AlleleSpecies categorySpecies;

    private static final int[] BACKGROUND_SLOTS = new int[] {
            9, 11, 12, 13, 14, 15, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 30, 35,
            36, 39, 44 };
    protected static final int[] PRODUCT_SLOTS = new int[] { 28, 29, 37, 38 };
    protected static final int[] ALLELE_SLOTS = new int[] { 31, 32, 33, 34, 40, 41, 42, 43 };

    public BeeAtlasDetailCategory(BeeLoreService loreService, BeeRegistry beeRegistry, BeeGeneticService geneticService,
                                  AlleleRegistry alleleRegistry, BeeAtlasNavigationService navigationService,
                                  BeeAtlasCategoryFactory factory, AlleleSpecies categorySpecies) {
        super(loreService, beeRegistry, geneticService, alleleRegistry, navigationService, factory,
                "detail." + categorySpecies.getName().toLowerCase(Locale.ROOT),
                loreService.generify(categorySpecies.getDroneItemStack()));

        this.categorySpecies = categorySpecies;
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
        return categorySpecies.getDisplayName();
    }

    @Override
    protected void fillMenu(ChestMenu menu, Player p, PlayerProfile profile, SlimefunGuideMode mode, int page) {
        SlimyBeesPlayerProfile sbProfile = SlimyBeesPlayerProfile.get(p);
        boolean shouldDisplay = sbProfile.hasDiscovered(categorySpecies) || beeRegistry.isAlwaysDisplayed(categorySpecies);
        if (mode == SlimefunGuideMode.CHEAT_MODE || !shouldDisplay) {
            navigationService.openMainMenu(profile, mode);
            return;
        }

        // the bee that is displayed
        addBeeItem(menu, DETAIL_BEE_SLOT, categorySpecies, ChestMenuUtils.getEmptyClickHandler());

        addObtainSection(menu, profile, sbProfile);
        addProductsSection(menu);
        addAllelesSection(menu);
    }

    /**
     * Adds a section containing information about possible ways to obtain the species
     * this detail page represents.
     *
     * @param menu The {@link ChestMenu} to add the section to
     * @param profile The {@link PlayerProfile} containing guide history
     * @param sbProfile The {@link SlimyBeesPlayerProfile} containing information about player's discoveries
     */
    private void addObtainSection(ChestMenu menu, PlayerProfile profile, SlimyBeesPlayerProfile sbProfile) {
        String speciesUid = categorySpecies.getUid();
        List<BeeMutationDTO> mutations = beeRegistry.getMutationForChild(speciesUid);
        if (mutations.isEmpty()) {
            menu.addItem(CHANCE_ITEM_SLOT, OBTAINED_NEST_ITEM, ChestMenuUtils.getEmptyClickHandler());
        } else if (mutations.size() == 1) {
            addMutationInfo(menu, mutations.get(0), profile, sbProfile);
        } else if (speciesUid.equals(SpeciesUids.COMMON)) {
            addCommonParents(menu);
        } else if (speciesUid.equals(SpeciesUids.CULTIVATED)) {
            addCultivatedParents(menu, profile, sbProfile);
        } else {
            menu.addItem(CHANCE_ITEM_SLOT, new CustomItem(Material.BEEHIVE,
                    ChatColor.GOLD + "More than one way to obtain.",
                    ChatColor.GOLD + "Please consult the addon wiki!"), ChestMenuUtils.getEmptyClickHandler());
        }
    }

    /**
     * Adds a section containing information about both parent {@link AlleleSpecies} and the mutation chance
     * to given {@link ChestMenu}.
     * If any of the parents is not considered "always displayed" or is not discovered yet by
     * the given {@link SlimyBeesPlayerProfile}, the chance will be displayed as "undiscovered".
     *
     * @param menu The {@link ChestMenu} to add the link to
     * @param mutation The {@link BeeMutationDTO} containing both parents and the mutation chance
     * @param profile The {@link PlayerProfile} containing guide history
     * @param sbProfile The {@link SlimyBeesPlayerProfile} containing information about player's discoveries
     */
    private void addMutationInfo(ChestMenu menu, BeeMutationDTO mutation, PlayerProfile profile, SlimyBeesPlayerProfile sbProfile) {
        AlleleSpecies firstSpecies = ((AlleleSpecies) alleleRegistry.get(ChromosomeType.SPECIES, mutation.getFirstParent()));
        AlleleSpecies secondSpecies = ((AlleleSpecies) alleleRegistry.get(ChromosomeType.SPECIES, mutation.getSecondParent()));

        boolean firstDisplayed = addBeeDetailLink(menu, FIRST_PARENT_SLOT, firstSpecies, profile, sbProfile);
        boolean secondDisplayed = addBeeDetailLink(menu, SECOND_PARENT_SLOT, secondSpecies, profile, sbProfile);
        if (firstDisplayed && secondDisplayed) {
            addChanceItem(menu, mutation.getChance());
        } else {
            // if either one of the parents is undiscovered, we won't show the chance
            addUndiscoveredChanceItem(menu);
        }
    }

    private void addCommonParents(ChestMenu menu) {
        menu.addItem(FIRST_PARENT_SLOT, ANY_NEST_BEE, ChestMenuUtils.getEmptyClickHandler());
        menu.addItem(SECOND_PARENT_SLOT, OTHER_NEST_BEE, ChestMenuUtils.getEmptyClickHandler());
        addChanceItem(menu, BeeSetup.COMMON_MUTATION_CHANCE);
    }

    private void addCultivatedParents(ChestMenu menu, PlayerProfile profile, SlimyBeesPlayerProfile sbProfile) {
        AlleleSpecies commonSpecies = (AlleleSpecies) alleleRegistry.get(ChromosomeType.SPECIES, SpeciesUids.COMMON);

        if (addBeeDetailLink(menu, FIRST_PARENT_SLOT, commonSpecies, profile, sbProfile)) {
            addChanceItem(menu, BeeSetup.CULTIVATED_MUTATION_CHANCE);
        } else {
            addUndiscoveredChanceItem(menu);
        }

        menu.addItem(SECOND_PARENT_SLOT, ANY_NEST_BEE, ChestMenuUtils.getEmptyClickHandler());
    }

    private void addChanceItem(ChestMenu menu, double chance) {
        menu.addItem(CHANCE_ITEM_SLOT,
                new CustomItem(Material.PAPER, createChanceText(chance)),
                ChestMenuUtils.getEmptyClickHandler());
    }

    /**
     * Adds an {@link ItemStack} representing an undiscovered chance to given {@link ChestMenu}.
     *
     * @param menu The {@link ChestMenu} to add the chance item to
     */
    private void addUndiscoveredChanceItem(ChestMenu menu) {
        menu.addItem(CHANCE_ITEM_SLOT, UNDISCOVERED_CHANCE_ITEM, ChestMenuUtils.getEmptyClickHandler());
    }

    /**
     * Adds a section containing information about bee products to given {@link ChestMenu}.
     *
     * @param menu The {@link ChestMenu} to add the product section to
     */
    private void addProductsSection(ChestMenu menu) {
        List<ChanceItemStack> products = categorySpecies.getProducts();
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
    }

    /**
     * Adds a section to given {@link ChestMenu} containing information about all
     * allele values found by default in the species this detail page represents.
     *
     * @param menu The {@link ChestMenu} to add the alleles section to
     */
    private void addAllelesSection(ChestMenu menu) {
        Genome genome = geneticService.getGenome(categorySpecies.getDroneItemStack());

        if (genome != null) {
            // -1 because we dont show species in the allele info box
            int alleleCount = ChromosomeType.CHROMOSOME_COUNT - 1;
            for (int i = 0; i < ALLELE_SLOTS.length && i < alleleCount; i++) {
                addAlleleInfo(menu, i, genome);
            }
        }
    }

    /**
     * Adds an {@link ItemStack} representing one allele value to given {@link ChestMenu}.
     *
     * @param menu The {@link ChestMenu} to add the product section to
     * @param index The index of the allele to add, is equal to the order of {@link ChromosomeType}
     *              shifted by one, because species are not displayed this way
     * @param genome The {@link Genome} containing all allele values
     */
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
