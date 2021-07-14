package cz.martinbrom.slimybees.core;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.utils.FireworkUtils;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

/**
 * This service handles bee discovery related logic
 */
@ParametersAreNonnullByDefault
public class BeeDiscoveryService {

    private final AlleleRegistry alleleRegistry;

    private final Config discoveryConfig;
    private final Map<String, String> discoveredSpecies = new ConcurrentHashMap<>();

    private final boolean shouldBroadcastDiscoveries;

    public BeeDiscoveryService(AlleleRegistry alleleRegistry, Config config) {
        this.alleleRegistry = alleleRegistry;

        discoveryConfig = new Config("data-storage/SlimyBees/discoveries.yml");

        List<String> speciesUids = alleleRegistry.getAllUidsByChromosomeType(ChromosomeType.SPECIES);
        for (String uid : speciesUids) {
            String discoveredBy = discoveryConfig.getString(uid);
            if (discoveredBy != null) {
                discoveredSpecies.put(uid, discoveredBy);
            }
        }

        shouldBroadcastDiscoveries = config.getBoolean("discoveries.broadcast-first-discovery");
    }

    /**
     * Returns an immutable copy of the global discoveries.
     * Each entry contains the species uid as the key and the player name as the value.
     *
     * @return Immutable copy of global discoveries
     */
    @Nonnull
    public Map<String, String> getDiscoveryInfo() {
        return ImmutableMap.copyOf(discoveredSpecies);
    }

    /**
     * Marks an {@link AlleleSpecies} stored in the given {@link Genome} as discovered.
     * Also checks whether this is the first time anyone has discovered this species.
     * If it is, marks it down and broadcasts a message about the discovery.
     *
     * @param p The {@link Player} for who the discovery should be made
     * @param genome The {@link Genome} which contains the {@link AlleleSpecies} to be discovered
     * @return True, if a change was made, false otherwise.
     */
    public boolean discover(Player p, Genome genome) {
        Validate.notNull(p, "The player cannot be null!");
        Validate.notNull(genome, "The genome cannot be null!");

        AlleleSpecies species = genome.getSpecies();
        discoverGlobal(p, species);
        return discoverInner(p, species, true);
    }

    private void discoverGlobal(Player p, AlleleSpecies species) {
        String uid = species.getUid();
        if (!discoveredSpecies.containsKey(uid)) {
            String playerName = p.getName();
            discoveredSpecies.put(uid, playerName);
            discoveryConfig.setValue(uid, playerName);

            // we could do "isDirty" (like the SlimyBeesPlayerProfile), but the global discoveries
            // are FAR rarer so saving each time is fine
            discoveryConfig.save();

            if (shouldBroadcastDiscoveries) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + playerName
                            + ChatColor.RESET + ChatColor.WHITE + " was the first one to discover the "
                            + ChatColor.BOLD + species.getDisplayName()
                            + ChatColor.RESET + ChatColor.WHITE + " species!");

                    onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                }
            }
        }
    }

    /**
     * Marks the given {@link AlleleSpecies} as discovered.
     *
     * @param p The {@link Player} for who the discovery should be made
     * @param species The {@link AlleleSpecies} to be discovered
     * @return True, if a change was made, false otherwise.
     */
    public boolean discover(Player p, AlleleSpecies species) {
        Validate.notNull(p, "The player cannot be null!");
        Validate.notNull(species, "The species cannot be null!");

        return discoverInner(p, species, true);
    }

    /**
     * Marks the given {@link AlleleSpecies} as not discovered.
     *
     * @param p The {@link Player} from who the discovery should be removed
     * @param species The {@link AlleleSpecies} to be undiscovered
     * @return True, if a change was made, false otherwise.
     */
    public boolean undiscover(Player p, AlleleSpecies species) {
        Validate.notNull(p, "The player cannot be null!");
        Validate.notNull(species, "The species cannot be null!");

        return discoverInner(p, species, false);
    }

    private boolean discoverInner(Player p, AlleleSpecies species, boolean discover) {
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);
        if (profile.hasDiscovered(species) != discover) {
            profile.discoverBee(species, discover);
            notifyPlayer(p, species.getDisplayName(), discover);
            return true;
        }

        return false;
    }

    /**
     * Marks all previously undiscovered {@link AlleleSpecies} as discovered
     * and returns the number of species discovered.
     *
     * @param p The {@link Player} for who the discoveries should be made
     * @return The number of previously undiscovered bee species
     */
    public long discoverAll(Player p) {
        return discoverAllInner(p, alleleRegistry.getAllSpecies().stream());
    }

    /**
     * Marks all bees, that the owner has discovered, as discovered
     * and returns the number of species discovered.
     *
     * @param p The {@link Player} for who the discoveries should be made
     * @param owner The owner's {@link UUID}, whose discoveries should be used
     * @return The number of previously undiscovered bee species
     */
    public long discoverAllByOwner(Player p, UUID owner) {
        SlimyBeesPlayerProfile ownerProfile = SlimyBeesPlayerProfile.get(owner);
        Stream<AlleleSpecies> ownerDiscoveredSpecies = ownerProfile.getDiscoveredBees().stream()
                .map(uid -> (AlleleSpecies) alleleRegistry.get(ChromosomeType.SPECIES, uid))
                .filter(Objects::nonNull);

        return discoverAllInner(p, ownerDiscoveredSpecies);
    }

    private long discoverAllInner(Player p, Stream<AlleleSpecies> speciesStream) {
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);

        return speciesStream
                .filter(species -> !profile.hasDiscovered(species))
                .sorted(Comparator.comparing(Allele::getName))
                .peek(species -> {
                    profile.discoverBee(species, true);
                    notifyPlayer(p, species.getDisplayName(), true);
                })
                .count();
    }

    /**
     * Marks all previously discovered {@link AlleleSpecies} as not discovered.
     *
     * @param p The {@link Player} for who the discoveries should be made
     */
    public void undiscoverAll(Player p) {
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);

        profile.getDiscoveredBees().stream()
            .sorted()
            .forEach(uid -> profile.discoverBee(uid, false));
    }

    private void notifyPlayer(Player p, String name, boolean discover) {
        if (discover) {
            FireworkUtils.launchRandom(p, 1);
            p.sendMessage(ChatColor.GREEN + "You have discovered a new species - "
                    + ChatColor.GRAY + ChatColor.BOLD + name
                    + ChatColor.RESET + ChatColor.GREEN + "!");
        }
    }

}
