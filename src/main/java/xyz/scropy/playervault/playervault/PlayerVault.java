package xyz.scropy.playervault.playervault;

import lombok.Getter;
import xyz.scropy.playervault.PlayerVaultPlugin;
import xyz.scropy.playervault.vault.Vault;

import java.util.List;
import java.util.Map;

@Getter
public class PlayerVault {

    private final Vault vault;
    private final Map<Integer, List<PlayerVaultItem>> items;
//1 List
    public PlayerVault(Vault vault, Map<Integer, List<PlayerVaultItem>> items) {
        this.vault = vault;
        this.items = items;
    }

    public void save() {
        items.values().forEach(entry -> entry.forEach(playerVaultItem -> PlayerVaultPlugin.getInstance().getDatabaseManager().getPlayerVaultItemRepository().save(playerVaultItem)));
    }

    public void setPlayerVaultItems(int page, List<PlayerVaultItem> playerVaultItems) {
        items.put(page, playerVaultItems);
    }
}
