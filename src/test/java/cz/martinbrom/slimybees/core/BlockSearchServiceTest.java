package cz.martinbrom.slimybees.core;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.WorldMock;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockSearchServiceTest {

    private static BlockSearchService searchService;

    private World world;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();

        // load Slimefun and SlimyBees
        MockBukkit.load(Slimefun.class);
        MockBukkit.load(SlimyBeesPlugin.class);
    }

    @BeforeEach
    public void setUp() {
        searchService = new BlockSearchService();

        world = new WorldMock(Material.AIR, 0);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testContainsBlock() {
        Block centerBlock = world.getBlockAt(0, 10, 0);

        Material material = Material.SUNFLOWER;
        centerBlock.getRelative(1, 0, -2).setType(material);

        assertTrue(searchService.containsBlock(centerBlock, 3, material));
    }

    @Test
    void testContainsBlockBorderInside() {
        Block centerBlock = world.getBlockAt(0, 10, 0);

        Material material = Material.SUNFLOWER;
        centerBlock.getRelative(0, 0, 3).setType(material);

        assertTrue(searchService.containsBlock(centerBlock, 3, material));
    }

    @Test
    void testContainsBlockBorderOutside() {
        Block centerBlock = world.getBlockAt(0, 10, 0);

        Material material = Material.SUNFLOWER;
        centerBlock.getRelative(0, 0, 4).setType(material);

        assertFalse(searchService.containsBlock(centerBlock, 3, material));
    }

}
