package cz.martinbrom.slimybees.core.genetics.enums;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleDouble;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleInteger;
import cz.martinbrom.slimybees.core.genetics.alleles.AllelePlant;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import cz.martinbrom.slimybees.utils.StringUtils;

@ParametersAreNonnullByDefault
public enum ChromosomeType {

    SPECIES(AlleleSpecies.class, SlimyBeesHeadTexture.PRINCESS.getAsItemStack(), false),
    PRODUCTIVITY(AlleleDouble.class, new ItemStack(Material.HONEYCOMB), true),
    FERTILITY(AlleleInteger.class, new ItemStack(Material.BEE_SPAWN_EGG), true),
    LIFESPAN(AlleleInteger.class, new ItemStack(Material.CLOCK), true),
    RANGE(AlleleInteger.class, new ItemStack(Material.ELYTRA), true),
    PLANT(AllelePlant.class, new ItemStack(Material.OXEYE_DAISY), false);

    public static final int CHROMOSOME_COUNT = values().length;
    private static final Map<String, ChromosomeType> lookupTable = Stream.of(values())
            .collect(Collectors.toMap(Enum::name, UnaryOperator.identity()));

    private final Class<? extends Allele> cls;
    private final String displayName;
    private final ItemStack displayItem;
    private final boolean displayAllValues;

    ChromosomeType(Class<? extends Allele> cls, ItemStack displayItem, boolean displayAllValues) {
        Validate.notNull(cls, "ChromosomeType allele class cannot be null!");
        Validate.notNull(displayItem, "ChromosomeType display item cannot be null!");

        this.cls = cls;
        displayName = StringUtils.snakeToCamel(name());
        this.displayItem = displayItem;
        this.displayAllValues = displayAllValues;
    }

    @Nullable
    public static ChromosomeType parse(String name) {
        return lookupTable.get(name);
    }

    @Nonnull
    public Class<? extends Allele> getAlleleClass() {
        return cls;
    }

    @Nonnull
    public String getDisplayName() {
        return displayName;
    }

    @Nonnull
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public boolean shouldDisplayAllValues() {
        return displayAllValues;
    }

    public boolean isSortable() {
        // TODO: 13.07.21 Better way?
        return cls.equals(AlleleInteger.class) || cls.equals(AlleleDouble.class);
    }

}
