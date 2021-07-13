package cz.martinbrom.slimybees.utils;

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
        return getSpeciesByUid(StringUtils.nameToUid(ChromosomeType.SPECIES, name));
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

}
