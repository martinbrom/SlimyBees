package cz.martinbrom.slimybees.core.genetics;

import static cz.martinbrom.slimybees.core.genetics.ChromosomeType.CHROMOSOME_COUNT;

public class GenomeBuilder {

    private final Chromosome<Object>[] chromosomes;

    @SuppressWarnings("unchecked")
    public GenomeBuilder(String species) {
        chromosomes = new Chromosome[CHROMOSOME_COUNT];
        for (int i = 0; i < CHROMOSOME_COUNT; i++) {
            ChromosomeType type = ChromosomeType.values()[i];

            Allele<Object> allele;
            if (type == ChromosomeType.SPECIES) {
                allele = new Allele<>(species);
            } else {
                allele = new Allele<>(type.getDefaultValue());
            }

            chromosomes[i] = new Chromosome<>(allele, allele);
        }
    }

    public GenomeBuilder setDefaultChromosome(ChromosomeType type, Object value) {
        if (type == ChromosomeType.SPECIES) {
            throw new IllegalArgumentException("Cannot overwrite the species of a bee!");
        }

        Allele<Object> allele = new Allele<>(value);
        chromosomes[type.ordinal()] = new Chromosome<>(allele, allele);
        return this;
    }

    public Genome build() {
        return new Genome(chromosomes);
    }

}
