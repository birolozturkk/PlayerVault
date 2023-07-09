package xyz.scropy.playervault.vault;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.units.qual.A;
import xyz.scropy.playervault.PlayerVaultPlugin;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;
import java.util.stream.Collectors;

public class VaultManager {

    private final PlayerVaultPlugin plugin;
    private Map<String, Vault> vaults = new HashMap<>();

    public VaultManager(PlayerVaultPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        ConfigurationSection vaultsSection = plugin.getConfig().getConfigurationSection("vaults");
        if (vaultsSection == null) return;
        for (String key : vaultsSection.getKeys(false)) {
            List<Material> materials = vaultsSection.getStringList(key + ".allowed-items.materials").stream()
                    .map(Material::valueOf).collect(Collectors.toList());
            VaultCondition vaultCondition = new VaultCondition(new ArrayList<>(materials), vaultsSection.getIntegerList(key + ".allowed-items.model-data"),
                    vaultsSection.getStringList(key + ".allowed-items.nbt"));
            Vault vault = new Vault(key, vaultsSection.getString(key + ".name"), vaultCondition);
            vaults.put(key, vault);
        }
    }

    public Optional<Vault> getVault(String id) {
        return Optional.ofNullable(vaults.getOrDefault(id, null));
    }

    public void add(Vault vault) {
        vaults.put(vault.getId(), vault);
    }
}
