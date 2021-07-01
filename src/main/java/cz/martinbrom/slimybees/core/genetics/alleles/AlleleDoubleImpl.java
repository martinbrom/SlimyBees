package cz.martinbrom.slimybees.core.genetics.alleles;

public class AlleleDoubleImpl extends AlleleImpl implements AlleleDouble {

    private final double value;

    public AlleleDoubleImpl(String uid, String name, double value, boolean dominant) {
        super(uid, name, dominant);
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

}
