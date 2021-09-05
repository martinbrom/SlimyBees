package cz.martinbrom.slimybees.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;

import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import cz.martinbrom.slimybees.utils.SlimyBeesHeadTexture;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ParametersAreNonnullByDefault
public class TestUtils {

    // prevent instantiation
    private TestUtils() {}

    @Nonnull
    public static PlayerProfile awaitProfile(OfflinePlayer player) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<PlayerProfile> ref = new AtomicReference<>();

        // This loads the profile asynchronously
        Assertions.assertFalse(PlayerProfile.get(player, profile -> {
            ref.set(profile);
            latch.countDown();
        }));

        latch.await(2, TimeUnit.SECONDS);
        return ref.get();
    }

    @Nonnull
    public static Allele mockAllele(String uid) {
        Allele allele = mock(Allele.class);
        when(allele.getUid()).thenReturn(uid);

        return allele;
    }

    @Nonnull
    public static AlleleSpecies registerSpecies(AlleleRegistry alleleRegistry, String uid, String name) {
        return registerSpecies(alleleRegistry, uid, name, false);
    }

    @Nonnull
    public static AlleleSpecies registerSpecies(AlleleRegistry alleleRegistry, String uid, String name, boolean secret) {
        AlleleSpecies species = new AlleleSpecies(uid, name, false, secret);
        ItemStack droneItem = new CustomItemStack(SlimyBeesHeadTexture.DRONE.getAsItemStack(), species.getDisplayName() + " Drone");
        species.setDroneItemStack(droneItem);

        when(alleleRegistry.get(ChromosomeType.SPECIES, uid)).thenReturn(species);

        return species;
    }

    public static void assertDisplayName(String expected, @Nullable ItemStack item) {
        assertNotNull(item);
        assertNotNull(item.getItemMeta());
        assertEquals(expected, item.getItemMeta().getDisplayName());
    }
}
