package cz.martinbrom.slimybees.core.genetics;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.utils.Tuple;

@ParametersAreNonnullByDefault
public class Chromosome<T> {

    public static final String DELIMITER = ";";

    private final Tuple<Allele<T>> alleles;

    public static <T> Chromosome<T> parse(String str, Function<String, T> parser) {
        String[] parts = str.split(DELIMITER);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Found a chromosome with incorrect number of alleles!");
        }

        return new Chromosome<>(new Allele<>(parser.apply(parts[0])), new Allele<>(parser.apply(parts[1])));
    }

    Chromosome(Allele<T> first, Allele<T> second) {
        Validate.notNull(first, "First allele must not be null!");
        Validate.notNull(second, "Second allele must not be null!");
        this.alleles = new Tuple<>(first, second);
    }

    @Nonnull
    public Allele<T> getPrimaryAllele() {
        return alleles.getFirstValue();
    }

    @Nonnull
    public Allele<T> getSecondaryAllele() {
        return alleles.getFirstValue();
    }

    @Nonnull
    public Allele<T> getActiveAllele() {
        return getSecondaryAllele().isDominant() && !getPrimaryAllele().isDominant()
                ? getSecondaryAllele()
                : getPrimaryAllele();
    }

    @Nonnull
    public Allele<T> getInactiveAllele() {
        return getSecondaryAllele().isDominant() && !getPrimaryAllele().isDominant()
                ? getPrimaryAllele()
                : getSecondaryAllele();
    }

}
