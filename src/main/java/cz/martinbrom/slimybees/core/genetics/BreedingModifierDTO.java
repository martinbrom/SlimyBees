package cz.martinbrom.slimybees.core.genetics;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BreedingModifierDTO {

    public static final BreedingModifierDTO DEFAULT = new BreedingModifierDTO(1, 1);

    private final double productionModifier;
    private final double lifespanModifier;

    public BreedingModifierDTO(double productionModifier, double lifespanModifier) {
        this.productionModifier = productionModifier;
        this.lifespanModifier = lifespanModifier;
    }

    @Nonnull
    public BreedingModifierDTO combine(BreedingModifierDTO other) {
        return new BreedingModifierDTO(getProductionModifier() * other.getProductionModifier(),
                getLifespanModifier() * other.getLifespanModifier());
    }

    public double getProductionModifier() {
        return productionModifier;
    }

    public double getLifespanModifier() {
        return lifespanModifier;
    }

}
