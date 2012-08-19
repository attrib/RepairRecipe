package de.funkyclan.mc.RepairRecipe;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.List;

public class RepairRecipeConfig {

    private RepairRecipe plugin;

    private Permission permission = null;
    private int groups = 0;
    private Economy economy;
    private HashMap<String, Integer> discountGroups;
    private HashMap<String, Integer> enchantMultiplierGroups;

    public static final String PERM_REPAIR         = "RepairRecipe.repair";
    public static final String PERM_REPAIR_ENCHANT = "RepairRecipe.repair.enchant";
    public static final String PERM_REPAIR_OVER    = "RepairRecipe.repair.overRepair";

    public static final boolean DEBUG = false;

    public enum Default {
        PERM_REPAIR (true),
        PERM_REPAIR_ENCHANT (true),
        PERM_REPAIR_OVER (false),

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

        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp != null) {
                groups = rsp.getProvider().getGroups().length;
                permission = rsp.getProvider();
            }

            RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
        }

        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        ConfigurationSection discountSection = plugin.getConfig().getConfigurationSection("discount");
        discountGroups = new HashMap<String, Integer>();
        if (discountSection != null) {
            for (String group : discountSection.getKeys(false)) {
                discountGroups.put(group, discountSection.getInt(group, 0));
            }
        }
        ConfigurationSection enchantMultiplierSection = plugin.getConfig().getConfigurationSection("enchant_multiplier_groups");
        enchantMultiplierGroups = new HashMap<String, Integer>();
        if (enchantMultiplierSection != null) {
            for (String group : enchantMultiplierSection.getKeys(false)) {
                enchantMultiplierGroups.put(group, enchantMultiplierSection.getInt(group, 0));
            }
        }
    }

    public boolean hasPermission(Player player, String permissionString) {
        if (permission != null) {
            if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("Permission: "+player+" "+permissionString+" "+permission.has(player, permissionString));
            return permission.has(player, permissionString);
        }
        else if (permissionString.equals(PERM_REPAIR))  {
            return Default.PERM_REPAIR.getBoolean();
        }
        else if (permissionString.equals(PERM_REPAIR_ENCHANT))  {
            return Default.PERM_REPAIR_ENCHANT.getBoolean();
        }
        else if (permissionString.equals(PERM_REPAIR_OVER)) {
            return Default.PERM_REPAIR_OVER.getBoolean();
        }
        else {
            return false;
        }
    }

    public boolean configAllowOverRepair() {
        if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("allow_over_repair: "+plugin.getConfig().getBoolean("allow_over_repair", Default.CONF_ALLOW_OVER_REPAIR.getBoolean()));
        return plugin.getConfig().getBoolean("allow_over_repair", Default.CONF_ALLOW_OVER_REPAIR.getBoolean());
    }

    public boolean configKeepEnchantments() {
        if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("keep_enchantments: " + plugin.getConfig().getBoolean("keep_enchantments", Default.CONF_KEEP_ENCHANTS.getBoolean()));
        return plugin.getConfig().getBoolean("keep_enchantments", Default.CONF_KEEP_ENCHANTS.getBoolean());
    }

    public double configMaxEnchantMultiplier(Player player) {
        int multiplier = Integer.MIN_VALUE;
        if (permission != null && groups > 0 && enchantMultiplierGroups.size() >  0) {
            //permission.
            for (String group : permission.getPlayerGroups(player)) {
                if (enchantMultiplierGroups.containsKey(group)) {
                    if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("Player Groups "+group+ " enchantment multiplier "+enchantMultiplierGroups.get(group));
                    multiplier = Math.max(multiplier, enchantMultiplierGroups.get(group));
                }
            }
        }
        if (multiplier == Integer.MIN_VALUE) {
            multiplier = plugin.getConfig().getInt("enchant_multiplier", Default.CONF_MAX_ENCHANT_MULTIPLER.getInt());
            if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("enchant_multiplier: "+multiplier);
        }
        if (multiplier <= 0) {
            return 0.0;
        }
        if (multiplier >= 200) {
            return 2.0;  //This was set to 3.0...should be 2.0, no?
        }
        return 1.0+(multiplier/100.0);
    }

    public double configMaxEnchantMultiplier(List<HumanEntity> players) {
        double multiplier = Double.MAX_VALUE;
        for (HumanEntity player : players) {
            if (player instanceof Player) {
                multiplier = Math.min(multiplier, configMaxEnchantMultiplier((Player) player));
            }
        }
        if (multiplier == Double.MAX_VALUE) {
            multiplier = plugin.getConfig().getInt("enchant_multiplier", Default.CONF_MAX_ENCHANT_MULTIPLER.getInt());
            if (multiplier <= 0) {
                return 0.0;
            }
            if (multiplier >= 200) {
                return 2.0;  //This was set to 3.0...should be 2.0, no?
            }
            return 1.0+(multiplier/100.0);
        }
        return multiplier;
    }

    public double configDiscount(Player player) {
        if (permission == null || groups == 0 || enchantMultiplierGroups.size() == 0) {
            return 1.0;
        }
        int discount = 0;

        for (String group : permission.getPlayerGroups(player)) {
            if (discountGroups.containsKey(group)) {
                if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("Player Groups "+group+ " discount "+discountGroups.get(group));
                discount = Math.max(discount, discountGroups.get(group));
            }
        }
        if (discount >= 100) {
            return 0.001;
        }
        return 1.0+(discount/100.0);
    }

    public double configDiscount(List<HumanEntity> players) {
        double discount = Double.MAX_VALUE;
        for (HumanEntity player : players) {
            if (player instanceof Player) {
                discount = Math.min(discount, configDiscount((Player) player));
            }
        }
        if (discount == Double.MAX_VALUE) {
            discount = 1.0;
        }
        return discount;
    }

}
