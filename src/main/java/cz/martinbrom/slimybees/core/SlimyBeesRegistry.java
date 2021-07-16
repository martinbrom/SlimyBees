package cz.martinbrom.slimybees.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.block.Biome;

import cz.martinbrom.slimybees.worldgen.NestDTO;

@ParametersAreNonnullByDefault
public class SlimyBeesRegistry {

    private final Map<UUID, SlimyBeesPlayerProfile> profiles = new HashMap<>();

    // could use a HashMap but there are only three so it makes almost no difference
    private final Map<Biome, List<NestDTO>> overworldNestMap = new ConcurrentHashMap<>();
    private final Map<Biome, List<NestDTO>> netherNestMap = new ConcurrentHashMap<>();
    private final Map<Biome, List<NestDTO>> endNestMap = new ConcurrentHashMap<>();

    @Nonnull
    public Map<UUID, SlimyBeesPlayerProfile> getPlayerProfiles() {
        return profiles;
    }

    /**
     * Registers a new nest that can be generated in the world.
     *
     * @param nest New {@link NestDTO} to register for world-gen
     */
    public void registerNest(NestDTO nest) {
        Validate.notNull(nest, "Cannot register a null nest!");

        World.Environment env = nest.getEnvironment();
        Map<Biome, List<NestDTO>> nestMap = getNestMapForEnvironment(env);
        if (nestMap != null) {
            for (Biome biome : nest.getBiomes()) {
                nestMap.computeIfAbsent(biome, k -> new ArrayList<>()).add(nest);
            }
        }
    }

    @Nonnull
    public List<NestDTO> getNestsForBiome(World world, Biome biome) {
        Map<Biome, List<NestDTO>> nestMap = getNestMapForWorld(world);
        if (nestMap == null) {
            return Collections.emptyList();
        }

        List<NestDTO> nests = nestMap.get(biome);
        return nests == null ? Collections.emptyList() : nests;
    }

    @Nullable
    private Map<Biome, List<NestDTO>> getNestMapForWorld(World world) {
        Validate.notNull(world, "Cannot get the nest map for null world!");

        return getNestMapForEnvironment(world.getEnvironment());
    }

    @Nullable
    private Map<Biome, List<NestDTO>> getNestMapForEnvironment(World.Environment env) {
        if (env == World.Environment.NORMAL) {
            return overworldNestMap;
        } else if (env == World.Environment.NETHER) {
            return netherNestMap;
        } else if (env == World.Environment.THE_END) {
            return endNestMap;
        }

        // TODO: 15.07.21 Support for custom dimensions?
        return null;
    }

}
