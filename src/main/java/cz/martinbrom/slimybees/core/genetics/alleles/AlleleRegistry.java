package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.BeeBuilder;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

@ParametersAreNonnullByDefault
public class AlleleRegistry {

    private final Map<ChromosomeType, Map<String, ? extends Allele>> allelesByChromosomeType = new HashMap<>();
    private final Map<ChromosomeType, List<Pair<Double, String>>> sortedPairsByChromosomeType = new HashMap<>();

    @Nullable
    public Allele get(ChromosomeType type, String uid) {
        Map<String, ? extends Allele> alleleMap = allelesByChromosomeType.get(type);
        if (alleleMap == null) {
            return null;
        }

        return alleleMap.get(uid);
    }

    /**
     * Adds a new {@link Allele} to available alleles for given {@link ChromosomeType}.
     * DO NOT USE DIRECTLY FOR REGISTERING SPECIES! USE {@link BeeBuilder} INSTEAD!
     *
     * @param type The {@link ChromosomeType} this {@link Allele} belongs to
     * @param allele The {@link Allele} to register
     * @param <T> Super-type of the {@link Allele}
     */
    @SuppressWarnings("unchecked")
    public <T extends Allele> void register(ChromosomeType type, T allele) {
        Validate.notNull(type, "Cannot register an Allele for null ChromosomeType!");
        Validate.notNull(allele, "Cannot register a null Allele!");

        if (!type.getAlleleClass().isAssignableFrom(allele.getClass())) {
            throw new IllegalArgumentException("Allele class (" + allele.getClass()
                    + ") does not match required ChromosomeType class (" + type.getAlleleClass() + ")!");
        }

        Map<String, T> alleleMap = (Map<String, T>) allelesByChromosomeType.computeIfAbsent(type, k -> new HashMap<>());
        alleleMap.put(allele.getUid(), allele);
        allelesByChromosomeType.put(type, alleleMap);
    }

    public <T, V extends AlleleValue<T>> void register(ChromosomeType type, V alleleValue, String uid, String name) {
        boolean dominant = alleleValue.isDominant();
        T value = alleleValue.getValue();

        Class<?> valueClass = value.getClass();
        if (Double.class.isAssignableFrom(valueClass)) {
            AlleleDouble allele = new AlleleDoubleImpl(uid, name, (Double) value, dominant);
            registerAndSort(type, allele, (Double) value);
        } else if (Integer.class.isAssignableFrom(valueClass)) {
            AlleleInteger allele = new AlleleIntegerImpl(uid, name, (Integer) value, dominant);
            registerAndSort(type, allele, (double) (Integer) value);
        } else {
            throw new RuntimeException("Could not create allele for uid: " + uid + " and value " + valueClass);
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public List<AlleleSpecies> getAllSpecies() {
        Map<String, ? extends Allele> speciesMap = allelesByChromosomeType.get(ChromosomeType.SPECIES);
        if (speciesMap == null) {
            return Collections.emptyList();
        }

        List<? extends Allele> species = new ArrayList<Allele>(speciesMap.values());
        return (List<AlleleSpecies>) species;
    }

    @Nonnull
    public List<String> getAllSpeciesNames() {
        return getAllNamesByChromosomeType(ChromosomeType.SPECIES);
    }

    @Nonnull
    public List<String> getAllNamesByChromosomeType(ChromosomeType type) {
        // TODO: 30.06.21 Cache results
        if (type == ChromosomeType.SPECIES) {
            return getAllSpecies().stream()
                    .map(Allele::getName)
                    .collect(Collectors.toList());
        } else {
            List<Pair<Double, String>> sortedPairs = sortedPairsByChromosomeType.get(type);
            if (sortedPairs != null) {
                List<String> names = new ArrayList<>();
                for (Pair<Double, String> pair : sortedPairs) {
                    names.add(pair.getSecondValue());
                }

                return names;
            }
        }

        return Collections.emptyList();
    }

    private <T extends Allele> void registerAndSort(ChromosomeType type, T allele, double value) {
        register(type, allele);

        List<Pair<Double, String>> sortedNames = sortedPairsByChromosomeType.computeIfAbsent(type, k -> new ArrayList<>());
        sortedNames.add(new Pair<>(value, allele.getName()));
        sortedNames.sort(Comparator.comparing(Pair::getFirstValue));
        sortedPairsByChromosomeType.put(type, sortedNames);
    }

}
