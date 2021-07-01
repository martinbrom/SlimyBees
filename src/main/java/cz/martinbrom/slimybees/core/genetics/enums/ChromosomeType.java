package cz.martinbrom.slimybees.core.genetics.enums;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleDouble;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleInteger;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;

public enum ChromosomeType {

    SPECIES(AlleleSpecies.class),
    PRODUCTIVITY(AlleleDouble.class),
    FERTILITY(AlleleInteger.class);

    public static final int CHROMOSOME_COUNT = values().length;

    private final Class<? extends Allele> cls;

    ChromosomeType(Class<? extends Allele> cls) {
        this.cls = cls;
    }

    public Class<? extends Allele> getAlleleClass() {
        return cls;
    }

}
