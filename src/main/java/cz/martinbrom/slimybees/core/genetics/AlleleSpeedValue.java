package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;

public class AlleleSpeedValue {
    public static final int VERY_SLOW = 3;
    public static final int SLOW = 4;
    public static final int AVERAGE = 6;
    public static final int FAST = 8;
    public static final int VERY_FAST = 10;

    @Nonnull
    public static String fromValue(int value) {
        switch (value) {
            case 3: return "VERY_SLOW";
            case 4: return "SLOW";
            case 6: return "AVERAGE";
            case 8: return "FAST";
            case 10: return "VERY_FAST";
        }

        return "UNKNOWN";
    }
}
