package cz.martinbrom.slimybees.core.genetics.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.Categories;
import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.SlimyBeesRegistry;
import cz.martinbrom.slimybees.core.genetics.BeeGeneticService;
import cz.martinbrom.slimybees.core.genetics.BeeMutation;
import cz.martinbrom.slimybees.core.genetics.BeeRegistry;
import cz.martinbrom.slimybees.core.genetics.Genome;
import cz.martinbrom.slimybees.core.genetics.alleles.Allele;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleHelper;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpeciesImpl;
import cz.martinbrom.slimybees.items.bees.AnalyzedBee;
import cz.martinbrom.slimybees.items.bees.UnknownBee;
import cz.martinbrom.slimybees.utils.Tuple;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;

@ParametersAreNonnullByDefault
public enum BeeType {

    FOREST(true, ChatColor.DARK_GREEN) {
        @Override
        protected void setAlleles(Allele[] alleles) {
            AlleleHelper.set(alleles, ChromosomeTypeImpl.FERTILITY, AlleleType.Fertility.HIGH);
            AlleleHelper.set(alleles, ChromosomeTypeImpl.SPEED, AlleleType.Speed.VERY_SLOW);
        }
    },
    ENDER(true, ChatColor.DARK_PURPLE) {
        @Override
        protected void setAlleles(Allele[] alleles) {
            AlleleHelper.set(alleles, ChromosomeTypeImpl.FERTILITY, AlleleType.Fertility.LOW);
            AlleleHelper.set(alleles, ChromosomeTypeImpl.SPEED, AlleleType.Speed.NORMAL);
        }
    },
    MUTATED(false, ChatColor.GOLD) {
        @Override
        protected void setAlleles(Allele[] alleles) {
            AlleleHelper.set(alleles, ChromosomeTypeImpl.SPEED, AlleleType.Speed.VERY_FAST);
        }

        @Override
        protected void registerMutations() {
            registerMutation(FOREST, ENDER, 0.2);
        }
    };

    private static boolean initialized = false;

    private final AlleleSpecies species;
    private final ChatColor color;

    private Genome genome;
    private Allele[] template;

    BeeType(boolean dominant, ChatColor color) {
        this(dominant, color, false);
    }

    BeeType(boolean dominant, ChatColor color, boolean enchanted) {
        Validate.notNull(color, "BeeType color cannot be null!");

        this.color = color;

        String lowercaseName = toString().toLowerCase(Locale.ENGLISH);
        String name = lowercaseName.substring(0, 1).toUpperCase() + lowercaseName.substring(1);
        String uid = "species." + lowercaseName;

        species = new AlleleSpeciesImpl(uid, name, dominant, enchanted);

        List<Pair<ItemStack, Double>> products = new ArrayList<>();
        setProducts(products);
        species.setProducts(products);

        SlimyBeesPlugin.getAlleleRegistry().registerAllele(species, ChromosomeTypeImpl.SPECIES);
    }

    public static void setUp() {
        if (initialized) {
            throw new UnsupportedOperationException("SlimyBees bees can only be registered once!");
        }

        initialized = true;

        for (BeeType type : values()) {
            type.register();
        }

        for (BeeType type : values()) {
            type.registerMutations();
        }
    }

    @Nonnull
    public final Allele[] getTemplate() {
        return template;
    }

    protected void setProducts(List<Pair<ItemStack, Double>> products) {
        // default does nothing
    }

    protected void setAlleles(Allele[] alleles) {
        // default does nothing
    }

    protected void registerMutations() {
        // default does nothing
    }

    protected final void registerMutation(BeeType firstParent, BeeType secondParent, double chance) {
        String firstParentUid = firstParent.species.getUid();
        String secondParentUid = secondParent.species.getUid();
        String childUid = species.getUid();

        BeeMutation mutation = new BeeMutation(firstParentUid, secondParentUid, childUid, chance);
        SlimyBeesPlugin.getRegistry().getBeeMutationTree().registerMutation(mutation);
    }

    private void register() {
        BeeRegistry beeRegistry = SlimyBeesPlugin.getBeeRegistry();
        template = beeRegistry.getDefaultTemplate();

        setAlleles(template);
        AlleleHelper.set(template, ChromosomeTypeImpl.SPECIES, species);

        genome = BeeGeneticService.getGenomeFromAlleles(template);

        registerItemStacks();

        beeRegistry.registerTemplate(template);
    }

    private void registerItemStacks() {
        String coloredName = color + species.getName();
        String uppercaseName = species.getName().toUpperCase(Locale.ENGLISH);
        SlimefunItemStack unknown = ItemStacks.createBee("_UNKNOWN_" + uppercaseName, coloredName, "", "&8<unknown>");
        SlimefunItemStack analyzed = ItemStacks.createBee(uppercaseName, coloredName);

        BeeGeneticService.updateItemGenome(unknown, genome);
        BeeGeneticService.updateItemGenome(analyzed, genome);

        UnknownBee unknownBee = new UnknownBee(Categories.GENERAL, unknown, RecipeType.NULL, new ItemStack[9]);
        AnalyzedBee analyzedBee = new AnalyzedBee(Categories.GENERAL, analyzed, RecipeType.NULL, new ItemStack[9]);

        SlimyBeesRegistry registry = SlimyBeesPlugin.getRegistry();
        registry.getBeeTypes().put(species.getUid(), new Pair<>(analyzedBee, unknownBee));

        SlimyBeesPlugin plugin = SlimyBeesPlugin.instance();
        unknownBee.register(plugin);
//        unknownBee.setHidden(true);
        analyzedBee.register(plugin);
//        analyzedBee.setHidden(true);

        species.setAnalyzedItemStack(analyzed);
        species.setUnknownItemStack(unknown);
    }

}
