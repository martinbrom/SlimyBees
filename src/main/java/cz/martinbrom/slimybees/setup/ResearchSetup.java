package cz.martinbrom.slimybees.setup;

import javax.annotation.Nonnull;

import cz.martinbrom.slimybees.SlimyBees;

/**
 * This is the place where all researches from SlimyBees are registered.
 */
public class ResearchSetup {

    private static boolean initialized = false;

    // prevent instantiation
    private ResearchSetup() {
    }

    public static void setup(@Nonnull SlimyBees plugin) {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees Research can only be registered once!");
        }

        initialized = true;
    }

}
