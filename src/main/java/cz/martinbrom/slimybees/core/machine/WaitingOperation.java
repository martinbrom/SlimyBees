package cz.martinbrom.slimybees.core.machine;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;

@ParametersAreNonnullByDefault
public class WaitingOperation implements MachineOperation {

    private final int totalTicks;
    private int currentTicks = 0;

    public WaitingOperation(int totalTicks) {
        Validate.isTrue(totalTicks >= 0, "The amount of total ticks must be a positive integer or zero, received: " + totalTicks);
        this.totalTicks = totalTicks;
    }

    @Override
    public void addProgress(int num) {
        Validate.isTrue(num > 0, "Progress must be positive!");
        currentTicks += num;
    }

    @Override
    public int getProgress() {
        return currentTicks;
    }

    @Override
    public int getTotalTicks() {
        return totalTicks;
    }

}
