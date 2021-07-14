package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class ChromosomeParser {

    public static final String DELIMITER = ";";

    private final BeeRegistry beeRegistry;
    private final AlleleRegistry alleleRegistry;

    public ChromosomeParser(BeeRegistry beeRegistry, AlleleRegistry alleleRegistry) {
        this.beeRegistry = beeRegistry;
        this.alleleRegistry = alleleRegistry;
    }

    @Nonnull
    public Chromosome parseSpecies(String speciesStr) {
        Validate.notNull(speciesStr, "The serialized species cannot be null!");

        String[] parts = speciesStr.split(DELIMITER);
        Allele firstAllele = validateOrGetDefault(alleleRegistry.get(ChromosomeType.SPECIES, parts[0]), ChromosomeType.SPECIES, parts[0]);
        Allele secondAllele = validateOrGetDefault(alleleRegistry.get(ChromosomeType.SPECIES, parts[1]), ChromosomeType.SPECIES, parts[1]);

        return new Chromosome(firstAllele, secondAllele);
    }

    @Nonnull
    public Chromosome parse(@Nullable String chromosomeStr, ChromosomeType type, String firstSpecies, String secondSpecies) {
        Allele uncheckedFirstAllele;
        Allele uncheckedSecondAllele;
        if (chromosomeStr == null) {
            // Backwards compatibility case - the bee has less chromosomes than there are in the current version,
            // we will just load the species default chromosome
            uncheckedFirstAllele = uncheckedSecondAllele = null;
        } else {
            String[] parts = chromosomeStr.split(DELIMITER);
            uncheckedFirstAllele = alleleRegistry.get(type, parts[0]);
            uncheckedSecondAllele = alleleRegistry.get(type, parts[1]);
        }

        Allele firstAllele = validateOrGetDefault(uncheckedFirstAllele, type, firstSpecies);
        Allele secondAllele = validateOrGetDefault(uncheckedSecondAllele, type, secondSpecies);
        return new Chromosome(firstAllele, secondAllele);
    }

    @Nonnull
    public String serialize(Chromosome chromosome) {
        Validate.notNull(chromosome, "Cannot serialize a null chromosome!");

        return chromosome.getPrimaryAllele().getUid() + DELIMITER + chromosome.getSecondaryAllele().getUid();
    }

    @Nonnull
    private Allele validateOrGetDefault(@Nullable Allele allele, ChromosomeType type, String species) {
        if (allele != null) {
            return allele;
        }

        return beeRegistry.getAllele(type, species);
    }

}
