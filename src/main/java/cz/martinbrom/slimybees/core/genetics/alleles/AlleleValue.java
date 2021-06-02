package cz.martinbrom.slimybees.core.genetics.alleles;

public interface AlleleValue<T> {

    boolean isDominant();

    T getValue();

}
