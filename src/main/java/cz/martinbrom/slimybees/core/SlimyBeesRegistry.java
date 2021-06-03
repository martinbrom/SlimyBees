package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.generator.BlockPopulator;

public class SlimyBeesRegistry {

    private final List<BlockPopulator> populators = new ArrayList<>();
    private final Map<UUID, SlimyBeesPlayerProfile> profiles = new HashMap<>();

    public List<BlockPopulator> getPopulators() {
        return populators;
    }

    public Map<UUID, SlimyBeesPlayerProfile> getPlayerProfiles() {
        return profiles;
    }

}
