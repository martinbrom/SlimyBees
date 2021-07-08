package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BreedingModifierDTO {

    public static final BreedingModifierDTO DEFAULT = new BreedingModifierDTO(1);

    private final double productionModifier;

    public BreedingModifierDTO(double productionModifier) {
        this.productionModifier = productionModifier;
    }

    @Nonnull
    public BreedingModifierDTO combine(BreedingModifierDTO other) {
        return new BreedingModifierDTO(getProductionModifier() * other.getProductionModifier());
    }

    public double getProductionModifier() {
        return productionModifier;
    }

}
