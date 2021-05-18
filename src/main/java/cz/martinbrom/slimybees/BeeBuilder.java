package cz.martinbrom.slimybees;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import cz.martinbrom.slimybees.items.bees.BeeNest;
import cz.martinbrom.slimybees.setup.ItemSetup;
import cz.martinbrom.slimybees.utils.RandomizedSlimefunItemStack;
import cz.martinbrom.slimybees.worldgen.AbstractNestPopulator;
import cz.martinbrom.slimybees.worldgen.GroundNestPopulator;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class BeeBuilder {

    private final String id;
    private String name;
    private SlimefunItemStack beeItemStack;
    private SlimefunItemStack honeycombItemStack;
    private SlimefunItemStack nestItemStack;
    private AbstractNestPopulator populator;
    private BeeMutation mutation;

    private BeeBuilder(String id) {
        this.id = id;
    }

    public static BeeBuilder of(String id) {
        return new BeeBuilder(id);
    }

    public BeeBuilder setName(String name) {
        Validate.notNull(id, "You must set the bee's id before setting a name!");
        this.name = name;
        beeItemStack = ItemStacks.createBee(id, name);
        honeycombItemStack = ItemStacks.createHoneycomb(id, name);
        return this;
    }

    public BeeBuilder setNest(Biome[] validBiomes, Material[] validFloorMaterials, double chance) {
        Validate.notNull(id, "You must set the bee's id before setting a nest!");
        Validate.notNull(name, "You must set the bee's name before setting a nest!");
        Validate.isTrue(mutation == null, "You can only set a nest or a mutation, not both!");
        nestItemStack = new SlimefunItemStack(
                id + "_BEE_NEST",
                Material.BEEHIVE,
                name + " Bee Nest");
        populator = new GroundNestPopulator(validBiomes, validFloorMaterials, chance, nestItemStack);
        return this;
    }

    public BeeBuilder setMutation(BeeMutation mutation) {
        Validate.isTrue(populator == null, "You can only set a nest or a mutation, not both!");
        this.mutation = mutation;
        return this;
    }

    public void register(SlimyBeesPlugin plugin) {
        SlimyBeesRegistry registry = SlimyBeesPlugin.getRegistry();

        ItemSetup.registerAndHide(beeItemStack, plugin);

        if (honeycombItemStack != null) {
            ItemSetup.registerAndHide(honeycombItemStack, plugin);
        }

        if (mutation != null) {
            registry.getBeeMutationTree().registerMutation(mutation);
        }

        if (populator != null) {
            BeeNest nest = new BeeNest(nestItemStack, beeItemStack)
                    .addRandomDrop(new RandomizedSlimefunItemStack(honeycombItemStack, 0, 1))
                    .addRandomDrop(new RandomizedSlimefunItemStack(ItemStacks.COMMON_HONEYCOMB, 0, 3));
            nest.register(plugin);
            nest.setHidden(true);
            registry.getPopulators().add(populator);
        }
    }

}
