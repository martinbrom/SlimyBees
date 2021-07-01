package cz.martinbrom.slimybees.core.genetics.alleles;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class AlleleService {

    private final AlleleRegistry alleleRegistry;

    public AlleleService(AlleleRegistry alleleRegistry) {
        this.alleleRegistry = alleleRegistry;
    }

    public void set(Allele[] template, ChromosomeType type, String uid) {
        Validate.notNull(template, "Cannot update null allele template!");
        Validate.notNull(type, "Cannot update alleles belonging to null ChromosomeType!");
        Validate.notNull(uid, "Cannot update alleles by allele with null uid!");

        Allele allele = alleleRegistry.get(type, uid);
        if (allele == null) {
            throw new IllegalArgumentException("There is no Allele for uid: " + uid);
        }

        template[type.ordinal()] = allele;
    }

}
