package cz.martinbrom.slimybees.utils;

import java.util.regex.Pattern;

import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;

public class PatternUtil {

    public static final Pattern TOME_OWNER_LORE = Pattern.compile(ChatColors.color("&7Owner: &b") + "\\w+");

}
