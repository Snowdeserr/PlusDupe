package me.skythebuilder.plusdupe;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class PlusDupe extends JavaPlugin implements CommandExecutor {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        getCommand("dupe").setExecutor(this);
        getCommand("dupereload").setExecutor(this);
        loadConfig();
    }

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("dupe")) {
            return dupeCommand(sender);
        } else if (cmd.getName().equalsIgnoreCase("dupereload")) {
            return reloadConfigCommand(sender);
        }
        return false;
    }

    private boolean dupeCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("consoleError"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (handItem == null) {
            player.sendMessage(getMessage("noItemInHand"));
            return true;
        }

        List<String> blacklist = config.getStringList("blacklist");
        if (blacklist.contains(handItem.getType().toString())) {
            player.sendMessage(getMessage("blacklistedItem"));
            return true;
        }
        ItemStack duplicatedItem = handItem.clone();
        player.getInventory().addItem(duplicatedItem);

        player.sendMessage(getMessage("successMessage"));
        return true;
    }

    private boolean reloadConfigCommand(CommandSender sender) {
        if (!sender.hasPermission("itemduplication.reloadconfig")) {
            sender.sendMessage("Du hast keine Berechtigung, die Konfiguration neu zu laden.");
            return true;
        }

        reloadConfig();
        loadConfig();
        sender.sendMessage("Konfiguration erfolgreich neu geladen.");

        return true;
    }

    private String getMessage(String key) {
        return config.getString("messages." + key, getDefaultMessage(key));
    }

    private String getDefaultMessage(String key) {
        switch (key) {
            case "consoleError":
                return "Dieser Befehl kann nur von einem Spieler ausgeführt werden.";
            case "noItemInHand":
                return "Du hältst kein Item in der Hand.";
            case "successMessage":
                return "Item erfolgreich dupliziert!";
            case "blacklistedItem":
                return "Dieses Item kann nicht dupliziert werden.";
            default:
                return "";
        }
    }
}
