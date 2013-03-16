package de.funkyclan.mc.RepairRecipe;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RepairRecipeCommand implements CommandExecutor {

    private RepairRecipe plugin;

    public RepairRecipeCommand(RepairRecipe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (command.getName().equalsIgnoreCase("repairrecipe")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload") && hasAdminPermission(sender)) {
                plugin.getConfigurator().reloadConfig();
                sender.sendMessage("[RepairRecipe] Config reloaded");
                sender.sendMessage("[RepairRecipe] If you changed items.yml, please restart server.");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("debug") && hasAdminPermission(sender)) {
                RepairRecipeConfig.DEBUG = !RepairRecipeConfig.DEBUG;
                sender.sendMessage("[RepairRecipe] Debug set to " + RepairRecipeConfig.DEBUG);
                return true;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("debug") && hasAdminPermission(sender)) {
                boolean debug = !RepairRecipeConfig.DEBUG;
                if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("1")) {
                    debug = true;
                } else if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("0")) {
                    debug = false;
                }
                RepairRecipeConfig.DEBUG = debug;
                sender.sendMessage("[RepairRecipe] Debug set to " + RepairRecipeConfig.DEBUG);
                return true;
            }
        }
        return false;
    }

    private boolean hasAdminPermission(CommandSender sender) {
        boolean permission = true;
        if (sender instanceof Player) {
            permission = plugin.getConfigurator().hasPermission((Player) sender, RepairRecipeConfig.PERM_ADMIN);
        }
        if (!permission) {
            sender.sendMessage("You are not allowed to use this command.");
        }
        return permission;
    }

}
