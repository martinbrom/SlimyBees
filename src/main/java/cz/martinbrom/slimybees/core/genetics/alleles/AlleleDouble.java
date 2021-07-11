package cz.martinbrom.slimybees.core.genetics.alleles;

public class AlleleDouble extends Allele {

    private final double value;

    public AlleleDouble(String uid, String name, double value, boolean dominant) {
        super(uid, name, dominant);

        this.value = value;
    }

    public double getValue() {
        return value;
    }

}
