package cz.martinbrom.slimybees.core.category;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.martinbrom.slimybees.core.genetics.alleles.AlleleSpecies;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

@ParametersAreNonnullByDefault
public class BeeAtlasNavigationService {

    public void openMainMenu(PlayerProfile profile, SlimefunGuideMode mode) {
        SlimefunGuide.openMainMenu(profile, mode, profile.getGuideHistory().getMainMenuPage());
    }

    public void openDetailPage(PlayerProfile profile, AlleleSpecies species, BeeAtlasCategoryFactory factory) {
        SlimefunGuide.openItemGroup(profile, factory.createDetail(species), SlimefunGuideMode.SURVIVAL_MODE, 1);
    }

    public void goBack(PlayerProfile profile, SlimefunGuideMode mode) {
        SlimefunGuideImplementation guide = Slimefun.getRegistry().getSlimefunGuide(mode);
        profile.getGuideHistory().goBack(guide);
    }

}
