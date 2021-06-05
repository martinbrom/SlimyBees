package cz.martinbrom.slimybees.core;

import org.bukkit.entity.Player;

import cz.martinbrom.slimybees.SlimyBeesPlugin;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import cz.martinbrom.slimybees.items.bees.BeeAtlas;

public class BeeAtlasHistory {

    private final SlimyBeesPlayerProfile profile;

    private int page = 1;
    private AlleleSpecies item = null;

    public BeeAtlasHistory(SlimyBeesPlayerProfile profile) {
        this.profile = profile;
    }

    public void back(BeeAtlas atlas) {
        item = null;
        atlas.openListPage(profile.getPlayer(), page);
    }

    public void openLast(BeeAtlas atlas) {
        Player p = profile.getPlayer();
        if (item != null) {
            atlas.openDetailPage(p, item);
        } else {
            atlas.openListPage(p, page);
        }
    }

    public void openNextPage(BeeAtlas atlas) {
        Player p = profile.getPlayer();
        if (page * BeeAtlas.ITEMS_PER_PAGE < SlimyBeesPlugin.getAlleleRegistry().getSpeciesCount()) {
            page++;
            atlas.openListPage(p, page);
        }
    }

    public void openPreviousPage(BeeAtlas atlas) {
        Player p = profile.getPlayer();
        if (page > 1) {
            page--;
            atlas.openListPage(p, page);
        }
    }

    public void openDetailPage(BeeAtlas atlas, AlleleSpecies species) {
        Player p = profile.getPlayer();
        item = species;
        atlas.openDetailPage(p, species);
    }

}
