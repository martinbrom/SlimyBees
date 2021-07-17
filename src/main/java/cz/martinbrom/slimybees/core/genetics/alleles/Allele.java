package cz.martinbrom.slimybees.core.genetics.alleles;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.utils.PatternUtil;
import cz.martinbrom.slimybees.utils.StringUtils;

@ParametersAreNonnullByDefault
public class Allele {

    private final String uid;
    private final String name;
    private final String displayName;
    private final boolean dominant;

    public Allele(String uid, String name, boolean dominant) {
        Validate.notEmpty(uid, "The allele uid must not be null or empty!");
        Validate.isTrue(PatternUtil.UID_PATTERN.matcher(uid).matches(), "The allele uid must start with a prefix " +
                "and be in the lower snake case format, got " + uid + "!");
        Validate.notEmpty(name, "The allele name must not be null or empty!");
        Validate.isTrue(PatternUtil.UPPER_SNAKE.matcher(name).matches(), "The allele name must be " +
                "in the upper snake case format, got " + name + "!");

        this.uid = uid;
        this.name = name;
        this.displayName = StringUtils.humanizeSnake(name);
        this.dominant = dominant;
    }

    @Nonnull
    public String getUid() {
        return uid;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDisplayName() {
        return displayName;
    }

    public boolean isDominant() {
        return dominant;
    }

}
