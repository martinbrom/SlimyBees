package cz.martinbrom.slimybees.core;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

/**
 * This class represents holds cached player data.
 * It is basically a copy of {@link PlayerProfile} from Slimefun 4,
 * but until that class is fully extendable for addons,
 * a separate implementation is the way for this addon.
 */
public class SlimyBeesPlayerProfile {

    private final UUID uuid;
    private final String name;

    private final Config beeConfig;

    private boolean dirty = false;
    private boolean markedForDeletion = false;

//    private final List<String> discoveredBees;

    public SlimyBeesPlayerProfile(@Nonnull OfflinePlayer p) {
        this.uuid = p.getUniqueId();
        this.name = p.getName();

        beeConfig = new Config("data-storage/SlimyBees/Players/" + uuid + ".yml");

//        discoveredBees = SlimyBeesPlugin.getRegistry().getBeeTypes()
//                .stream()
//                .filter(bee -> beeConfig.contains("bees." + bee))
//                .collect(Collectors.toList());
    }

    @Nonnull
    public static Optional<SlimyBeesPlayerProfile> find(@Nonnull OfflinePlayer p) {
        return Optional.ofNullable(SlimyBeesPlugin.getRegistry().getPlayerProfiles().get(p.getUniqueId()));
    }

    public void markForDeletion() {
        markedForDeletion = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void save() {
        beeConfig.save();
        dirty = false;
    }

    public void discoverBee(String beeId) {
        Validate.notNull(beeId, "The discovered bee must not be null!");
        dirty = true;

        beeConfig.setValue("bees." + beeId, true);
//        discoveredBees.add(beeId);
    }

    public boolean hasDiscovered(String beeId) {
//        return discoveredBees.contains(beeId);
        return true;
    }

}
