package cz.martinbrom.slimybees.items.core;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BeeMutation {

    private final String firstParent;
    private final String secondParent;
    private final String child;
    private final double chance;

    public BeeMutation(String firstParent, String secondParent, String child, double chance) {
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
