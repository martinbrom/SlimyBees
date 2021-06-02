package cz.martinbrom.slimybees.core.genetics.enums;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;

public interface ChromosomeType {

    Class<? extends Allele> getAlleleClass();

    int ordinal();

}
