package cz.martinbrom.slimybees.core.genetics;

import static cz.martinbrom.slimybees.core.genetics.ChromosomeType.CHROMOSOME_COUNT;

public class GenomeBuilder {

    private final Chromosome<Object>[] chromosomes;

    @SuppressWarnings("unchecked")
    public GenomeBuilder() {
        chromosomes = new Chromosome[CHROMOSOME_COUNT];
    }

    public GenomeBuilder setDefaultChromosome(ChromosomeType type, Object value) {
        if (type == ChromosomeType.SPECIES) {
            throw new IllegalArgumentException("Cannot overwrite the species of a bee!");
        }

        Allele<Object> allele = new Allele<>(value);
        chromosomes[type.ordinal()] = new Chromosome<>(allele, allele);
        return this;
    }

    @SuppressWarnings("unchecked")
    public Genome build() {
//        return new Genome(chromosomes);
        Allele<Object> allele = new Allele<>(2);
        Chromosome<Object>[] chromosomes = new Chromosome[]{
                new Chromosome<>(allele, allele),
                new Chromosome<>(allele, allele),
                new Chromosome<>(allele, allele),
                new Chromosome<>(allele, allele)
        };
        return new Genome(chromosomes);
    }

}
