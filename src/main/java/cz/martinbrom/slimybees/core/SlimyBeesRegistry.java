package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cz.martinbrom.slimybees.worldgen.AbstractNestPopulator;

public class SlimyBeesRegistry {

    private final List<AbstractNestPopulator> nestPopulators = new ArrayList<>();
    private final Map<UUID, SlimyBeesPlayerProfile> profiles = new HashMap<>();

    public List<AbstractNestPopulator> getNestPopulators() {
        return nestPopulators;
    }

    public Map<UUID, SlimyBeesPlayerProfile> getPlayerProfiles() {
        return profiles;
    }

}
