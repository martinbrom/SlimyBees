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

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.Chromosome;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;
import cz.martinbrom.slimybees.items.bees.AbstractBee;
import cz.martinbrom.slimybees.utils.StringUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

@ParametersAreNonnullByDefault
public class BeeLoreService {

    public static final String UNKNOWN_LORE = ChatColor.DARK_GRAY + "<unknown>";

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

    // TODO: 08.06.21 Document that it creates a copy
    public ItemStack makeUnknown(ItemStack item) {
        if (!item.hasItemMeta()) {
            return item;
        }

        ItemStack copy = item.clone();

        ItemMeta meta = copy.getItemMeta();
        meta.setLore(Collections.singletonList(UNKNOWN_LORE));

        copy.setItemMeta(meta);
        return copy;
    }

    // TODO: 08.06.21 Document that it creates a copy
    public ItemStack generify(ItemStack item) {
        return generify(item, Collections.emptyList());
    }

    // TODO: 08.06.21 Cache this somewhere
    // TODO: 08.06.21 Document that it creates a copy
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

    // TODO: 08.06.21 Document that it creates a copy
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

    @Nonnull
    public static List<String> createLore(Genome genome) {
        List<String> lore = new ArrayList<>();
        lore.add("");   // intentional empty first line

        Chromosome[] chromosomes = genome.getChromosomes();
        for (ChromosomeType type : ChromosomeTypeImpl.values()) {
            lore.add(ChatColor.WHITE + StringUtils.snakeToCamel(type.toString()) + ": "
                    + ChatColor.GRAY + StringUtils.snakeToCamel(chromosomes[type.ordinal()].getPrimaryAllele().getName()) + " / "
                    + StringUtils.snakeToCamel(chromosomes[type.ordinal()].getSecondaryAllele().getName()));
        }

        return lore;
    }

}
