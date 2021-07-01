package cz.martinbrom.slimybees.utils;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.CaseFormat;

@ParametersAreNonnullByDefault
public class StringUtils {

    @Nonnull
    public static String capitalize(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase(Locale.ROOT);
    }

    @Nonnull
    public static String snakeToCamel(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, s);
    }

}
