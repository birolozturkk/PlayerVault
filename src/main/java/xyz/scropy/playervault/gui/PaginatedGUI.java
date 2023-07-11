package xyz.scropy.playervault.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public abstract class PaginatedGUI extends GUI {

    protected final List<Integer> paginatedSlots;
    protected int currentPage;

    public PaginatedGUI(Player player, String title, int rows, List<Integer> paginatedSlots) {
        super(player, title, rows);
        this.paginatedSlots = paginatedSlots;
    }

    @Override
    protected void addContent() {
        inventory.clear();
        List<ItemStack> paginatedItems = getPaginatedItems();
        for (int i = 0; i < paginatedSlots.size(); i++) {
            int index = currentPage * paginatedSlots.size() + i;
            ItemStack itemStack;
            if(index >= paginatedItems.size()) itemStack = new ItemStack(Material.AIR);
            else itemStack = paginatedItems.get(index);
            setItem(paginatedSlots.get(i), itemStack);
        }
    }

    protected void goNextPage() {
        ++currentPage;
        inventory.clear();
        addContent();

    }

    protected void goPreviousPage() {
        --currentPage;
        inventory.clear();
        addContent();
    }

    protected boolean isFirstPage() {
        return currentPage == 0;
    }

    protected abstract List<ItemStack> getPaginatedItems();
}
