package xyz.scropy.playervault;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import lombok.Getter;
import lombok.SneakyThrows;
import mc.obliviate.inventory.InventoryAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.scropy.playervault.commands.PlayerVaultCommand;
import xyz.scropy.playervault.config.Config;
import xyz.scropy.playervault.database.DatabaseManager;
import xyz.scropy.playervault.expansions.KiroVaultExpansion;
import xyz.scropy.playervault.listeners.GuiListener;
import xyz.scropy.playervault.playervault.PlayerVaultManager;
import xyz.scropy.playervault.utils.StringUtils;
import xyz.scropy.playervault.vault.VaultManager;

import java.sql.SQLException;

@Getter
public final class PlayerVaultPlugin extends JavaPlugin {

    private final Config config = new Config(this, "config");

    private final DatabaseManager databaseManager = new DatabaseManager(this);
    private final VaultManager vaultManager = new VaultManager(this);
    private PlayerVaultManager playerVaultManager;
    private BukkitCommandManager<CommandSender> commandManager;
    private BukkitAudiences adventure;

    @Getter
    private static PlayerVaultPlugin instance;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        this.adventure = BukkitAudiences.create(this);
        new InventoryAPI(this).init();

        getServer().getPluginManager().registerEvents(new GuiListener(), this);

        config.create();

        setupManagers();
        setupCommands();

        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            new KiroVaultExpansion().register();
    }

    @Override
    public void onDisable() {
        if(adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    private void setupManagers() throws SQLException {
        databaseManager.init();
        this.playerVaultManager = new PlayerVaultManager(this);
        vaultManager.load();
        playerVaultManager.load();
    }

    private void setupCommands() {
        this.commandManager = BukkitCommandManager.create(this);
        commandManager.registerCommand(new PlayerVaultCommand(this));
        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.formatComponent(config.getString("messages.invalid-argument"), Placeholder.builder().build())));

        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.formatComponent(config.getString("messages.unknown-command"), Placeholder.builder().build())));

        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.formatComponent(config.getString("messages.not-enough-arguments"), Placeholder.builder().build())));

        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.formatComponent(config.getString("messages.too-many-arguments"), Placeholder.builder().build())));

        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> adventure.sender(sender)
                .sendMessage(StringUtils.formatComponent(config.getString("messages.no-permission"), Placeholder.builder().build())));
    }

}
