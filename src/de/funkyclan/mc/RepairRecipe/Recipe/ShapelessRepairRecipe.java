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

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ShapelessRepairRecipe extends ShapelessRecipe {

    private Material item;
    private Material ingot;
    private int ingotCost;
    private RepairRecipe plugin;

    public ShapelessRepairRecipe(Material item, Material ingot, int ingotCost, RepairRecipe plugin) {
        super(new ItemStack(item));

        this.item = item;
        addIngredient(1, item, -1);

        this.ingot = ingot;
        addIngredient(1, ingot, -1);

        this.ingotCost = ingotCost;

        this.plugin = plugin;
    }

    public String toString() {
        return "ShapelessRepairRecipe "+this.item.name();
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
        }
        else {
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
            if (plugin.getConfigurator().configKeepEnchantments(players) < 100 || !enchantPermission) {
                if (RepairRecipeConfig.DEBUG && enchantPermission) RepairRecipe.logger.info("Removing Enchantments of item.");
                if (RepairRecipeConfig.DEBUG && !enchantPermission) RepairRecipe.logger.info("Removing Enchantments with a chance of "+plugin.getConfigurator().configKeepEnchantments(players)+" from  item.");
                int chance = plugin.getConfigurator().configKeepEnchantments(players);
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
                        }
                        else if (dieGod.nextInt(100) > chance) {
                            int level = repairedItem.getEnchantmentLevel(ench);
                            repairedItem.removeEnchantment(ench);
                            repairedItem.addEnchantment(ench, dieGod.nextInt(level-1)+1);
                        }
                    }
                }
            }
            int enchantLevel = 0;
            if (plugin.getConfigurator().configUseHighestEnchant()) {
                enchantLevel = Integer.MIN_VALUE;
            }
            if (enchantments.size() > 0) {
                boolean highestEnchant = plugin.getConfigurator().configUseHighestEnchant();
                for (Enchantment ench : enchantments.keySet()) {
                    if (highestEnchant) {
                        enchantLevel = Math.max(enchantLevel, repairedItem.getEnchantmentLevel(ench));
                    }
                    else {
                        enchantLevel += repairedItem.getEnchantmentLevel(ench);
                    }
                }
            }

            double baseRepairCost = (double) repairedItem.getDurability()/((item.getMaxDurability()/this.ingotCost));
            if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("baseRepairCost: "+baseRepairCost);

            baseRepairCost += baseRepairCost * plugin.getConfigurator().configMaxEnchantMultiplier(players) * enchantLevel;
            if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("baseRepairCost+enchantMulti: "+baseRepairCost + " (EnchantMulti: "+plugin.getConfigurator().configMaxEnchantMultiplier(players)+", EnchantLevel: "+ enchantLevel +")");

            double discount = plugin.getConfigurator().configDiscount(players);
            baseRepairCost = baseRepairCost * discount;
            if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("costIngotPerDurability+enchantMulti+Discount: "+baseRepairCost+" (Discount: "+plugin.getConfigurator().configDiscount(players)+")");

            int ingotCost = new Double(Math.ceil(baseRepairCost)).intValue();
            short durability = 0;

            if (ingot.getAmount() < ingotCost) {
                ingotCost = ingot.getAmount();
                durability = (short)(repairedItem.getDurability() - new Double(Math.ceil(ingotCost * repairedItem.getDurability() / baseRepairCost)).shortValue());
            }
            else if (ingotCost > 0 && plugin.getConfigurator().configAllowOverRepair() && hasPermission(players, RepairRecipeConfig.PERM_REPAIR_OVER)) {
                durability = (short)(repairedItem.getDurability() - new Double(Math.ceil(ingotCost * repairedItem.getDurability() / baseRepairCost)).shortValue());
            }
            if (Math.abs(durability) > item.getMaxDurability()) {
                durability = (short) (-1 * item.getMaxDurability());
            }
            if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("New Durability: "+durability+" for "+ingotCost+" ingots");
            repairedItem.setDurability(durability);

            if (setInventory) {
                ingotCost = ingotCost-1;
                if (ingotCost > 0) {
                    ingot.setAmount(ingot.getAmount() - ingotCost);
                    if (ingot.getAmount() <= 1) {
                        ingot.setAmount(0);
                    }

                    for (HumanEntity entity : players) {
                        plugin.updateSlotInventory(entity, ingot, ingotIndex);
                    }

                }
                else if (ingotCost == -1 && discount == 0.0) {
                    ingot.setAmount(ingot.getAmount()+1);
                    for (HumanEntity entity : players) {
                        plugin.updateSlotInventory(entity, ingot, ingotIndex);
                    }
                }
                else if (ingotCost < 0) {
                    return null;
                }
            }
        }
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
