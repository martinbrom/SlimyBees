package cz.martinbrom.slimybees.core;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ProductionResultDTO {

    private final List<ItemStack> products;
    private final int ticks;

    public ProductionResultDTO(List<ItemStack> products, int ticks) {
        this.products = products;
        this.ticks = ticks;
    }

    public List<ItemStack> getProducts() {
        return products;
    }

    public int getTicks() {
        return ticks;
    }

}
