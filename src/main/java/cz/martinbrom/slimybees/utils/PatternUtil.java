package cz.martinbrom.slimybees.utils;

import java.util.regex.Pattern;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;

public class PatternUtil {

    // prevent instantiation
    private PatternUtil() {}

    public static final Pattern TOME_OWNER_LORE = Pattern.compile(ChatColors.color("&7Owner: &b") + "\\w+");

    public static final Pattern LOWER_SNAKE = Pattern.compile("[a-z_]+");
    public static final Pattern UPPER_SNAKE = Pattern.compile("[A-Z_]+");

    public static final Pattern SPECIES_UID_PATTERN = Pattern.compile("species:" + LOWER_SNAKE);
    public static final Pattern UID_PATTERN = Pattern.compile(LOWER_SNAKE + ":" + LOWER_SNAKE);

}
