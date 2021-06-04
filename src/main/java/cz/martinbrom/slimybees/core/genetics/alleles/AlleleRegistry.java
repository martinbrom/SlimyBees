package cz.martinbrom.slimybees.core.genetics.alleles;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class AlleleRegistry {

    private final Map<Class<?>, Map<?, ? extends Allele>> allelesByEnum = new HashMap<>();
    private final Map<String, Allele> allelesByUid = new HashMap<>();
    private final Map<Allele, Set<ChromosomeType>> chromosomeTypesByAllele = new HashMap<>();

    @Nonnull
    public Map<Class<?>, Map<?, ? extends Allele>> getAllelesByEnum() {
        return allelesByEnum;
    }

    @Nullable
    public Allele getByUid(String uid) {
        return allelesByUid.get(uid);
    }

    @Nonnull
    public Collection<ChromosomeType> getValidChromosomeTypesForAllele(Allele allele) {
        return Collections.unmodifiableSet(chromosomeTypesByAllele.get(allele));
    }

    public <K extends Enum<K> & AlleleValue<V>, V> void createAlleles(Class<K> enumClass, ChromosomeType type) {
        EnumMap<K, Allele> enumMap = new EnumMap<>(enumClass);
        String prefix = enumClass.getSimpleName().toLowerCase(Locale.ROOT);
        for (K enumValue : enumClass.getEnumConstants()) {
            Allele allele = createAllele(prefix, enumValue, type);
            enumMap.put(enumValue, allele);
        }

        this.allelesByEnum.put(enumClass, enumMap);
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

    public void registerAllele(Allele allele, ChromosomeType type) {
        if (!type.getAlleleClass().isAssignableFrom(allele.getClass())) {
            throw new IllegalArgumentException("Allele class (" + allele.getClass()
                    + ") does not match chromosome type (" + type.getAlleleClass() + ")!");
        }

        allelesByUid.put(allele.getUid(), allele);
        Set<ChromosomeType> types = chromosomeTypesByAllele.getOrDefault(allele, new HashSet<>());
        types.add(type);
        chromosomeTypesByAllele.put(allele, types);
    }

}
