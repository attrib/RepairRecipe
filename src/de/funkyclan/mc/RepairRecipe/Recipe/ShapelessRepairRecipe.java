package de.funkyclan.mc.RepairRecipe.Recipe;

import de.funkyclan.mc.RepairRecipe.RepairRecipe;
import de.funkyclan.mc.RepairRecipe.RepairRecipeConfig;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ShapelessRepairRecipe extends ShapelessRecipe {

    private Material item;
    private Material ingot;
    private int ingotCost;
    private RepairRecipe plugin;
    private Map<String, String> config;

    public ShapelessRepairRecipe(Material item, Material ingot, int ingotCost, RepairRecipe plugin) {
        super(new ItemStack(item));

        this.item = item;
        addIngredient(1, item, -1);

        this.ingot = ingot;
        addIngredient(1, ingot, -1);

        this.ingotCost = ingotCost;

        this.plugin = plugin;

        this.config = new HashMap<String, String>();
    }

    public void setConfig(String key, String value) {
        if (value != null && !value.toLowerCase().equals("default")) {
            this.config.put(key, value);
        }
    }

    public String getConfig(String key) {
        if (this.config.containsKey(key)) {
            return this.config.get(key);
        } else {
            return null;
        }
    }

    private int getKeepEnchantmentsChance(List<HumanEntity> players) {
        String tmp = getConfig("keep_enchantments_chance");
        if (tmp != null) {
            try {
                return Integer.parseInt(tmp);
            } catch (NumberFormatException exception) {
                RepairRecipe.logger.info("[RepairRecipe] item.yml: not a number keep enchantments chance");
            }
        }
        return plugin.getConfigurator().configKeepEnchantments(players);
    }

    private double getEnchantMultiplier(List<HumanEntity> players) {
        String tmp = getConfig("enchant_multiplier");
        if (tmp != null) {
            try {
                int multiplier = Integer.parseInt(tmp);
                if (multiplier <= 0) {
                    return 0.0;
                }
                if (multiplier >= 200) {
                    return 2.0;
                }
                return (multiplier / 100.0);
            } catch (NumberFormatException exception) {
                RepairRecipe.logger.info("[RepairRecipe] item.yml: not a number keep enchantments chance");
            }
        }
        return plugin.getConfigurator().configMaxEnchantMultiplier(players);
    }

    private boolean getUseHighestEnchant() {
        String tmp = getConfig("use_highest_enchant");
        if (tmp != null) {
            if (tmp.toLowerCase().equals("true")) {
                return true;
            } else if (tmp.toLowerCase().equals("false")) {
                return false;
            }
        }
        return plugin.getConfigurator().configUseHighestEnchant();
    }

    private boolean getAllowOverRepair(List<HumanEntity> players) {
        if (hasPermission(players, RepairRecipeConfig.PERM_REPAIR_OVER)) {
            String tmp = getConfig("allow_over_repair");
            if (tmp != null) {
                if (tmp.toLowerCase().equals("true")) {
                    return true;
                } else if (tmp.toLowerCase().equals("false")) {
                    return false;
                }
            }
            return plugin.getConfigurator().configAllowOverRepair();
        }
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" - (");
        int i = 0;
        for (String key : config.keySet()) {
            if (i > 0) stringBuilder.append(", ");
            stringBuilder.append(key);
            stringBuilder.append(": ");
            stringBuilder.append(config.get(key));
            i++;
        }
        stringBuilder.append(")");
        return "ShapelessRepairRecipe " + this.item.name() + stringBuilder.toString();
    }

    public boolean checkIngredients(ItemStack[] matrix) {
        List<ItemStack> list = getIngredientList();
        int usedItems = 0;
        int matrixItems = 0;
        for (ItemStack item : matrix) {
            if (item != null && item.getTypeId() != Material.AIR.getId()) {
                for (ItemStack recipeItem : list) {
                    if (recipeItem.getTypeId() == item.getTypeId()) {
                        usedItems++;
                        list.remove(recipeItem);
                        break;
                    }
                }
                matrixItems++;
            }
        }

        if (usedItems == matrixItems && 0 == list.size()) {
            return true;
        } else {
            return false;
        }
    }

    public ItemStack repairItem(CraftingInventory inventory, boolean setInventory, List<HumanEntity> players) {
        ItemStack[] matrix = inventory.getMatrix();
        ItemStack repairedItem = null;
        ItemStack ingot = null;
        int ingotIndex = 0;
        int index = 1;
        for (ItemStack matrixItem : matrix) {
            if (matrixItem != null) {
                if (matrixItem.getTypeId() == this.item.getId()) {
                    repairedItem = matrixItem.clone();
                }
                if (matrixItem.getTypeId() == this.ingot.getId()) {
                    ingot = matrixItem;
                    ingotIndex = index;
                }
            }
            index++;
        }

        if (repairedItem == null) {
            repairedItem = inventory.getResult().clone();
        }
        if (repairedItem.getDurability() <= 0) {
            return null;
        }
        if (ingot != null) {
            Map<Enchantment, Integer> enchantments = repairedItem.getEnchantments();
            boolean enchantPermission = hasPermission(players, RepairRecipeConfig.PERM_REPAIR_ENCHANT);
            if (RepairRecipeConfig.DEBUG)
                RepairRecipe.logger.info("keep enchant chance: " + getKeepEnchantmentsChance(players));
            if (getKeepEnchantmentsChance(players) < 100 || !enchantPermission) {
                if (RepairRecipeConfig.DEBUG && enchantPermission)
                    RepairRecipe.logger.info("Removing Enchantments of item.");
                if (RepairRecipeConfig.DEBUG && !enchantPermission)
                    RepairRecipe.logger.info("Removing Enchantments with a chance of " + getKeepEnchantmentsChance(players) + " from  item.");
                int chance = getKeepEnchantmentsChance(players);
                Random dieGod = new Random();
                for (Enchantment ench : enchantments.keySet()) {
                    // player don't has permission to keep enchants, remove all enchantments
                    // the chance is 0, no need to ask the mighty god
                    if (!enchantPermission || chance == 0) {
                        repairedItem.removeEnchantment(ench);
                    }
                    // praise to the die god to keep your enchants
                    else {
                        if (dieGod.nextInt(100) > chance) {
                            repairedItem.removeEnchantment(ench);
                            if (RepairRecipeConfig.DEBUG)
                                RepairRecipe.logger.info("bad luck brain - removing enchant " + ench.getName());
                        } else if (dieGod.nextInt(100) > chance) {
                            int level = repairedItem.getEnchantmentLevel(ench);
                            repairedItem.removeEnchantment(ench);
                            if (level - 1 > 0)
                                repairedItem.addEnchantment(ench, dieGod.nextInt(level - 1) + 1);
                            if (RepairRecipeConfig.DEBUG)
                                RepairRecipe.logger.info("enchant " + ench.getName() + " decreased level from " + level + " to " + repairedItem.getEnchantmentLevel(ench));
                        }
                    }
                }
            }
            double enchantLevel = 0;

            if (enchantments.size() > 0) {
                boolean highestEnchant = getUseHighestEnchant();
                if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("use highest enchant: " + highestEnchant);
                if (highestEnchant) {
                    enchantLevel = Integer.MIN_VALUE;
                }
                for (Enchantment ench : enchantments.keySet()) {
                    if (highestEnchant) {
                        enchantLevel = Math.max(enchantLevel, repairedItem.getEnchantmentLevel(ench) * plugin.getConfigurator().configSpecialEnchantMultiplier(ench));
                    } else {
                        enchantLevel += repairedItem.getEnchantmentLevel(ench) * plugin.getConfigurator().configSpecialEnchantMultiplier(ench);
                    }
                    if (RepairRecipeConfig.DEBUG && plugin.getConfigurator().configSpecialEnchantMultiplier(ench) != 1.0)
                        RepairRecipe.logger.info("special enchant multiplier of " + plugin.getConfigurator().configSpecialEnchantMultiplier(ench) + " for " + ench.getName());
                }
            }

            double baseRepairCost = (double) repairedItem.getDurability() / ((item.getMaxDurability() / this.ingotCost));
            if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("baseRepairCost: " + baseRepairCost);

            baseRepairCost += baseRepairCost * getEnchantMultiplier(players) * enchantLevel;
            if (RepairRecipeConfig.DEBUG)
                RepairRecipe.logger.info("baseRepairCost+enchantMulti: " + baseRepairCost + " (EnchantMulti: " + getEnchantMultiplier(players) + ", EnchantLevel: " + enchantLevel + ")");

            double discount = plugin.getConfigurator().configDiscount(players);
            baseRepairCost = baseRepairCost * discount;
            if (RepairRecipeConfig.DEBUG)
                RepairRecipe.logger.info("costIngotPerDurability+enchantMulti+Discount: " + baseRepairCost + " (Discount: " + discount + ")");

            int ingotCost = new Double(Math.ceil(baseRepairCost)).intValue();
            if (RepairRecipeConfig.DEBUG) {
                RepairRecipe.logger.info("Ingots cost to repair fully: " + ingotCost + "x" + ingot.getType().name());
                for (HumanEntity he : players) {
                    if (he instanceof Player) {
                        ((Player) he).sendMessage("Ingots cost to repair fully: " + ingotCost + "x" + ingot.getType().name());
                    }
                }
            }
            short durability = 0;

            if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("allow over repair: " + getAllowOverRepair(players));

            if (ingot.getAmount() < ingotCost) {
                ingotCost = ingot.getAmount();
                durability = (short) (repairedItem.getDurability() - new Double(Math.ceil(ingotCost * repairedItem.getDurability() / baseRepairCost)).shortValue());
            } else if (ingotCost > 0 && getAllowOverRepair(players)) {
                durability = (short) (repairedItem.getDurability() - new Double(Math.ceil(ingotCost * repairedItem.getDurability() / baseRepairCost)).shortValue());
            }
            if (durability < -item.getMaxDurability()) {
                durability = (short) (-1 * item.getMaxDurability());
            }
            if (RepairRecipeConfig.DEBUG)
                RepairRecipe.logger.info("New Durability: " + durability + " for " + ingotCost + " ingots");
            repairedItem.setDurability(durability);

            if (setInventory) {
                // one ingot is removed automatically by recipe cost
                ingotCost = ingotCost - 1;
                if (ingotCost > 0) {
                    ingot.setAmount(ingot.getAmount() - ingotCost);
                    if (ingot.getAmount() <= 1) {
                        ingot.setAmount(0);
                    }

                    for (HumanEntity entity : players) {
                        // some strange behavior here, we have also to subtract the recipe costs here...
                        ItemStack sendIngot = ingot.clone();
                        sendIngot.setAmount(ingot.getAmount() - 1);
                        plugin.updateSlotInventory(entity, sendIngot, ingotIndex);
                    }

                } else if (ingotCost == -1 && discount == 0.0) {
                    ingot.setAmount(ingot.getAmount() + 1);
                    for (HumanEntity entity : players) {
                        // some strange behavior here, we have also to subtract the recipe costs here...
                        ItemStack sendIngot = ingot.clone();
                        sendIngot.setAmount(ingot.getAmount() - 1);
                        plugin.updateSlotInventory(entity, sendIngot, ingotIndex);
                    }
                } else if (ingotCost < 0) {
                    return null;
                }
            }
        }
        // dupe fix
        repairedItem.setAmount(1);
        return repairedItem;
    }

    private boolean hasPermission(List<HumanEntity> players, String permission) {
        for (HumanEntity humanEntity : players) {
            if (humanEntity instanceof Player) {
                if (!plugin.getConfigurator().hasPermission((Player) humanEntity, permission)) {
                    return false;
                }
            }
        }
        return true;
    }

}
