package xyz.scropy.playervault.gui;

import com.google.common.cache.Cache;
import lombok.SneakyThrows;
import mc.obliviate.inventory.Icon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import xyz.scropy.playervault.Placeholder;
import xyz.scropy.playervault.PlayerVaultPlugin;
import xyz.scropy.playervault.config.Config;
import xyz.scropy.playervault.playervault.PlayerVault;
import xyz.scropy.playervault.playervault.PlayerVaultItem;
import xyz.scropy.playervault.utils.BukkitSerialization;
import xyz.scropy.playervault.utils.ItemUtils;
import xyz.scropy.playervault.utils.StringUtils;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlayerVaultGUI extends PaginatedGUI {

    private final PlayerVault playerVault;
    private final Map<Integer, Integer> emptySlots = new HashMap<>();

    public PlayerVaultGUI(Player player, PlayerVault playerVault) {
        super(player, StringUtils.format(PlayerVaultPlugin.getInstance().getConfig().getString("gui.title"), Placeholder.builder().apply("%vault_name%", playerVault.getVault().getName()).build()),
                PlayerVaultPlugin.getInstance().getConfig().getInt("gui.rows"), PlayerVaultPlugin.getInstance().getConfig().getIntegerList("gui.paginated-slots"));
        this.playerVault = playerVault;
    }

    @Override
    protected void addContent() {
        super.addContent();
        Config config = PlayerVaultPlugin.getInstance().getConfig();

        ItemStack backgroundItem = ItemUtils.makeItem(config.getConfigurationSection("gui.items.background"),
                Placeholder.builder().build()).build();
        setItems(config.getIntegerList("gui.items.background.slots"), backgroundItem);


        ItemStack next = ItemUtils.makeItem(config.getConfigurationSection("gui.items.next"),
                Placeholder.builder().build()).build();
        setItems(config.getIntegerList("gui.items.next.slots"), next, clickEvent -> {
            if (isFull() || hasNextPage()) {
                storeItems();
                goNextPage();
            }
        });


        ItemStack previous = ItemUtils.makeItem(config.getConfigurationSection("gui.items.previous"),
                Placeholder.builder().build()).build();
        setItems(config.getIntegerList("gui.items.previous.slots"), previous, clickEvent -> {
            if (isFirstPage()) return;
            storeItems();
            goPreviousPage();
        });
    }

    @Override
    protected List<ItemStack> getPaginatedItems() {
        return playerVault.getItems().values().stream().flatMap(List::stream).map(base64 -> {
            try {
                return BukkitSerialization.itemStackFromBase64(base64.getItem());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }


    private void storeItems() {
        List<PlayerVaultItem> playerVaultItems = playerVault.getItems().getOrDefault(currentPage, new ArrayList<>());
        playerVault.getItems().remove(currentPage);
        playerVaultItems.forEach(playerVaultItem -> PlayerVaultPlugin.getInstance().getDatabaseManager().getPlayerVaultItemRepository().delete(playerVaultItem).join());
        playerVaultItems.clear();
        for (int i = 0; i < PlayerVaultPlugin.getInstance().getConfig().getIntegerList("gui.paginated-slots").size(); i++) {
            int slot = PlayerVaultPlugin.getInstance().getConfig().getIntegerList("gui.paginated-slots").get(i);
            ItemStack itemStack = getInventory().getItem(slot);
            if ((itemStack == null || itemStack.getType().equals(Material.AIR)) && hasNextPage() && playerVault.getItems().get(currentPage + 1).size() > 0) {
                playerVault.getItems().keySet().stream()
                        .filter(pageNum -> pageNum > currentPage)
                        .filter(pageNum -> playerVault.getItems().get(pageNum).size() > 0)
                        .forEach(pageNum -> {
                            PlayerVaultItem playerVaultItem = playerVault.getItems().get(pageNum).get(0);
                            playerVaultItem.setPage(pageNum - 1);
                            playerVaultItem.setChanged(true);
                            PlayerVaultPlugin.getInstance().getDatabaseManager().getPlayerVaultItemRepository().save(playerVaultItem).join();
                            playerVault.getItems().getOrDefault(pageNum - 1, playerVaultItems).add(playerVault.getItems().get(pageNum).remove(0));
                        });
            }
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) continue;
            PlayerVaultItem playerVaultItem = new PlayerVaultItem(player.getUniqueId(), playerVault.getVault().getId(), currentPage,
                    BukkitSerialization.itemStackToBase64(itemStack));
            playerVaultItems.add(playerVaultItem);
        }
        if (playerVaultItems.isEmpty()) return;
        playerVault.setPlayerVaultItems(currentPage, playerVaultItems);
        playerVault.save();
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != inventory) {
            event.setCancelled(false);
            return;
        }


        List<Integer> paginatedSlots = PlayerVaultPlugin.getInstance().getConfig().getIntegerList("gui.paginated-slots");
        if (paginatedSlots.contains(event.getSlot())) {
            if (event.getCursor() == null || event.getCursor().getType().equals(Material.AIR)) {
                event.setCancelled(false);
                return;
            }
            if (playerVault.getVault().getCondition().control(event.getCursor())) {
                event.setCancelled(false);
                return;
            }
        }
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        storeItems();
    }

    private void setItems(List<Integer> slots, ItemStack itemStack) {
        slots.forEach(slot -> setItem(slot, itemStack));
    }

    private void setItems(List<Integer> slots, ItemStack itemStack, Consumer<InventoryClickEvent> action) {
        slots.forEach(slot -> setItem(slot, itemStack, action));
    }

    protected boolean hasNextPage() {
        return playerVault.getItems().containsKey(currentPage + 1);
    }

    private boolean isFull() {
        int itemCount = 0;
        for (Integer paginatedSlot : paginatedSlots) {
            if (inventory.getItem(paginatedSlot) != null) itemCount++;
        }
        return itemCount == paginatedSlots.size();
    }
}
