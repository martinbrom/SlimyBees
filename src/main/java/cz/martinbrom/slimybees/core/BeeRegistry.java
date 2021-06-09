package cz.martinbrom.slimybees.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.BeeMutationTree;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleHelper;
import cz.martinbrom.slimybees.core.genetics.enums.AlleleType;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;
import cz.martinbrom.slimybees.items.bees.Drone;
import cz.martinbrom.slimybees.items.bees.Princess;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

@ParametersAreNonnullByDefault
public class BeeRegistry {

    private final Map<String, Allele[]> templateMap = new HashMap<>();
    private final Map<String, Pair<Princess, Drone>> beeItems = new HashMap<>();
    private final BeeMutationTree beeTree = new BeeMutationTree();

    private Allele[] defaultTemplate;

    @Nullable
    public Allele[] getTemplate(String species) {
        return templateMap.get(species);
    }

    public void registerTemplate(Allele[] template) {
        Validate.notNull(template, "Template cannot be null!");
        Validate.isTrue(template.length > 0, "Template needs to have at least 1 allele!");

        templateMap.put(template[ChromosomeTypeImpl.SPECIES.ordinal()].getUid(), template);
    }

    @Nonnull
    public Allele[] getDefaultTemplate() {
        if (defaultTemplate == null) {
            defaultTemplate = new Allele[ChromosomeTypeImpl.CHROMOSOME_COUNT];

            AlleleHelper.set(defaultTemplate, ChromosomeTypeImpl.SPEED, AlleleType.Speed.VERY_SLOW);
            AlleleHelper.set(defaultTemplate, ChromosomeTypeImpl.FERTILITY, AlleleType.Fertility.NORMAL);
        }
        return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
    }

    public Map<String, Pair<Princess, Drone>> getBeeItems() {
        return beeItems;
    }

    public BeeMutationTree getBeeMutationTree() {
        return beeTree;
    }
}
