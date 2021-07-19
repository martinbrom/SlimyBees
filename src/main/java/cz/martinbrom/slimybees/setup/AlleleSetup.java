package cz.martinbrom.slimybees.setup;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleRegistry;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleService;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleValue;
import cz.martinbrom.slimybees.core.genetics.enums.ChromosomeType;
import io.github.thebusybiscuit.slimefun4.utils.FireworkUtils;

public class AlleleSetup {

    public static final String DEFAULT_PRODUCTIVITY_UID = AlleleUids.PRODUCTIVITY_NORMAL;
    public static final String DEFAULT_FERTILITY_UID = AlleleUids.FERTILITY_NORMAL;
    public static final String DEFAULT_LIFESPAN_UID = AlleleUids.LIFESPAN_NORMAL;
    public static final String DEFAULT_RANGE_UID = AlleleUids.RANGE_NORMAL;
    public static final String DEFAULT_PLANT_UID = AlleleUids.PLANT_NONE;
    public static final String DEFAULT_EFFECT_UID = AlleleUids.EFFECT_NONE;

    private static final Color[] COLORS = {
            Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA,
            Color.GRAY, Color.GREEN, Color.LIME, Color.MAROON,
            Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE,
            Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW
    };

    private static boolean initialized = false;

    // prevent instantiation
    private AlleleSetup() {}

    public static void setUp() {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees Alleles can only be registered once!");
        }

        initialized = true;

        AlleleRegistry alleleRegistry = SlimyBeesPlugin.getAlleleRegistry();

        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(0.5, true), AlleleUids.PRODUCTIVITY_VERY_LOW);
        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(0.75), AlleleUids.PRODUCTIVITY_LOW);
        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(1.0, true), AlleleUids.PRODUCTIVITY_NORMAL);
        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(1.5), AlleleUids.PRODUCTIVITY_HIGH);
        alleleRegistry.register(ChromosomeType.PRODUCTIVITY, new AlleleValue<>(2.0), AlleleUids.PRODUCTIVITY_VERY_HIGH);

        alleleRegistry.register(ChromosomeType.FERTILITY, new AlleleValue<>(1, true), AlleleUids.FERTILITY_LOW);
        alleleRegistry.register(ChromosomeType.FERTILITY, new AlleleValue<>(2, true), AlleleUids.FERTILITY_NORMAL);
        alleleRegistry.register(ChromosomeType.FERTILITY, new AlleleValue<>(3), AlleleUids.FERTILITY_HIGH);
        alleleRegistry.register(ChromosomeType.FERTILITY, new AlleleValue<>(4), AlleleUids.FERTILITY_VERY_HIGH);

        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(15), AlleleUids.LIFESPAN_VERY_SHORT);
        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(25), AlleleUids.LIFESPAN_SHORT);
        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(30, true), AlleleUids.LIFESPAN_NORMAL);
        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(40), AlleleUids.LIFESPAN_LONG);
        alleleRegistry.register(ChromosomeType.LIFESPAN, new AlleleValue<>(60, true), AlleleUids.LIFESPAN_VERY_LONG);

        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(1), AlleleUids.RANGE_VERY_SHORT);
        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(2, true), AlleleUids.RANGE_SHORT);
        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(3, true), AlleleUids.RANGE_NORMAL);
        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(4), AlleleUids.RANGE_LONG);
        alleleRegistry.register(ChromosomeType.RANGE, new AlleleValue<>(5), AlleleUids.RANGE_VERY_LONG);

        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.AIR), AlleleUids.PLANT_NONE);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.OXEYE_DAISY, true), AlleleUids.PLANT_OXEYE_DAISY);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.WHEAT, true), AlleleUids.PLANT_WHEAT);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.SUGAR_CANE, true), AlleleUids.PLANT_SUGAR_CANE);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.MELON, true), AlleleUids.PLANT_MELON);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.PUMPKIN, true), AlleleUids.PLANT_PUMPKIN);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.POTATOES, true), AlleleUids.PLANT_POTATO);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.CARROTS, true), AlleleUids.PLANT_CARROT);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.BEETROOTS, true), AlleleUids.PLANT_BEETROOT);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.COCOA, true), AlleleUids.PLANT_COCOA);
        alleleRegistry.register(ChromosomeType.PLANT, new AlleleValue<>(Material.SWEET_BERRY_BUSH), AlleleUids.PLANT_BERRY);
        // TODO: 18.07.21 Glow Berries

        alleleRegistry.registerEffect(new AlleleValue<>((l, r) -> {}), AlleleUids.EFFECT_NONE);
        alleleRegistry.registerEffect(new AlleleValue<>(createEffect(PotionEffectType.REGENERATION)), AlleleUids.EFFECT_NONE);
        // TODO: 19.07.21 Fireworks are for testing only
        alleleRegistry.registerEffect(new AlleleValue<>((l, r) -> {
            World world = l.getWorld();
            if (world != null) {
                double x = ThreadLocalRandom.current().nextDouble(l.getX() - r, l.getX() + r);
                double z = ThreadLocalRandom.current().nextDouble(l.getZ() - r, l.getZ() + r);
                Color color = COLORS[ThreadLocalRandom.current().nextInt(COLORS.length)];

                // TODO: 19.07.21 Might need to move a bit up so it doesn't get stuck in the hive
                FireworkUtils.launchFirework(new Location(world, x, l.getY(), z), color);
            }
        }), AlleleUids.EFFECT_FIREWORK);

        BeeRegistry beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        AlleleService alleleService = SlimyBeesPlugin.getAlleleService();
        Allele[] defaultTemplate = new Allele[ChromosomeType.CHROMOSOME_COUNT];

        alleleService.set(defaultTemplate, ChromosomeType.PRODUCTIVITY, DEFAULT_PRODUCTIVITY_UID);
        alleleService.set(defaultTemplate, ChromosomeType.FERTILITY, DEFAULT_FERTILITY_UID);
        alleleService.set(defaultTemplate, ChromosomeType.LIFESPAN, DEFAULT_LIFESPAN_UID);
        alleleService.set(defaultTemplate, ChromosomeType.RANGE, DEFAULT_RANGE_UID);
        alleleService.set(defaultTemplate, ChromosomeType.PLANT, DEFAULT_PLANT_UID);
        alleleService.set(defaultTemplate, ChromosomeType.EFFECT, DEFAULT_EFFECT_UID);

        beeRegistry.registerDefaultTemplate(defaultTemplate);

    }

    @Nonnull
    public static BiConsumer<Location, Integer> createEffect(PotionEffectType type) {
        return createEffect(type, 1);
    }

    @Nonnull
    public static BiConsumer<Location, Integer> createEffect(PotionEffectType type, int amplifier) {
        return (l, r) -> {
            World world = l.getWorld();
            if (world != null) {
                // TODO: 19.07.21 Calling instanceof twice, wouldn't it be faster to only check inside the loop?
                Collection<Entity> entities = world.getNearbyEntities(l, r, 1.5, r, n -> n instanceof LivingEntity && n.isValid());
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity) {
                        // TODO: 19.07.21 Configurable duration?
                        ((LivingEntity) entity).addPotionEffect(type.createEffect(400, amplifier));
                    }
                }
            }
        };
    }

}
