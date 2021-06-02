package cz.martinbrom.slimybees.core.genetics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleHelper;
import cz.martinbrom.slimybees.core.genetics.enums.AlleleType;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeTypeImpl;

@ParametersAreNonnullByDefault
public class BeeRegistry {

    private final Map<String, Allele[]> templateMap = new HashMap<>();

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

    // TODO: 02.06.21 Use some sort of average bee instead?
    @Nonnull
    public Allele[] getDefaultTemplate() {
        if (defaultTemplate == null) {
            defaultTemplate = new Allele[ChromosomeTypeImpl.CHROMOSOME_COUNT];

            AlleleHelper.set(defaultTemplate, ChromosomeTypeImpl.SPEED, AlleleType.Speed.VERY_SLOW);
            AlleleHelper.set(defaultTemplate, ChromosomeTypeImpl.FERTILITY, AlleleType.Fertility.NORMAL);
        }
        return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
    }
}
