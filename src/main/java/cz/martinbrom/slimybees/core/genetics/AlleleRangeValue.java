package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;

public class AlleleRangeValue {

    public static final int TINY = 1;
    public static final int SMALL = 2;
    public static final int AVERAGE = 3;
    public static final int LARGE = 4;
    public static final int HUGE = 6;

    @Nonnull
    public static String fromValue(int value) {
        switch (value) {
            case 1:
                return "TINY";
            case 2:
                return "SMALL";
            case 3:
                return "AVERAGE";
            case 4:
                return "LARGE";
            case 6:
                return "HUGE";
        }

        return "UNKNOWN";
    }

}
