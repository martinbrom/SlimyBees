package cz.martinbrom.slimybees.core.genetics.alleles;

public class AlleleIntegerImpl extends AlleleImpl implements AlleleInteger {

    private final int value;

    public AlleleIntegerImpl(String uid, String name, int value, boolean dominant) {
        super(uid, name, dominant);
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

}
