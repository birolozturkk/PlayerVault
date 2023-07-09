package xyz.scropy.playervault.playervault;

import xyz.scropy.playervault.PlayerVaultPlugin;
import xyz.scropy.playervault.database.Repository;
import xyz.scropy.playervault.vault.Vault;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerVaultManager {

    private final PlayerVaultPlugin plugin;
    private final Repository<PlayerVaultItem, Long> repository = PlayerVaultPlugin.getInstance().getDatabaseManager().getPlayerVaultItemRepository();
    private final Map<UUID, Map<Vault, PlayerVault>> playerVaults = new HashMap<>();

    public PlayerVaultManager(PlayerVaultPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        playerVaults.clear();
        List<PlayerVaultItem> playerVaultItemList = repository.getEntries();
        playerVaultItemList.stream()
                .collect(Collectors.groupingBy(PlayerVaultItem::getPlayerId))
                .forEach((key, value) -> {
                    playerVaults.putIfAbsent(key, new HashMap<>());
                    value.stream()
                            .collect(Collectors.groupingBy(PlayerVaultItem::getVaultId))
                            .forEach((key1, playerVaultItems) -> {
                                Optional<Vault> vaultOptional = plugin.getVaultManager().getVault(key1);
                                if (vaultOptional.isEmpty()) return;
                                Map<Integer, List<PlayerVaultItem>> items = playerVaultItems.stream()
                                        .collect(Collectors.groupingBy(PlayerVaultItem::getPage));
                                playerVaults.get(key).put(vaultOptional.get(), new PlayerVault(vaultOptional.get(), new HashMap<>(items)));

                            });
                });
    }

    public PlayerVault getPlayerVault(UUID uniqueId, Vault vault) {
        playerVaults.putIfAbsent(uniqueId, Map.of());
        Optional<PlayerVault> optionalPlayerVault = Optional.ofNullable(playerVaults.get(uniqueId).getOrDefault(vault, null));
        return optionalPlayerVault.orElseGet(() -> new PlayerVault(vault, new HashMap<>()));
    }
}
