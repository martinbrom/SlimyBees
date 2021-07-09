package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.martinbrom.slimybees.core.genetics.Chromosome;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.items.bees.AbstractBee;
import cz.martinbrom.slimybees.utils.StringUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

/**
 * This service manipulates lore for bee-related {@link ItemStack}s.
 */
@ParametersAreNonnullByDefault
public class BeeLoreService {

    public static final String UNKNOWN_LORE = ChatColor.DARK_GRAY + "<unknown>";

    /**
     * Checks whether the given {@link ItemStack} is considered to be an "unknown" bee
     * by comparing its lore to the UNKNOWN_LORE constant.
     *
     * @param item The {@link ItemStack} to check
     * @return True if the {@link ItemStack} is considered to be an "unknown" bee,
     *         false otherwise
     */
    public boolean isUnknown(ItemStack item) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem instanceof AbstractBee) {
            // no need to check 'hasItemMeta()' because 'getByItem()' already does
            ItemMeta meta = item.getItemMeta();

            List<String> lore = meta.getLore();
            if (lore != null && lore.size() == 1) {
                return lore.get(0).equals(UNKNOWN_LORE);
            }
        }

        return false;
    }

    /**
     * Modifies the given {@link ItemStack} by changing its lore to the UNKNOWN_LORE constant value.
     * The {@link ItemStack} is copied before applying any changes.
     *
     * @param item The {@link ItemStack} to modify
     * @return The modified {@link ItemStack}
     */
    @Nonnull
    public ItemStack makeUnknown(ItemStack item) {
        if (!item.hasItemMeta()) {
            return item;
        }

        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem instanceof AbstractBee) {
            ItemStack copy = item.clone();

            ItemMeta meta = copy.getItemMeta();
            meta.setLore(Collections.singletonList(UNKNOWN_LORE));

            copy.setItemMeta(meta);
            return copy;
        }

        return item;
    }

    /**
     * Modifies the given {@link ItemStack} by removing its lore
     * and changing the kind of bee (Princess / Drone) to the generic "Bee".
     * The {@link ItemStack} is copied before applying any changes.
     *
     * @param item The {@link ItemStack} to modify
     * @return The modified {@link ItemStack}
     */
    @Nonnull
    public ItemStack generify(ItemStack item) {
        return generify(item, Collections.emptyList());
    }

    // TODO: 08.06.21 Cache this somewhere
    /**
     * Modifies the given {@link ItemStack} by updating its lore
     * and changing the kind of bee (Princess / Drone) to the generic "Bee".
     * The {@link ItemStack} is copied before applying any changes.
     *
     * @param item The {@link ItemStack} to modify
     * @param lore The lore to apply to the {@link ItemStack}
     * @return The modified {@link ItemStack}
     */
    @Nonnull
    public ItemStack generify(ItemStack item, List<String> lore) {
        if (!item.hasItemMeta()) {
            return item;
        }

        ItemStack copy = item.clone();

        ItemMeta meta = copy.getItemMeta();
        meta.setLore(lore);

        String genericName = meta.getDisplayName().replace("Drone", "Bee").replace("Princess", "Bee");
        meta.setDisplayName(genericName);

        copy.setItemMeta(meta);
        return copy;

    }

    /**
     * Modifies the given {@link ItemStack} by updating its lore
     * based on the given {@link Genome}.
     * The {@link ItemStack} is copied before applying any changes.
     *
     * @param item   The {@link ItemStack} to modify
     * @param genome The {@link Genome} that is used to create
     *               the new lore for the given {@link ItemStack}
     * @return The modified {@link ItemStack}
     */
    @Nonnull
    public ItemStack updateLore(ItemStack item, Genome genome) {
        Validate.notNull(item, "The ItemStack cannot be null when updating item lore!");
        Validate.notNull(genome, "The genome cannot be null when updating item lore!");

        if (!item.hasItemMeta()) {
            return item;
        }

        ItemStack copy = item.clone();

        ItemMeta meta = copy.getItemMeta();
        meta.setLore(createLore(genome));
        copy.setItemMeta(meta);

        return copy;
    }

    /**
     * Creates a String lore based on the primary and secondary values
     * of the given {@link Genome}.
     * The lore contains all chromosomes, each on a separate line
     * and each line consists of two elements, the primary and the secondary
     * value of that specific chromosome.
     * Each line also has a prefix showing what chromosome this line describes.
     *
     * @param genome The {@link Genome} to use
     * @return Lore based on the values of the given {@link Genome}
     */
    @Nonnull
    public List<String> createLore(Genome genome) {
        List<String> lore = new ArrayList<>();
        lore.add("");   // intentional empty first line

        Chromosome[] chromosomes = genome.getChromosomes();
        for (ChromosomeType type : ChromosomeType.values()) {
            lore.add(ChatColor.WHITE + StringUtils.snakeToCamel(type.toString()) + ": "
                    + ChatColor.GRAY + StringUtils.snakeToCamel(chromosomes[type.ordinal()].getPrimaryAllele().getName()) + " / "
                    + StringUtils.snakeToCamel(chromosomes[type.ordinal()].getSecondaryAllele().getName()));
        }

        return lore;
    }

}
