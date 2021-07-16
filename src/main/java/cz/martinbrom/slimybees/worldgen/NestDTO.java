package cz.martinbrom.slimybees.worldgen;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

@ParametersAreNonnullByDefault
public class NestDTO {

    private final World.Environment environment;
    private final Biome[] biomes;
    private final Material[] floorMaterials;
    private final double spawnChance;

    private String nestId;

    public NestDTO(World.Environment environment, Biome[] biomes, Material[] floorMaterials, double spawnChance) {
        Validate.notNull(environment, "Nest environment cannot be null!");
        Validate.notEmpty(biomes, "Nest biomes cannot be null or empty!");
        Validate.noNullElements(biomes, "Nest biomes cannot contain null!");
        Validate.notEmpty(floorMaterials, "Nest floor materials cannot be null or empty!");
        Validate.noNullElements(floorMaterials, "Nest floor materials cannot contain null!");
        Validate.isTrue(spawnChance > 0 && spawnChance <= 1, "Spawn chance must be between 0% (exclusive) and 100% (inclusive)!");

        this.environment = environment;
        this.biomes = biomes;
        this.floorMaterials = floorMaterials;
        this.spawnChance = spawnChance;
    }

    @Nonnull
    public World.Environment getEnvironment() {
        return environment;
    }

    @Nonnull
    public Biome[] getBiomes() {
        return biomes;
    }

    @Nonnull
    public Material[] getFloorMaterials() {
        return floorMaterials;
    }

    public double getSpawnChance() {
        return spawnChance;
    }

    @Nonnull
    public String getNestId() {
        Validate.notNull(nestId, "The nest id cannot be null, you likely forgot to call setNestItemStack()!");

        return nestId;
    }

    public void setItemStack(SlimefunItemStack nestItemStack) {
        Validate.notNull(nestItemStack, "The bee nest item cannot be null!");

        nestId = nestItemStack.getItemId();
    }

}
