package cz.martinbrom.slimybees.items.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.generator.BlockPopulator;

import cz.martinbrom.slimybees.items.bees.AnalyzedBee;
import cz.martinbrom.slimybees.items.bees.UnknownBee;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

public class SlimyBeesRegistry {

    private final List<BlockPopulator> populators = new ArrayList<>();
    private final Map<String, Pair<AnalyzedBee, UnknownBee>> beeTypes = new HashMap<>();
    private final BeeMutationTree beeTree = new BeeMutationTree();

    private final Map<UUID, SlimyBeesPlayerProfile> profiles = new HashMap<>();

    public List<BlockPopulator> getPopulators() {
        return populators;
    }

    public Map<String, Pair<AnalyzedBee, UnknownBee>> getBeeTypes() {
        return beeTypes;
    }

    public Map<UUID, SlimyBeesPlayerProfile> getPlayerProfiles() {
        return profiles;
    }

    public BeeMutationTree getBeeMutationTree() {
        return beeTree;
    }

}
