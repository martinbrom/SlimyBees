package cz.martinbrom.slimybees.core.genetics.alleles;

public class AlleleInteger extends Allele {

    private final int value;

    public AlleleInteger(String uid, String name, int value, boolean dominant) {
        super(uid, name, dominant);

        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
