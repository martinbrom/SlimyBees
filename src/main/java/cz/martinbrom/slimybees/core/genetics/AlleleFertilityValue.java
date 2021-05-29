package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;

public class AlleleFertilityValue {
    public static final int LOW = 1;
    public static final int AVERAGE = 2;
    public static final int GOOD = 3;
    public static final int GREAT = 4;

    @Nonnull
    public static String fromValue(int value) {
        switch (value) {
            case 1: return "LOW";
            case 2: return "AVERAGE";
            case 3: return "GOOD";
            case 4: return "HIGH";
        }

        return "UNKNOWN";
    }
}
