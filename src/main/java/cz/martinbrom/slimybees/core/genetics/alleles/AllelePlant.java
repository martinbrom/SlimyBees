package cz.martinbrom.slimybees.core.genetics.alleles;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;

@ParametersAreNonnullByDefault
public class AllelePlant extends Allele {

    private final Material value;

    public AllelePlant(String uid, String name, Material value, boolean dominant) {
        super(uid, name, dominant);

        Validate.isTrue(value.isBlock(), "The material of AllelePlant has to be a block!");
        this.value = value;
    }

    @Nonnull
    public Material getValue() {
        return value;
    }

}
