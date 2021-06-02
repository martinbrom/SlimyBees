package cz.martinbrom.slimybees.core.genetics.enums;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleInteger;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;

public enum ChromosomeTypeImpl implements ChromosomeType {

    SPECIES(AlleleSpecies.class),
    SPEED(AlleleInteger.class),
    FERTILITY(AlleleInteger.class);

    public static final int CHROMOSOME_COUNT = values().length;

    private final Class<? extends Allele> cls;

    ChromosomeTypeImpl(Class<? extends Allele> cls) {
        this.cls = cls;
    }

    @Override
    public Class<? extends Allele> getAlleleClass() {
        return cls;
    }

}
