package cz.martinbrom.slimybees.core.genetics.alleles;

import org.bukkit.Material;

public class AllelePlant extends Allele {

    private final Material value;

    public AllelePlant(String uid, String name, Material value, boolean dominant) {
        super(uid, name, dominant);

        this.value = value;
    }

    public Material getValue() {
        return value;
    }

}
