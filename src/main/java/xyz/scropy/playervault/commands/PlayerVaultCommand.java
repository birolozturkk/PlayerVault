package xyz.scropy.playervault.commands;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import xyz.scropy.playervault.Placeholder;
import xyz.scropy.playervault.PlayerVaultPlugin;
import xyz.scropy.playervault.gui.PlayerVaultGUI;
import xyz.scropy.playervault.playervault.PlayerVault;
import xyz.scropy.playervault.utils.StringUtils;
import xyz.scropy.playervault.vault.Vault;
import xyz.scropy.playervault.vault.VaultCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Command("kirovault")
public class PlayerVaultCommand extends BaseCommand {

    private final PlayerVaultPlugin plugin;

    public PlayerVaultCommand(PlayerVaultPlugin plugin) {
        this.plugin = plugin;
    }

    @Permission("kirovault.admin")
    @SubCommand("open")
    public void playerVault(CommandSender sender, Player player, String vaultId) {
        Optional<Vault> vault = plugin.getVaultManager().getVault(vaultId);
        if (vault.isEmpty()) {
            sender.sendMessage(StringUtils.format(plugin.getConfig().getString("messages.not-found-vault"), Placeholder.builder().build()));
            return;
        }
        sender.sendMessage(StringUtils.format(plugin.getConfig().getString("messages.opened-vault"), Placeholder.builder().build()));
        PlayerVault playerVaultOptional = plugin.getPlayerVaultManager().getPlayerVault(player.getUniqueId(), vault.get());
        new PlayerVaultGUI(player, playerVaultOptional).open();
    }

    @Permission("kirovault.admin")
    @SubCommand("create")
    public void createVault(CommandSender sender, String vaultId) {
        Optional<Vault> vaultOptional = plugin.getVaultManager().getVault(vaultId);
        if(vaultOptional.isPresent()) {
            sender.sendMessage(StringUtils.format(plugin.getConfig().getString("messages.already-vault"), Placeholder.builder().build()));
            return;
        }
        ConfigurationSection vaultsSection = plugin.getConfig().getConfigurationSection("vaults");
        if (vaultsSection == null) vaultsSection = plugin.getConfig().createSection("vaults");
        ConfigurationSection vaultSection = vaultsSection.createSection(vaultId);
        vaultSection.set("name", "");
        plugin.getConfig().save();

        VaultCondition vaultCondition = new VaultCondition(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Vault vault = new Vault(vaultId, "", vaultCondition);
        plugin.getVaultManager().add(vault);

        sender.sendMessage(StringUtils.format(plugin.getConfig().getString("messages.vault-created"), Placeholder.builder().build()));

    }

    @Permission("kirovault.admin")
    @SubCommand("edit")
    public void edit(Player player, String vaultId, String add, String condition) {
        if (!add.equals("add")) return;
        Optional<Vault> vaultOptional = plugin.getVaultManager().getVault(vaultId);
        if (vaultOptional.isEmpty()) return;
        VaultCondition vaultCondition = vaultOptional.get().getCondition();
        ConfigurationSection vaultSection = plugin.getConfig().getConfigurationSection("vaults." + vaultId);
        if (condition.equals("hand")) {
            if (player.getItemInHand().getItemMeta().hasCustomModelData()) {
                vaultCondition.getModeDataList().add(player.getItemInHand().getItemMeta().getCustomModelData());
                List<Integer> modelDataList = new ArrayList<>(vaultSection.getIntegerList("allowed-items.model-data"));
                modelDataList.add(player.getItemInHand().getItemMeta().getCustomModelData());
                vaultSection.set("allowed-items.model-data", modelDataList);
            }
            NBT.get(player.getItemInHand(), ReadableNBT::getKeys).forEach(key -> {
                List<String> nbtList = new ArrayList<>(vaultSection.getStringList("allowed-items.nbt"));
                nbtList.add(key);
                vaultSection.set("allowed-items.nbt", nbtList);
                vaultCondition.getNbtList().add(key);
            });
        } else if (condition.equals("itemid")) {
            List<String> materials = new ArrayList<>(vaultSection.getStringList("allowed-items.materials"));
            vaultCondition.getMaterials().add(player.getItemInHand().getType());
            materials.add(player.getItemInHand().getType().toString());
            vaultSection.set("allowed-items.materials", materials);
        }

        StringUtils.sendMessage(player, "edited-vault");
        plugin.getConfig().save();
    }


    @Permission("kirovault.admin")
    @SubCommand("reload")
    public void reload(CommandSender sender) {
        plugin.getConfig().reload();
        plugin.getVaultManager().load();
        plugin.getPlayerVaultManager().load();
        sender.sendMessage(StringUtils.format(plugin.getConfig().getString("messages.reloaded-config"), Placeholder.builder().build()));
    }
}
