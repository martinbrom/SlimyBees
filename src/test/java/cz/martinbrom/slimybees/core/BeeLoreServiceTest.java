package cz.martinbrom.slimybees.core;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.items.bees.Drone;
import cz.martinbrom.slimybees.items.bees.Princess;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeeLoreServiceTest {

    private static BeeLoreService beeLoreService;

    private static Princess princess;
    private static Drone drone;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();

        // load Slimefun and SlimyBees
        Slimefun plugin = MockBukkit.load(Slimefun.class);
        MockBukkit.load(SlimyBeesPlugin.class);

        beeLoreService = SlimyBeesPlugin.getBeeLoreService();

        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "test-category"), new ItemStack(Material.HONEY_BLOCK));
        princess = new Princess(category, ItemStacks.createPrincess("TEST", "Test", false, "", "Test lore"), RecipeType.NULL, new ItemStack[9]);
        princess.register(plugin);
        drone = new Drone(category, ItemStacks.createDrone("TEST", "Test", false, "", "Test lore"), RecipeType.NULL, new ItemStack[9]);
        drone.register(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testMakeUnknown() {
        assertFalse(beeLoreService.isUnknown(princess.getItem()));
        assertFalse(beeLoreService.isUnknown(drone.getItem()));

        ItemStack unknownPrincess = beeLoreService.makeUnknown(princess.getItem());
        ItemStack unknownDrone = beeLoreService.makeUnknown(drone.getItem());

        assertTrue(beeLoreService.isUnknown(unknownPrincess));
        assertTrue(beeLoreService.isUnknown(unknownDrone));
    }

    @Test
    void testMakeUnknownOtherItem() {
        ItemStack item = new ItemStack(Material.COBBLESTONE);

        ItemStack result = beeLoreService.makeUnknown(item);

        assertFalse(beeLoreService.isUnknown(result));
    }

    @Test
    void testMakeGeneric() {
        ItemStack genericPrincess = beeLoreService.generify(princess.getItem());
        ItemStack genericDrone = beeLoreService.generify(drone.getItem());

        ItemMeta princessMeta = genericPrincess.getItemMeta();
        ItemMeta droneMeta = genericDrone.getItemMeta();

        assertNotNull(princessMeta);
        assertNotNull(droneMeta);
        assertEquals("Test Bee", princessMeta.getDisplayName());
        assertEquals("Test Bee", droneMeta.getDisplayName());

        assertLore(null, princessMeta);
        assertLore(null, droneMeta);
    }

    @Test
    void testMakeGenericCustomLore() {
        List<String> customLore = Arrays.asList("Test", "lore", "", ChatColor.RED + "line");

        ItemStack genericPrincess = beeLoreService.generify(princess.getItem(), customLore);
        ItemStack genericDrone = beeLoreService.generify(drone.getItem(), customLore);

        ItemMeta princessMeta = genericPrincess.getItemMeta();
        ItemMeta droneMeta = genericDrone.getItemMeta();

        assertLore(customLore, princessMeta);
        assertLore(customLore, droneMeta);
    }

    private void assertLore(List<String> expected, ItemMeta meta) {
        assertNotNull(meta);
        assertEquals(expected, meta.getLore());
    }

}
