package cz.martinbrom.slimybees.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.BeeMutationTree;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleService;
import cz.martinbrom.slimybees.setup.AlleleUids;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class BeeRegistry {

    private final Map<String, Allele[]> templateMap = new HashMap<>();
    private final BeeMutationTree beeTree = new BeeMutationTree();

    private Allele[] defaultTemplate;

    @Nullable
    public Allele[] getTemplate(String species) {
        return templateMap.get(species);
    }

    public void registerTemplate(Allele[] template) {
        Validate.notNull(template, "Template cannot be null!");
        Validate.isTrue(template.length > 0, "Template needs to have at least 1 allele!");

        templateMap.put(template[ChromosomeType.SPECIES.ordinal()].getUid(), template);
    }

    @Nonnull
    public Allele[] getDefaultTemplate() {
        if (defaultTemplate == null) {
            defaultTemplate = new Allele[ChromosomeType.CHROMOSOME_COUNT];
            AlleleService alleleService = SlimyBeesPlugin.getAlleleService();

            alleleService.set(defaultTemplate, ChromosomeType.PRODUCTIVITY, AlleleUids.PRODUCTIVITY_AVERAGE);
            alleleService.set(defaultTemplate, ChromosomeType.FERTILITY, AlleleUids.FERTILITY_NORMAL);
            alleleService.set(defaultTemplate, ChromosomeType.LIFESPAN, AlleleUids.LIFESPAN_NORMAL);
            alleleService.set(defaultTemplate, ChromosomeType.RANGE, AlleleUids.RANGE_NORMAL);
            alleleService.set(defaultTemplate, ChromosomeType.PLANT, AlleleUids.PLANT_NONE);

            // skip species, it is always null in the default template
            for (int i = 1; i < ChromosomeType.CHROMOSOME_COUNT; i++) {
                if (defaultTemplate[i] == null) {
                    throw new IllegalArgumentException("Chromosome of type " + ChromosomeType.values()[i]
                            + " is missing from the default template!");
                }
            }
        }
        return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
    }

    public BeeMutationTree getBeeMutationTree() {
        return beeTree;
    }
}
