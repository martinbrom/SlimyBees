package cz.martinbrom.slimybees.utils;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class GeneticUtil {

    @Nullable
    public static AlleleSpecies getSpeciesByName(String name) {
        return getSpeciesByUid(speciesNameToUid(name));
    }

    @Nullable
    public static AlleleSpecies getSpeciesByUid(String uid) {
        Validate.notNull(uid, "Given species uid cannot be null!");

        Allele allele = SlimyBeesPlugin.getAlleleRegistry().get(ChromosomeType.SPECIES, uid);
        if (ChromosomeType.SPECIES.getAlleleClass().isInstance(allele)) {
            return (AlleleSpecies) allele;
        }

        return null;
    }

    @Nonnull
    public static String speciesNameToUid(String name) {
        Validate.notNull(name, "Given species name cannot be null!");

        return "species." + name.toLowerCase(Locale.ROOT);
    }

}
