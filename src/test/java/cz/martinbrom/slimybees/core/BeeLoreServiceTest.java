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
import be.seeseemelk.mockbukkit.ServerMock;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.items.bees.Drone;
import cz.martinbrom.slimybees.items.bees.Princess;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeeLoreServiceTest {

    private static ServerMock server;
    private static BeeLoreService beeLoreService;

    private static Princess princess;
    private static Drone drone;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();

        // load Slimefun and SlimyBees
        SlimefunPlugin plugin = MockBukkit.load(SlimefunPlugin.class);
        SlimyBeesPlugin addon = MockBukkit.load(SlimyBeesPlugin.class);

        beeLoreService = SlimyBeesPlugin.getBeeLoreService();

        Category category = new Category(new NamespacedKey(plugin, "test-category"), new ItemStack(Material.HONEY_BLOCK));
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
    public void testMakeUnknown() {
        assertFalse(beeLoreService.isUnknown(princess.getItem()));
        assertFalse(beeLoreService.isUnknown(drone.getItem()));

        ItemStack unknownPrincess = beeLoreService.makeUnknown(princess.getItem());
        ItemStack unknownDrone = beeLoreService.makeUnknown(drone.getItem());

        assertTrue(beeLoreService.isUnknown(unknownPrincess));
        assertTrue(beeLoreService.isUnknown(unknownDrone));
    }

    @Test
    public void testMakeGeneric() {
        ItemStack genericPrincess = beeLoreService.generify(princess.getItem());
        ItemStack genericDrone = beeLoreService.generify(drone.getItem());

        ItemMeta princessMeta = genericPrincess.getItemMeta();
        ItemMeta droneMeta = genericDrone.getItemMeta();

        assertNotNull(princessMeta);
        assertNotNull(droneMeta);
        assertEquals("Test Bee", princessMeta.getDisplayName());
        assertEquals("Test Bee", droneMeta.getDisplayName());

        assertTrue(princessMeta.getLore().isEmpty());
        assertTrue(droneMeta.getLore().isEmpty());
    }

    @Test
    public void testMakeGenericCustomLore() {
        List<String> customLore = Arrays.asList("Test", "lore", "", ChatColor.RED + "line");

        ItemStack genericPrincess = beeLoreService.generify(princess.getItem(), customLore);
        ItemStack genericDrone = beeLoreService.generify(drone.getItem(), customLore);

        ItemMeta princessMeta = genericPrincess.getItemMeta();
        ItemMeta droneMeta = genericDrone.getItemMeta();

        assertEquals(customLore, princessMeta.getLore());
        assertEquals(customLore, droneMeta.getLore());
    }

}
