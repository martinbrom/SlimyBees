package cz.martinbrom.slimybees.items.multiblocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cz.martinbrom.slimybees.ItemStacks;
import cz.martinbrom.slimybees.core.recipe.AbstractRecipe;
import cz.martinbrom.slimybees.core.recipe.RandomRecipe;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.InvUtils;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

@ParametersAreNonnullByDefault
public class Centrifuge extends MultiBlockMachine {

    private final List<AbstractRecipe> centrifugeRecipes;

    public Centrifuge(Category category, SlimefunItemStack item) {
        super(category, item, new ItemStack[] {
                null, null, null,
                null, new ItemStack(Material.IRON_TRAPDOOR), null,
                null, new ItemStack(Material.DISPENSER), null
        }, BlockFace.SELF);

        centrifugeRecipes = new ArrayList<>();

        registerCentrifugeRecipes(centrifugeRecipes);
    }

    private void registerCentrifugeRecipes(List<AbstractRecipe> recipes) {
        recipes.add(new RandomRecipe(ItemStacks.DRY_COMB).addOutput(ItemStacks.HONEY_DROP, 0.2));
        recipes.add(new RandomRecipe(ItemStacks.HONEY_COMB).addOutput(ItemStacks.HONEY_DROP, 0.5));
        recipes.add(new RandomRecipe(ItemStacks.SWEET_COMB).addOutput(ItemStacks.HONEY_DROP, 0.8));
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block dispBlock = b.getRelative(BlockFace.DOWN);
        BlockState state = PaperLib.getBlockState(dispBlock, false).getState();

        if (state instanceof Dispenser) {
            Dispenser dispenser = (Dispenser) state;
            Inventory inv = dispenser.getInventory();

            boolean shouldConsume = false;
            Inventory outputInv = null;
            for (ItemStack current : inv.getContents()) {
                for (AbstractRecipe recipe : getCentrifugeRecipes()) {
                    if (recipe != null && SlimefunUtils.isItemSimilar(current, recipe.getInput(), true)) {
                        List<ItemStack> outputs = recipe.getOutputs();

                        for (ItemStack output : outputs) {
                            // we "cache" output chests in between the iterations
                            if (outputInv == null || !InvUtils.fits(outputInv, output)) {
                                outputInv = findOutputInventory(output, dispBlock, inv);
                            }

                            // no eligible inventory was found
                            if (outputInv == null) {
                                SlimefunPlugin.getLocalization().sendMessage(p, "machines.full-inventory", true);
                                break;
                            }

                            outputInv.addItem(output);
                            shouldConsume = true;
                        }

                        if (outputs.isEmpty() || shouldConsume) {
                            ItemStack removing = current.clone();
                            removing.setAmount(recipe.getInput().getAmount());
                            inv.removeItem(removing);

                            p.getWorld().playEffect(b.getLocation(), Effect.IRON_TRAPDOOR_CLOSE, 1);
                        }

                        return;
                    }
                }
            }

            SlimefunPlugin.getLocalization().sendMessage(p, "machines.unknown-material", true);
        }
    }

    @Nonnull
    public List<AbstractRecipe> getCentrifugeRecipes() {
        return centrifugeRecipes;
    }

    @Override
    protected void registerDefaultRecipes(@Nonnull List<ItemStack> recipes) {
        CustomItem anyComb = new CustomItem(Material.HONEYCOMB, ChatColor.YELLOW + "Any comb");
        recipes.add(anyComb);
        recipes.add(ItemStacks.HONEY_DROP);

        recipes.add(anyComb);
        recipes.add(new CustomItem(Material.DIAMOND,
                ChatColor.YELLOW + "Bee product",
                ChatColor.YELLOW + "Consult the Bee Atlas or the addon wiki",
                ChatColor.YELLOW + "for more information"));
    }

}
