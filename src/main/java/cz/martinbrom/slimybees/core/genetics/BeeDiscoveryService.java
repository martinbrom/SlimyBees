package cz.martinbrom.slimybees.core.genetics;

import org.bukkit.entity.Player;

import cz.martinbrom.slimybees.core.SlimyBeesPlayerProfile;
import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;

public class BeeDiscoveryService {

    public void discover(Player p, Genome genome, boolean discover) {
        SlimyBeesPlayerProfile profile = SlimyBeesPlayerProfile.get(p);

        AlleleSpecies species = genome.getSpecies();
        if (!profile.hasDiscovered(species)) {
            // TODO: 04.06.21 Fireworks probably
            profile.discoverBee(species, discover);
        }
    }

}
