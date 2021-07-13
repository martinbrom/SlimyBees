package cz.martinbrom.slimybees.utils;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import com.google.common.base.CaseFormat;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;

@ParametersAreNonnullByDefault
public class StringUtils {

    @Nonnull
    public static String snakeToCamel(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, s);
    }

    @Nonnull
    public static String camelToSnake(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, s);
    }

    @Nonnull
    public static String nameToUid(ChromosomeType type, String name) {
        Validate.notNull(type, "Given chromosome type cannot be null!");
        Validate.notEmpty(name, "Given species name cannot be null or empty!");

        return (type.name() + "." + name).toLowerCase(Locale.ROOT);
    }

    @Nonnull
    public static String uidToName(String uid) {
        if (uid.isEmpty()) {
            return "";
        }

        String[] uidParts = uid.split("\\.");
        return uidParts.length == 1 ? "" : uidParts[1].toUpperCase(Locale.ROOT);
    }

}
