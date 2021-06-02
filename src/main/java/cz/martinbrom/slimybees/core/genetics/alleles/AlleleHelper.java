package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class AlleleHelper {

    public static <T extends Enum<T> & ChromosomeType> void set(Allele[] alleles, T chromosomeType, Allele allele) {
        Validate.notNull(alleles, "Cannot update null alleles!");
        Validate.notNull(chromosomeType, "Cannot update alleles belonging to null ChromosomeType!");
        Validate.notNull(allele, "Cannot update alleles with null value!");

        if (!chromosomeType.getAlleleClass().isInstance(allele)) {
            throw new IllegalArgumentException("Allele is the wrong type. Expected: " + chromosomeType + ", got: " + allele);
        }

        Collection<ChromosomeType> validTypes = SlimyBeesPlugin.getAlleleRegistry().getValidChromosomeTypesForAllele(allele);
        if (validTypes.size() > 0 && !validTypes.contains(chromosomeType)) {
            throw new IllegalArgumentException("Allele can't applied to this ChromosomeType. Expected: " + validTypes + ", got: " + chromosomeType);
        }

        alleles[chromosomeType.ordinal()] = allele;
    }

    public static <T extends Enum<T> & ChromosomeType> void set(Allele[] alleles, T chromosomeType, int value) {
        set(alleles, chromosomeType, get(value));
    }

    public static <T extends Enum<T> & ChromosomeType> void set(Allele[] alleles, T chromosomeType, AlleleValue<?> value) {
        set(alleles, chromosomeType, get(value));
    }

    @Nonnull
    private static Allele get(Object value) {
        Class<?> valueClass = value.getClass();
        Map<?, ? extends Allele> map = SlimyBeesPlugin.getAlleleRegistry().getAllelesByEnum().get(valueClass);
        if (map == null) {
            throw new IllegalArgumentException("There is no Allele type for: " + valueClass + ' ' + value);
        }
        Allele allele = map.get(value);
        if (allele == null) {
            throw new IllegalArgumentException("There is no Allele for: " + valueClass + ' ' + value);
        }

        return allele;
    }

}
