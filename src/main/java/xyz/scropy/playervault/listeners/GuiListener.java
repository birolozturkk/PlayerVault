package xyz.scropy.playervault.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import xyz.scropy.playervault.gui.GUI;

import java.util.function.Consumer;

public class GuiListener implements Listener {


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI gui) {
            if (event.getWhoClicked() instanceof Player) {
                if(event.getClickedInventory() == null) return;

                gui.onClick(event);
                Consumer<InventoryClickEvent> action = gui.getAction(event.getSlot());
                if(action == null) return;
                action.accept(event);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            if (event.getPlayer() instanceof Player) {
                GUI gui = (GUI) event.getInventory().getHolder();
                gui.onClose(event);

            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event){
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            if (event.getPlayer() instanceof Player) {
                GUI gui = (GUI) event.getInventory().getHolder();
                gui.onOpen(event);

            }
        }
    }
    @EventHandler
    public void onDrag(InventoryDragEvent event){
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            if (event.getWhoClicked() instanceof Player) {
                GUI gui = (GUI) event.getInventory().getHolder();
                gui.onDrag(event);

            }
        }
    }
}
