package cz.martinbrom.slimybees.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.BeeMutationTree;
import cz.martinbrom.slimybees.core.genetics.Chromosome;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

import static cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType.CHROMOSOME_COUNT;

@ParametersAreNonnullByDefault
public class BeeRegistry {

    private final Map<String, Allele[]> templateMap = new HashMap<>();
    private final BeeMutationTree beeTree = new BeeMutationTree();

    private Allele[] defaultTemplate;

    /**
     * Returns the {@link BeeMutationTree} which contains information on all available mutations.
     *
     * @return The {@link BeeMutationTree} containing mutation info
     */
    @Nonnull
    public BeeMutationTree getBeeMutationTree() {
        return beeTree;
    }

    /**
     * Returns the partial template for given bee species uid.
     * Contains an {@link Allele} for each {@link ChromosomeType} which should
     * be changed when this bee is created through a mutation.
     * Some or even all elements might be null, if that {@link Allele} is not
     * influenced by the mutation.
     * Returns null when no template has been registered for this species uid.
     *
     * @param speciesUid The species uid to get the partial template for
     * @return The partial template for given bee species or null
     */
    @Nullable
    public Allele[] getPartialTemplate(String speciesUid) {
        return templateMap.get(speciesUid);
    }

    /**
     * Returns the full template for given bee species.
     * Each {@link Allele} is guaranteed to be non-null and it's value
     * might come from the bee's own template if that respective {@link ChromosomeType}
     * has been specified, otherwise the value comes from the default template.
     *
     * @param speciesUid The species uid to get the full template for
     * @return The full template for given bee species
     */
    @Nonnull
    public Allele[] getFullTemplate(String speciesUid) {
        Allele[] template = getDefaultTemplate();
        Allele[] partial = templateMap.get(speciesUid);
        if (partial == null) {
            return template;
        }

        for (int i = 0; i < CHROMOSOME_COUNT; i++) {
            if (partial[i] != null) {
                template[i] = partial[i];
            }
        }

        return template;
    }

    /**
     * Returns the default {@link Allele} for given species uid and {@link ChromosomeType}.
     * If the partial template for given species uid contains the {@link Allele}, it will be returned,
     * otherwise the {@link Allele} will come from the default template.
     *
     * @param type The {@link ChromosomeType} describing which {@link Allele} to return
     * @param speciesUid The species uid to get the {@link Allele} for
     * @return The default {@link Allele} for given species uid and {@link ChromosomeType}
     */
    @Nonnull
    public Allele getAllele(ChromosomeType type, String speciesUid) {
        Allele[] partial = templateMap.get(speciesUid);
        Allele allele = partial[type.ordinal()];

        return allele == null ? getDefaultTemplate()[type.ordinal()] : allele;
    }

    /**
     * Registers a partial template for a bee species.
     * The SPECIES {@link Chromosome} is used to determine the bee's uid.
     *
     * @param template Bee's partial template to register
     */
    public void registerPartialTemplate(Allele[] template) {
        Validate.notNull(template, "The partial template cannot be null!");
        Validate.isTrue(template.length == CHROMOSOME_COUNT, "The partial template needs to have exactly " + CHROMOSOME_COUNT + " alleles!");
        Validate.notNull(template[ChromosomeType.SPECIES.ordinal()], "The partial template needs to contain the species chromosome!");

        templateMap.put(template[ChromosomeType.SPECIES.ordinal()].getUid(), template);
    }

    /**
     * Registers the default bee template.
     * Contains an {@link Allele} for each {@link ChromosomeType} other than SPECIES.
     * The SPECIES {@link Allele} might be included (but is not required to).
     *
     * @param template The default bee template
     */
    public void registerDefaultTemplate(Allele[] template) {
        Validate.notNull(template, "The default template cannot be null!");
        Validate.isTrue(template.length == CHROMOSOME_COUNT, "The default template needs to have exactly " + CHROMOSOME_COUNT + " alleles!");

        // skip species, it can be null in the default template
        for (int i = 1; i < CHROMOSOME_COUNT; i++) {
            if (template[i] == null) {
                throw new IllegalArgumentException("Chromosome of type " + ChromosomeType.values()[i]
                        + " is missing from the default template!");
            }
        }

        defaultTemplate = template;
    }

    /**
     * Returns a copy of the default bee template.
     *
     * @return Copy of the default bee template
     */
    @Nonnull
    private Allele[] getDefaultTemplate() {
        return Arrays.copyOf(defaultTemplate, CHROMOSOME_COUNT);
    }

}
