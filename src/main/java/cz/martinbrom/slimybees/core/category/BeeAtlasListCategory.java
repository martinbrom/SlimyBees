package cz.martinbrom.slimybees.core.category;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.BeeLoreService;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

// impl mostly copied from Slimefun4 MultiCategory
@ParametersAreNonnullByDefault
public class BeeAtlasListCategory extends AbstractBeeAtlasCategory {

    private static final int CATEGORY_SIZE = 36;
    private static final List<String> CHEAT_MODE_BEE_LORE = Arrays.asList("",
            ChatColor.YELLOW + "Left Click" + ChatColor.GRAY + " to get a " + ChatColor.BOLD + "Princess",
            ChatColor.YELLOW + "Right Click" + ChatColor.GRAY + " to get a " + ChatColor.BOLD + "Drone");

    public static final int PREVIOUS_PAGE_SLOT = 46;
    public static final int NEXT_PAGE_SLOT = 52;

    public BeeAtlasListCategory(BeeLoreService loreService, BeeRegistry beeRegistry, BeeGeneticService geneticService,
                                AlleleRegistry alleleRegistry, BeeAtlasNavigationService navigationService,
                                BeeAtlasCategoryFactory factory, ItemStack displayItem) {
        super(loreService, beeRegistry, geneticService, alleleRegistry, navigationService, factory, "list", displayItem);
    }

    @Override
    public boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
        return true;
    }

    @Override
    protected void fillMenu(ChestMenu menu, Player p, PlayerProfile profile, SlimefunGuideMode layout, int page) {
        List<AlleleSpecies> allSpecies = alleleRegistry.getAllSpecies();
        SlimyBeesPlayerProfile sbProfile = SlimyBeesPlayerProfile.get(p);

        int index = 9;
        int target = (CATEGORY_SIZE * (page - 1)) - 1;

        while (target < (allSpecies.size() - 1) && index < CATEGORY_SIZE + 9) {
            target++;

            AlleleSpecies species = allSpecies.get(target);
            if (layout == SlimefunGuideMode.SURVIVAL_MODE) {
                if (species != null && !species.isSecret()) {
                    addBeeDetailLink(menu, index, species, profile, sbProfile);
                    index++;
                }
            } else {
                addCheatModeButton(menu, index, species);
                index++;
            }
        }

        int pages = target == allSpecies.size() - 1 ? page : (allSpecies.size() - 1) / CATEGORY_SIZE + 1;

        addPreviousPageButton(menu, p, profile, layout, page, pages);
        addNextPageButton(menu, p, profile, layout, page, pages);

        menu.open(p);
    }

    private void addPreviousPageButton(ChestMenu menu, Player p, PlayerProfile profile, SlimefunGuideMode layout, int page, int pages) {
        menu.addItem(PREVIOUS_PAGE_SLOT, ChestMenuUtils.getPreviousButton(p, page, pages), (pl, slot, item, action) -> {
            int prev = page - 1;

            if (prev != page && prev > 0) {
                open(p, profile, layout, prev);
            }

            return false;
        });
    }

    private void addNextPageButton(ChestMenu menu, Player p, PlayerProfile profile, SlimefunGuideMode layout, int page, int pages) {
        menu.addItem(NEXT_PAGE_SLOT, ChestMenuUtils.getNextButton(p, page, pages), (pl, slot, item, action) -> {
            int next = page + 1;

            if (next != page && next <= pages) {
                open(p, profile, layout, next);
            }

            return false;
        });
    }

    /**
     * Adds an {@link ItemStack} to given {@link ChestMenu} to given slot.
     * Clicking the button gives a player an analyzed bee with given {@link AlleleSpecies}.
     *
     * @param menu The {@link ChestMenu} to add the button to
     * @param slot The slot to add the button to
     * @param species The bee with {@link AlleleSpecies} to give to the player,
     *                that clicks the button.
     */
    private void addCheatModeButton(ChestMenu menu, int slot, AlleleSpecies species) {
        ItemStack beeItemStack = loreService.generify(species.getDroneItemStack(), CHEAT_MODE_BEE_LORE);
        menu.addItem(slot, beeItemStack, (pl, clickedSlot, item, action) -> {
            ItemStack itemStack;
            if (action.isRightClicked()) {
                itemStack = species.getDroneItemStack();
            } else {
                itemStack = species.getPrincessItemStack();
            }

            Genome genome = geneticService.getGenome(species);
            ItemStack updatedItemStack = loreService.updateLore(itemStack, genome);
            pl.getInventory().addItem(updatedItemStack);

            return false;
        });
    }

}
