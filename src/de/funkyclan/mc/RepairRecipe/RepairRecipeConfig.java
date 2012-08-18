package de.funkyclan.mc.RepairRecipe;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;

public class RepairRecipeConfig {

    private RepairRecipe plugin;

    private Permission permission = null;
    private Economy economy;

    public static final String PERM_REPAIR         = "RepairRecipe.repair";
    public static final String PERM_REPAIR_ENCHANT = "RepairRecipe.repair.enchant";

    public enum Default {
        PERM_REPAIR (true),
        PERM_REPAIR_ENCHANT (true),

        CONF_ALLOW_OVER_REPAIR (false),
        CONF_KEEP_ENCHANTS (true),
        CONF_MAX_ENCHANT_MULTIPLER (100);

        private final boolean bdef;
        private final int idef;

        Default(boolean bdefault) {
            this.bdef = bdefault;
            this.idef = -1;
        }

        Default(int idefault) {
            this.bdef = false;
            this.idef = idefault;
        }

        boolean getBoolean() {
            return this.bdef;
        }

        int getInt() {
            return this.idef;
        }
    }

    public RepairRecipeConfig(RepairRecipe plugin) {
        this.plugin = plugin;

        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
            permission = rsp.getProvider();
        }

        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    public boolean hasPermission(Player player, String permissionString) {
        if (permission != null) {
            return permission.has(player, permissionString);
        }
        else if (permissionString.equals(PERM_REPAIR))  {
            return Default.PERM_REPAIR.getBoolean();
        }
        else if (permissionString.equals(PERM_REPAIR_ENCHANT))  {
            return Default.PERM_REPAIR_ENCHANT.getBoolean();
        }
        else {
            return false;
        }
    }

    public boolean configAllowOverRepair() {
        return plugin.getConfig().getBoolean("allow_over_repair", Default.CONF_ALLOW_OVER_REPAIR.getBoolean());
    }

    public boolean configKeepEnchantments() {
        return plugin.getConfig().getBoolean("keep_enchantments", Default.CONF_KEEP_ENCHANTS.getBoolean());
    }

    public double configMaxEnchantMultiplier() {
        int multiplier = plugin.getConfig().getInt("enchant_multiplier", Default.CONF_MAX_ENCHANT_MULTIPLER.getInt());
        if (multiplier <= 0) {
            return 0.0;
        }
        if (multiplier >= 200) {
            return 2.0;
        }
        return multiplier/100.0;
    }

    public double configDiscount(Player player) {
        if (permission == null) {
            return 1.0;
        }
        int discount = 0;
        List<String> discountGroups = plugin.getConfig().getStringList("discount");
        for (String group : permission.getPlayerGroups(player)) {
            if (discountGroups.contains(group)) {
                discount = Math.max(discount, plugin.getConfig().getInt("discount."+group, 0));
            }
        }
        if (discount >= 100) {
            return 0.001;
        }
        return 1.0-(discount/100.0);
    }

    public double configDiscount(List<HumanEntity> players) {
        double discount = 1.0;
        for (HumanEntity player : players) {
            if (player instanceof Player) {
                discount = Math.max(discount, configDiscount((Player) player));
            }
        }
        return discount;
    }

}
