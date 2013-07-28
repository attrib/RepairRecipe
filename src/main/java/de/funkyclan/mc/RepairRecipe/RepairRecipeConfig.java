package de.funkyclan.mc.RepairRecipe;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RepairRecipeConfig {

    private RepairRecipe plugin;

    private Permission permission = null;
    private Economy economy;
    private HashMap<String, Integer> discountGroups;
    private HashMap<String, Integer> enchantMultiplierGroups;
    private HashMap<String, Integer> enchantChanceGroups;
    private HashMap<Enchantment, Integer> specialEnchantMultiplier;

    private FileConfiguration itemConfig = null;
    private File itemConfigurationFile = null;

    public static final String PERM_REPAIR = "RepairRecipe.repair";
    public static final String PERM_REPAIR_ENCHANT = "RepairRecipe.repair.enchant";
    public static final String PERM_REPAIR_OVER = "RepairRecipe.repair.overRepair";
    public static final String PERM_ADMIN = "RepairRecipe.admin";

    public static boolean DEBUG = false;

    public enum Default {
        PERM_REPAIR(true),
        PERM_REPAIR_ENCHANT(true),
        PERM_REPAIR_OVER(false),
        PERM_ADMIN(false),

        CONF_ALLOW_OVER_REPAIR(false),
        CONF_KEEP_ENCHANTS(100),
        CONF_HIGHEST_ENCHANT(true),
        CONF_MAX_ENCHANT_MULTIPLIER(100),
        CONF_DISCOUNT(10);

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
            RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }

            RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
        }

        changeConfig0_2_1();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        reloadConfig();

    }

    private void changeConfig0_2_1() {
        FileConfiguration config = plugin.getConfig();
        Set<String> keys = config.getKeys(false);
        if (keys.contains("keep_enchantments")) {
            boolean keep = config.getBoolean("keep_enchantments");
            if (keep) {
                config.set("keep_enchantments_chance", 100);
            } else {
                config.set("keep_enchantments_chance", 0);
            }
            config.set("keep_enchantments", null);
        }
        if (config.isBoolean("discount")) {
            config.set("discount", Default.CONF_DISCOUNT.getInt());
            config.createSection("discount_groups");
        } else if (config.isConfigurationSection("discount")) {
            ConfigurationSection discountSectionOld = config.getConfigurationSection("discount");
            ConfigurationSection newDiscountSection = config.createSection("discount_groups");
            for (String group : discountSectionOld.getKeys(false)) {
                newDiscountSection.set(group, discountSectionOld.getInt(group, 0));
            }

            config.set("discount", Default.CONF_DISCOUNT.getInt());
        }
        if (config.isBoolean("enchant_multiplier_groups")) {
            config.createSection("enchant_multiplier_groups");
        }
    }

    public boolean hasPermission(Player player, String permissionString) {
        if (permission != null) {
            if (RepairRecipeConfig.DEBUG)
                RepairRecipe.logger.info("Permission: " + player + " " + permissionString + " " + permission.playerHas(player, permissionString));
            return permission.playerHas(player, permissionString);
        } else if (permissionString.equals(PERM_REPAIR)) {
            return Default.PERM_REPAIR.getBoolean();
        } else if (permissionString.equals(PERM_REPAIR_ENCHANT)) {
            return Default.PERM_REPAIR_ENCHANT.getBoolean();
        } else if (permissionString.equals(PERM_REPAIR_OVER)) {
            if (!Default.PERM_REPAIR_OVER.getBoolean() && configAllowOverRepair()) {
                return true;
            }
            return Default.PERM_REPAIR_OVER.getBoolean();
        } else if (permissionString.equals(PERM_ADMIN) && player.isOp()) {
            return true;
        }

        return false;
    }

    public boolean configAllowOverRepair() {
        return plugin.getConfig().getBoolean("allow_over_repair", Default.CONF_ALLOW_OVER_REPAIR.getBoolean());
    }

    public int configKeepEnchantments(Player player) {
        if (permission == null || groupsEnabled() || enchantChanceGroups.size() == 0 || player == null) {
            return plugin.getConfig().getInt("keep_enchantments_chance", Default.CONF_KEEP_ENCHANTS.getInt());
        } else {
            int chance = Integer.MIN_VALUE;
            for (String group : getPlayerGroups(player)) {
                if (enchantChanceGroups.containsKey(group)) {
                    chance = Math.max(chance, enchantChanceGroups.get(group));
                }
            }
            if (chance == Integer.MIN_VALUE) {
                chance = plugin.getConfig().getInt("keep_enchantments_chance", Default.CONF_KEEP_ENCHANTS.getInt());
            }
            return chance;
        }
    }

    public int configKeepEnchantments(List<HumanEntity> players) {
        int chance = Integer.MAX_VALUE;
        for (HumanEntity player : players) {
            if (player instanceof Player) {
                chance = Math.min(chance, configKeepEnchantments((Player) player));
            }
        }
        if (chance == Integer.MAX_VALUE) {
            chance = configKeepEnchantments((Player) null);
        }
        return chance;
    }

    public boolean configUseHighestEnchant() {
        return plugin.getConfig().getBoolean("use_highest_enchant", Default.CONF_HIGHEST_ENCHANT.getBoolean());
    }


    public double configMaxEnchantMultiplier(Player player) {
        int multiplier = Integer.MIN_VALUE;
        if (permission != null && groupsEnabled() && enchantMultiplierGroups.size() > 0 && player != null) {
            //permission.
            for (String group : getPlayerGroups(player)) {
                if (enchantMultiplierGroups.containsKey(group)) {
                    multiplier = Math.max(multiplier, enchantMultiplierGroups.get(group));
                }
            }
        }
        if (multiplier == Integer.MIN_VALUE) {
            multiplier = plugin.getConfig().getInt("enchant_multiplier", Default.CONF_MAX_ENCHANT_MULTIPLIER.getInt());
        }
        if (multiplier <= 0) {
            return 0.0;
        }
        if (multiplier >= 200) {
            return 2.0;
        }
        return (multiplier / 100.0);
    }

    public double configMaxEnchantMultiplier(List<HumanEntity> players) {
        double multiplier = Double.MAX_VALUE;
        for (HumanEntity player : players) {
            if (player instanceof Player) {
                multiplier = Math.min(multiplier, configMaxEnchantMultiplier((Player) player));
            }
        }
        if (multiplier == Double.MAX_VALUE) {
            multiplier = configMaxEnchantMultiplier((Player) null);
        }
        return multiplier;
    }

    public double configDiscount(Player player) {
        if (permission == null || groupsEnabled() || discountGroups.size() == 0) {
            int discount = plugin.getConfig().getInt("discount", Default.CONF_DISCOUNT.getInt());
            if (discount >= 100) {
                return 0.0;
            }
            return 1.0 - (discount / 100.0);
        }
        int discount = Integer.MIN_VALUE;

        for (String group : getPlayerGroups(player)) {
            if (discountGroups.containsKey(group)) {
                discount = Math.max(discount, discountGroups.get(group));
            }
        }
        if (discount == Integer.MIN_VALUE) {
            discount = plugin.getConfig().getInt("discount", Default.CONF_DISCOUNT.getInt());
        }
        if (discount >= 100) {
            return 0.0;
        }
        return 1.0 - (discount / 100.0);
    }

    private String[] getPlayerGroups(Player player) {
        if (!permission.hasGroupSupport())
            return new String[0];
        // TODO: Both functions could retrieve odd values, depending on permission system support, see https://github.com/MilkBowl/Vault/issues/368.
        String[] globalGroups = permission.getPlayerGroups((String) null, player.getName());
        String[] worldGroups = permission.getPlayerGroups(player);
        if (globalGroups == null && worldGroups == null)
            return new String[0];
        if (globalGroups == null && worldGroups != null)
            return worldGroups;
        if (globalGroups != null && worldGroups == null)
            return globalGroups;
        String[] groups = new String[globalGroups.length + worldGroups.length];
        System.arraycopy(globalGroups, 0, groups, 0, globalGroups.length);
        System.arraycopy(worldGroups, 0, groups, globalGroups.length, worldGroups.length);
        return groups;
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

    public double configSpecialEnchantMultiplier(Enchantment enchantment) {
        if (specialEnchantMultiplier.containsKey(enchantment)) {
            int multiplier = specialEnchantMultiplier.get(enchantment);
            if (multiplier < 0) {
                return 0.0;
            }
            if (multiplier > 200) {
                return 2.0;
            }
            return multiplier / 100;
        } else {
            return 1.0;
        }
    }

    private void reloadItemConfig() {
        if (itemConfigurationFile == null) {
            itemConfigurationFile = new File(plugin.getDataFolder(), "items.yml");
            if (!itemConfigurationFile.exists()) {
                itemConfig = YamlConfiguration.loadConfiguration(itemConfigurationFile);
                InputStream defConfigStream = plugin.getResource("items.yml");
                if (defConfigStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    itemConfig.setDefaults(defConfig);
                }
                itemConfig.options().copyDefaults(true);
                saveItemConfig();
            }
        }
        itemConfig = YamlConfiguration.loadConfiguration(itemConfigurationFile);

        InputStream defConfigStream = plugin.getResource("items.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            itemConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getItemConfig() {
        if (itemConfig == null) {
            reloadItemConfig();
        }
        return itemConfig;
    }

    public void saveItemConfig() {
        if (itemConfig == null || itemConfigurationFile == null) {
            return;
        }
        try {
            itemConfig.save(itemConfigurationFile);
        } catch (IOException exception) {
            RepairRecipe.logger.severe("[RepairRecipe] Error on saving item configuration.");
        }
    }

    public void reloadConfig() {
        plugin.reloadConfig();

        ConfigurationSection discountSection = plugin.getConfig().getConfigurationSection("discount_groups");
        discountGroups = new HashMap<String, Integer>();
        int discount = plugin.getConfig().getInt("discount", Default.CONF_DISCOUNT.getInt());
        if (discountSection != null) {
            for (String group : discountSection.getKeys(false)) {
                discountGroups.put(group, discountSection.getInt(group, discount));
            }
        }

        int enchantMultiplier = plugin.getConfig().getInt("enchant_multiplier", Default.CONF_MAX_ENCHANT_MULTIPLIER.getInt());
        ConfigurationSection enchantMultiplierSection = plugin.getConfig().getConfigurationSection("enchant_multiplier_groups");
        enchantMultiplierGroups = new HashMap<String, Integer>();
        if (enchantMultiplierSection != null) {
            for (String group : enchantMultiplierSection.getKeys(false)) {
                enchantMultiplierGroups.put(group, enchantMultiplierSection.getInt(group, enchantMultiplier));
            }
        }

        int enchantChance = plugin.getConfig().getInt("keep_enchantments_chance", Default.CONF_HIGHEST_ENCHANT.getInt());
        ConfigurationSection enchantChanceSection = plugin.getConfig().getConfigurationSection("keep_enchantments_chance_groups");
        enchantChanceGroups = new HashMap<String, Integer>();
        if (enchantChanceSection != null) {
            for (String group : enchantChanceSection.getKeys(false)) {
                enchantChanceGroups.put(group, enchantChanceSection.getInt(group, enchantChance));
            }
        }

        ConfigurationSection specialEnchantMultiplierSection = plugin.getConfig().getConfigurationSection("special_enchant_multiplier");
        specialEnchantMultiplier = new HashMap<Enchantment, Integer>();
        if (specialEnchantMultiplierSection != null) {
            for (String enchantment : specialEnchantMultiplierSection.getKeys(false)) {
                Enchantment ench = Enchantment.getByName(enchantment.toUpperCase());
                if (ench != null) {
                    specialEnchantMultiplier.put(ench, specialEnchantMultiplierSection.getInt(enchantment, 100));
                }
            }
        }

        if (!RepairRecipeConfig.DEBUG) {
            RepairRecipeConfig.DEBUG = plugin.getConfig().getBoolean("debug", false);
        }

        reloadItemConfig();
    }

    private boolean groupsEnabled() {
        return permission.getGroups().length > 0;
    }
}
