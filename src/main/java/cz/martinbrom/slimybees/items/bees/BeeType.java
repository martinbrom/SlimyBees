package cz.martinbrom.slimybees.items.bees;

import javax.annotation.Nonnull;

public enum BeeType {
    ENDER("&5Ender"),
    FOREST("&2Forest");

    private final String displayName;

    BeeType(String displayName) {
        this.displayName = displayName;
    }

    @Nonnull
    public String getType() {
        return name() + "_BEE";
    }

    @Nonnull
    public String getDisplayName() {
        return displayName + " Bee";
    }
}
