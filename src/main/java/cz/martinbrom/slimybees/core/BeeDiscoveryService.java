package cz.martinbrom.slimybees.core;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.utils.GeneticUtil;
import io.github.thebusybiscuit.slimefun4.utils.FireworkUtils;

/**
 * This service handles bee discovery related logic
 */
@ParametersAreNonnullByDefault
public class BeeDiscoveryService {

    private final AlleleRegistry alleleRegistry;

    public BeeDiscoveryService(AlleleRegistry alleleRegistry) {
        this.alleleRegistry = alleleRegistry;
    }

    /**
     * Marks an {@link AlleleSpecies} stored in the given {@link Genome} as
     * discovered / not discovered based on the value of the 'discover' argument.
     *
     * @param p The {@link Player} for which the discovery should be changed
     * @param genome The {@link Genome} which contains the {@link AlleleSpecies} to be discovered
     * @param discover True, if the species should be discovered, false otherwise
     * @return True, if a change was made, false otherwise.
     */
    public boolean discover(Player p, Genome genome, boolean discover) {
        Validate.notNull(p, "The player cannot be null!");
        Validate.notNull(genome, "The genome cannot be null!");

        AlleleSpecies species = genome.getSpecies();
        return discoverInner(p, species, discover);
    }

    /**
     * Marks the given {@link AlleleSpecies} as discovered / not discovered
     * based on the value of the 'discover' argument.
     *
     * @param p The {@link Player} for which the discovery should be changed
     * @param species The {@link AlleleSpecies} to be discovered
     * @param discover True, if the species should be discovered, false otherwise
     * @return True, if a change was made, false otherwise.
     */
    public boolean discover(Player p, AlleleSpecies species, boolean discover) {
        Validate.notNull(p, "The player cannot be null!");
        Validate.notNull(species, "The species cannot be null!");

        return discoverInner(p, species, discover);
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
     * @param p The {@link Player} for which the discoveries should be made
     * @return The number of previously undiscovered bee species
     */
    public long discoverAll(Player p) {
        return discoverAllInner(p, alleleRegistry.getAllSpecies().stream());
    }

    /**
     * Marks all bees, that the owner has discovered, as discovered
     * and returns the number of species discovered.
     *
     * @param p The {@link Player} for which the discoveries should be made
     * @param owner The owner's {@link UUID}, whose discoveries should be used
     * @return The number of previously undiscovered bee species
     */
    public long discoverAllByOwner(Player p, UUID owner) {
        SlimyBeesPlayerProfile ownerProfile = SlimyBeesPlayerProfile.get(owner);
        Stream<AlleleSpecies> ownerDiscoveredSpecies = ownerProfile.getDiscoveredBees().stream()
                .map(GeneticUtil::getSpeciesByName)
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
     * @param p The {@link Player} for which the discoveries should be made
     */
    public void undiscoverAll(Player p) {
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);

        profile.getDiscoveredBees().stream()
            .sorted()
            .forEach(name -> profile.discoverBee(name, false));
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
