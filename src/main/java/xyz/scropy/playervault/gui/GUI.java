package xyz.scropy.playervault.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.scropy.playervault.utils.BukkitSerialization;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class GUI implements InventoryHolder {

    protected final Player player;

    private final String title;
    private final int rows;

    protected Inventory inventory;

    private final Map<Integer, Consumer<InventoryClickEvent>> actions = new HashMap<>();

    public GUI(Player player, String title, int rows) {
        this.player = player;
        this.title = title;
        this.rows = rows;
    }

    public void open() {
        inventory = Bukkit.createInventory(this, rows * 9, title);
        addContent();
        player.openInventory(inventory);
    }

    protected abstract void addContent();

    public void setItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> clickAction) {
        actions.put(slot, clickAction);
        inventory.setItem(slot, itemStack);
    }

    public void setItem(int slot, ItemStack itemStack) {
        actions.put(slot, inventoryClickEvent -> {});
        inventory.setItem(slot, itemStack);
    }

    public void onClick(InventoryClickEvent event){}
    public void onClose(InventoryCloseEvent event){}
    public void onOpen(InventoryOpenEvent event) {}
    public void onDrag(InventoryDragEvent event) {}

    public Consumer<InventoryClickEvent> getAction(int slot) {
        return actions.get(slot);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
