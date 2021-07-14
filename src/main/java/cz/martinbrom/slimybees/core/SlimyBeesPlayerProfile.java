package cz.martinbrom.slimybees.core;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableSet;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

/**
 * This class represents holds cached player data.
 * It is basically a copy of {@link PlayerProfile} from Slimefun 4,
 * but until that class is fully extendable for addons,
 * a separate implementation is the way for this addon.
 */
@ParametersAreNonnullByDefault
public class SlimyBeesPlayerProfile {

    public static final String BEE_SPECIES_KEY = "discovered_bee_species";

    private final UUID uuid;
    private final Config beeConfig;
    private final Set<String> discoveredBees;

    private boolean dirty = false;
    private boolean markedForDeletion = false;

    private SlimyBeesPlayerProfile(UUID uuid) {
        Validate.notNull(uuid, "Cannot create a profile for null UUID!");

        this.uuid = uuid;

        beeConfig = new Config("data-storage/SlimyBees/Players/" + uuid + ".yml");

        List<String> allSpecies = SlimyBeesPlugin.getAlleleRegistry().getAllUidsByChromosomeType(ChromosomeType.SPECIES);
        discoveredBees = allSpecies.stream()
                .filter(uid -> beeConfig.contains(BEE_SPECIES_KEY + "." + uid))
                .collect(Collectors.toSet());
    }

    /**
     * Returns a {@link SlimyBeesPlayerProfile} for a given {@link OfflinePlayer}.
     * If the profile is not cached yet, loads it and puts it into the cache.
     *
     * @param p The {@link OfflinePlayer} to load the profile for
     * @return The {@link SlimyBeesPlayerProfile}
     */
    @Nonnull
    public static SlimyBeesPlayerProfile get(OfflinePlayer p) {
        Validate.notNull(p, "Cannot get a profile for null player!");

        return get(p.getUniqueId());
    }

    /**
     * Returns a {@link SlimyBeesPlayerProfile} for a given {@link OfflinePlayer}'s {@link UUID}.
     * If the profile is not cached yet, loads it and puts it into the cache.
     *
     * @param uuid The {@link OfflinePlayer}'s {@link UUID} to load the profile for
     * @return The {@link SlimyBeesPlayerProfile}
     */
    @Nonnull
    public static SlimyBeesPlayerProfile get(UUID uuid) {
        Validate.notNull(uuid, "Cannot get a profile for null UUID!");

        SlimyBeesPlayerProfile profile = find(uuid);
        if (profile == null) {
            profile = new SlimyBeesPlayerProfile(uuid);
            SlimyBeesPlugin.getRegistry().getPlayerProfiles().put(uuid, profile);
        }

        return profile;
    }

    /**
     * Returns a {@link SlimyBeesPlayerProfile} for a given {@link UUID} if
     * the profile has been cached already.
     * Does not try to load it.
     *
     * @param uuid The {@link UUID} of an {@link OfflinePlayer} to load the profile for
     * @return The {@link SlimyBeesPlayerProfile} if it has been cached, null otherwise
     */
    @Nullable
    public static SlimyBeesPlayerProfile find(UUID uuid) {
        Map<UUID, SlimyBeesPlayerProfile> playerProfiles = SlimyBeesPlugin.getRegistry().getPlayerProfiles();
        return playerProfiles.get(uuid);
    }

    /**
     * This returns the {@link Player} who this {@link SlimyBeesPlayerProfile} belongs to.
     * If the {@link Player} is offline, null will be returned.
     *
     * @return The {@link Player} of this {@link SlimyBeesPlayerProfile} or null
     */
    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Returns whether this {@link SlimyBeesPlayerProfile} should be saved.
     *
     * @return True if this {@link SlimyBeesPlayerProfile} should be saved, false otherwise
     */
    public boolean isDirty() {
        return dirty;
    }

    public void markForDeletion() {
        markedForDeletion = true;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    /**
     * Saves the bee config file
     */
    public void save() {
        beeConfig.save();
        dirty = false;
    }

    /**
     * Marks given {@link AlleleSpecies} as discovered / undiscovered.
     *
     * @param species  The {@link AlleleSpecies} to discover
     * @param discover True if the {@link AlleleSpecies} should be discovered,
     *                 false if "undiscovered"
     */
    public void discoverBee(AlleleSpecies species, boolean discover) {
        Validate.notNull(species, "The discovered bee species must not be null!");

        discoverBee(species.getUid(), discover);
    }

    /**
     * Marks a {@link AlleleSpecies} identified by given uid as discovered / undiscovered.
     *
     * @param speciesUid The uid of a {@link AlleleSpecies} to discover
     * @param discover True if the {@link AlleleSpecies} should be discovered,
     *                 false if "undiscovered"
     */
    public void discoverBee(String speciesUid, boolean discover) {
        Validate.notNull(speciesUid, "The discovered bee species uid must not be null!");

        String key = BEE_SPECIES_KEY + "." + speciesUid;
        if (discover) {
            beeConfig.setValue(key, true);
            discoveredBees.add(speciesUid);
        } else {
            beeConfig.setValue(key, null);
            discoveredBees.remove(speciesUid);
        }

        dirty = true;
    }

    /**
     * Returns whether this {@link SlimyBeesPlayerProfile}'s {@link OfflinePlayer}
     * has discovered given {@link AlleleSpecies}.
     *
     * @param species The {@link AlleleSpecies} to check
     * @return True if the player discovered given species, false otherwise
     */
    public boolean hasDiscovered(AlleleSpecies species) {
        Validate.notNull(species, "The bee species must not be null!");

        return discoveredBees.contains(species.getUid());
    }

    /**
     * Returns a {@link ImmutableSet} of all of the discovered bees
     *
     * @return {@link ImmutableSet} of all of the discovered bees
     */
    @Nonnull
    public ImmutableSet<String> getDiscoveredBees() {
        return ImmutableSet.copyOf(discoveredBees.iterator());
    }

}
