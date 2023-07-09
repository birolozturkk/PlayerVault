package xyz.scropy.playervault.expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.scropy.playervault.PlayerVaultPlugin;
import xyz.scropy.playervault.playervault.PlayerVault;
import xyz.scropy.playervault.utils.BukkitSerialization;
import xyz.scropy.playervault.vault.Vault;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KiroVaultExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "vault";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Scropy";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] args = params.split("_");
        if(args.length < 2) return "";
        Optional<Vault> optionalVault = PlayerVaultPlugin.getInstance().getVaultManager().getVault(args[0]);
        if (optionalVault.isEmpty()) return "vault not found";
        PlayerVault playerVault = PlayerVaultPlugin.getInstance().getPlayerVaultManager().getPlayerVault(player.getUniqueId(), optionalVault.get());
        if (args[1].equals("name"))
            return optionalVault.get().getName();
        if(args[1].equals("countofitems")) return String.valueOf(playerVault.getItems().values().stream()
                .flatMap(List::stream)
                .map(playerVaultItem -> {
                    try {
                        return BukkitSerialization.itemStackFromBase64(playerVaultItem.getItem());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.summarizingInt(ItemStack::getAmount)).getSum());
        return "";
    }
}
