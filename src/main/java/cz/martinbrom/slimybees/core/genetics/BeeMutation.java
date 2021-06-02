package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

@ParametersAreNonnullByDefault
public class BeeMutation {

    // TODO: 02.06.21 Change to AlleleSpecies instead of uid
    private final String firstParent;
    private final String secondParent;
    private final String child;
    private final double chance;

    public BeeMutation(String firstParent, String secondParent, String child, double chance) {
        Validate.notNull(firstParent, "BeeMutation needs two parents, the first one is null!");
        Validate.notNull(secondParent, "BeeMutation needs two parents, the second one is null!");
        Validate.notNull(child, "The child of a BeeMutation cannot be null!");
        Validate.isTrue(chance > 0 && chance < 1, "The chance of a BeeMutation needs to be more than 0 and less than 1!");

        if (firstParent.compareTo(secondParent) < 0) {
            this.firstParent = firstParent;
            this.secondParent = secondParent;
        } else {
            this.firstParent = secondParent;
            this.secondParent = firstParent;
        }

        this.child = child;
        this.chance = chance;
    }

    public String getFirstParent() {
        return firstParent;
    }

    public String getSecondParent() {
        return secondParent;
    }

    public String getChild() {
        return child;
    }

    public double getChance() {
        return chance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BeeMutation)) {
            return false;
        }


        BeeMutation other = (BeeMutation) o;

        return firstParent.equals(other.getFirstParent()) && secondParent.equals(other.getSecondParent());
    }

}
