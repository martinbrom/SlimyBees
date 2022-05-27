package cz.martinbrom.slimybees.utils;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.CommonPatterns;

@ParametersAreNonnullByDefault
public class StringUtils {

    // prevent instantiation
    private StringUtils() {}

    @Nonnull
    public static String humanizeSnake(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        boolean spaceBefore = false;
        String[] parts = CommonPatterns.UNDERSCORE.split(s.toLowerCase(Locale.ROOT));
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (spaceBefore) {
                sb.append(' ');
            }

            sb.append(capitalize(part));
            spaceBefore = true;
        }

        return sb.toString();
    }

    @Nonnull
    public static String capitalize(String s) {
        if (s.isEmpty() || s.length() == 1) {
            return s;
        }

        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    @Nonnull
    public static String nameToUid(ChromosomeType type, String name) {
        Validate.notNull(type, "Given chromosome type cannot be null!");
        Validate.notEmpty(name, "Given species name cannot be null or empty!");

        return (type.name() + ":" + name).toLowerCase(Locale.ROOT);
    }

    @Nonnull
    public static String uidToName(String uid) {
        if (uid.isEmpty()) {
            return "";
        }

        String[] uidParts = uid.split(":");
        return uidParts.length == 1 ? "" : uidParts[1].toUpperCase(Locale.ROOT);
    }

}
