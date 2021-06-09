package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class Chromosome {

    private static final String DELIMITER = ";";

    private final Allele primary;
    private final Allele secondary;

    Chromosome(Allele allele) {
        primary = allele;
        secondary = allele;
    }

    Chromosome(Allele primary, Allele secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Nonnull
    public static Chromosome parse(@Nullable String firstSpecies, @Nullable String secondSpecies, String chromosomeStr, ChromosomeType type) {
        AlleleRegistry registry = SlimyBeesPlugin.getAlleleRegistry();

        String[] parts = chromosomeStr.split(DELIMITER);
        Allele firstAllele = validateOrGetDefault(registry.getByUid(parts[0]), firstSpecies, type);
        Allele secondAllele = validateOrGetDefault(registry.getByUid(parts[1]), secondSpecies, type);
        return new Chromosome(firstAllele, secondAllele);
    }

    @Nonnull
    public Allele getPrimaryAllele() {
        return primary;
    }

    @Nonnull
    public Allele getSecondaryAllele() {
        return secondary;
    }

    @Nonnull
    public Allele getActiveAllele() {
        if (!primary.isDominant() && secondary.isDominant()) {
            return secondary;
        }

        return primary;
    }

    @Nonnull
    public Allele getInactiveAllele() {
        if (!primary.isDominant() && secondary.isDominant()) {
            return primary;
        }

        return secondary;
    }

    @Nonnull
    public String serialize() {
        return getPrimaryAllele().getUid() + DELIMITER + getSecondaryAllele().getUid();
    }

    @Nonnull
    private static Allele validateOrGetDefault(@Nullable Allele allele, @Nullable String species, ChromosomeType type) {
        if (type.getAlleleClass().isInstance(allele)) {
            return allele;
        }

        Allele[] template = null;

        BeeRegistry beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        if (species != null) {
            template = beeRegistry.getTemplate(species);
        }

        if (template == null) {
            template = beeRegistry.getDefaultTemplate();
        }

        return template[type.ordinal()];
    }

}
