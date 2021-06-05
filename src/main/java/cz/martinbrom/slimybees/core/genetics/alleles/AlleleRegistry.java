package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;

@ParametersAreNonnullByDefault
public class AlleleRegistry {

    private final Map<Class<?>, Map<?, ? extends Allele>> allelesByEnum = new HashMap<>();
    private final Map<String, Allele> allelesByUid = new HashMap<>();
    private final Map<Allele, Set<ChromosomeType>> chromosomeTypesByAllele = new HashMap<>();
    private final Map<ChromosomeType, List<? extends Allele>> allelesByChromosomeType = new HashMap<>();

    @Nonnull
    public Map<Class<?>, Map<?, ? extends Allele>> getAllelesByEnum() {
        return allelesByEnum;
    }

    @Nullable
    public Allele getByUid(String uid) {
        return allelesByUid.get(uid);
    }

    @Nonnull
    public Set<ChromosomeType> getValidChromosomeTypesForAllele(Allele allele) {
        return Collections.unmodifiableSet(chromosomeTypesByAllele.get(allele));
    }

    public <K extends Enum<K> & AlleleValue<V>, V> void createAlleles(Class<K> enumClass, ChromosomeType type) {
        EnumMap<K, Allele> enumMap = new EnumMap<>(enumClass);
        String prefix = enumClass.getSimpleName().toLowerCase(Locale.ROOT);
        for (K enumValue : enumClass.getEnumConstants()) {
            Allele allele = createAllele(prefix, enumValue, type);
            enumMap.put(enumValue, allele);
        }

        allelesByEnum.put(enumClass, enumMap);
    }

    private <K extends AlleleValue<V>, V> Allele createAllele(String prefix, K enumValue, ChromosomeType type) {
        V value = enumValue.getValue();
        boolean dominant = enumValue.isDominant();
        String name = enumValue.toString().toLowerCase(Locale.ROOT);
        String uid = prefix + "." + name;

        Class<?> valueClass = value.getClass();
        if (Integer.class.isAssignableFrom(valueClass)) {
            AlleleInteger alleleInteger = new AlleleIntegerImpl(uid, name, (Integer) value, dominant);
            registerAllele(alleleInteger, type);
            return alleleInteger;
        }

        throw new RuntimeException("Could not create allele for uid: " + uid + " and value " + valueClass);
    }

    @SuppressWarnings("unchecked")
    public void registerAllele(Allele allele, ChromosomeType type) {
        if (!type.getAlleleClass().isAssignableFrom(allele.getClass())) {
            throw new IllegalArgumentException("Allele class (" + allele.getClass()
                    + ") does not match chromosome type (" + type.getAlleleClass() + ")!");
        }

        allelesByUid.put(allele.getUid(), allele);
        chromosomeTypesByAllele.computeIfAbsent(allele, k -> new HashSet<>()).add(type);

        List<Allele> alleles = (List<Allele>) allelesByChromosomeType.computeIfAbsent(type, k -> new ArrayList<>());
        alleles.add(allele);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public List<AlleleSpecies> getAllSpecies() {
        return (List<AlleleSpecies>) allelesByChromosomeType.get(ChromosomeTypeImpl.SPECIES);
    }

    @Nonnull
    public List<String> getAllSpeciesNames() {
        return allelesByChromosomeType.get(ChromosomeTypeImpl.SPECIES).stream()
                .map(Allele::getName)
                .collect(Collectors.toList());
    }

    public int getSpeciesCount() {
        List<? extends Allele> alleles = allelesByChromosomeType.get(ChromosomeTypeImpl.SPECIES);
        return alleles == null ? 0 : alleles.size();
    }

}
