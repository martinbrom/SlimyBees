package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;

import cz.martinbrom.slimybees.core.BeeBuilder;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.utils.StringUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;

@ParametersAreNonnullByDefault
public class AlleleRegistry {

    private final Map<ChromosomeType, Map<String, ? extends Allele>> allelesByChromosomeType = new HashMap<>();
    private final Map<ChromosomeType, List<Pair<Double, ? extends Allele>>> sortedAllelesByChromosomeType = new HashMap<>();

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

        Map<String, T> alleleMap = (Map<String, T>) allelesByChromosomeType.computeIfAbsent(type, k -> new LinkedHashMap<>());
        alleleMap.put(allele.getUid(), allele);
        allelesByChromosomeType.put(type, alleleMap);
    }

    /**
     * Adds a new {@link Allele} to available alleles for given {@link ChromosomeType}.
     *
     * @param type The {@link ChromosomeType} this {@link Allele} belongs to
     * @param alleleValue The {@link AlleleValue} that is used to create the {@link Allele} instance
     * @param uid The uid under which the {@link Allele} should be registered
     * @param <T> The type of the extra {@link Allele} value (Double for {@link AlleleDouble}, etc.)
     * @param <V> The type of the {@link AlleleValue} that is used to create the {@link Allele} instance
     */
    public <T, V extends AlleleValue<T>> void register(ChromosomeType type, V alleleValue, String uid) {
        Validate.notNull(type, "The chromosome type cannot be null!");
        Validate.notEmpty(uid, "The allele uid cannot be null or empty!");
        Validate.notNull(alleleValue, "The allele value cannot be null!");

        boolean dominant = alleleValue.isDominant();
        T value = alleleValue.getValue();
        String name = StringUtils.uidToName(uid);

        Class<?> valueClass = value.getClass();
        if (Double.class.isAssignableFrom(valueClass)) {
            AlleleDouble allele = new AlleleDouble(uid, name, (Double) value, dominant);
            registerAndSort(type, allele, (Double) value);
        } else if (Integer.class.isAssignableFrom(valueClass)) {
            AlleleInteger allele = new AlleleInteger(uid, name, (Integer) value, dominant);
            registerAndSort(type, allele, (double) (Integer) value);
        } else if (Material.class.isAssignableFrom(valueClass)) {
            AllelePlant allele = new AllelePlant(uid, name, (Material) value, dominant);
            register(type, allele);
        } else if (AlleleEffect.EffectFunction.class.isAssignableFrom(valueClass)) {
            AlleleEffect allele = new AlleleEffect(uid, name, (AlleleEffect.EffectFunction) value, dominant);
            register(type, allele);
        } else {
            throw new IllegalArgumentException("Could not create allele for uid: " + uid + " and value " + valueClass);
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public List<AlleleSpecies> getAllSpecies() {
        Map<String, ? extends Allele> speciesMap = allelesByChromosomeType.get(ChromosomeType.SPECIES);
        if (speciesMap == null) {
            return Collections.emptyList();
        }

        List<? extends Allele> species = new ArrayList<>(speciesMap.values());
        return (List<AlleleSpecies>) species;
    }

    @Nonnull
    public List<String> getAllNamesByChromosomeType(ChromosomeType type) {
        return getAllAllelesByChromosomeType(type, Allele::getName);
    }

    @Nonnull
    public List<String> getAllDisplayNamesByChromosomeType(ChromosomeType type) {
        return getAllAllelesByChromosomeType(type, Allele::getDisplayName);
    }

    @Nonnull
    public List<String> getAllUidsByChromosomeType(ChromosomeType type) {
        return getAllAllelesByChromosomeType(type, Allele::getUid);
    }

    @Nonnull
    private <T> List<T> getAllAllelesByChromosomeType(ChromosomeType type, Function<Allele, T> mapFn) {
        // TODO: 30.06.21 Cache results
        Stream<? extends Allele> stream = null;
        if (type.isSortable()) {
            // in case the type is sortable, we have a separate sorted collection with value allele pairs
            List<Pair<Double, ? extends Allele>> sortedPairs = sortedAllelesByChromosomeType.get(type);
            if (sortedPairs != null) {
                stream = sortedPairs.stream()
                        .map(Pair::getSecondValue);
            }
        } else {
            // otherwise we rely on insertion order
            Map<String, ? extends Allele> speciesMap = allelesByChromosomeType.get(type);
            if (speciesMap != null) {
                stream = speciesMap.values().stream();
            }
        }

        if (stream != null) {
            return stream.map(mapFn).toList();
        }

        return Collections.emptyList();
    }

    private <T extends Allele> void registerAndSort(ChromosomeType type, T allele, double value) {
        register(type, allele);

        List<Pair<Double, ? extends Allele>> sortedAlleles = sortedAllelesByChromosomeType.computeIfAbsent(type, k -> new ArrayList<>());
        sortedAlleles.add(new Pair<>(value, allele));
        sortedAlleles.sort(Comparator.comparing(Pair::getFirstValue));
        sortedAllelesByChromosomeType.put(type, sortedAlleles);
    }

}
