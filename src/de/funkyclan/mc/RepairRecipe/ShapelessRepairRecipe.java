package de.funkyclan.mc.RepairRecipe;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;
import java.util.Map;

public class ShapelessRepairRecipe extends ShapelessRecipe {

    private Material item;
    private Material ingot;
    private int ingotCost;

    public ShapelessRepairRecipe(Material item, Material ingot, int ingotCost) {
        super(new ItemStack(item));

        this.item = item;
        addIngredient(1, item, -1);

        this.ingot = ingot;
        addIngredient(1, ingot, -1);

        this.ingotCost = ingotCost;
    }

    public String toString() {
        return "ShapelessRepairRecipe "+this.item.name();
    }

    public boolean isMatrixRecipe(ItemStack[] matrix) {
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

    public ItemStack repairItem(CraftingInventory inventory, boolean setInventory) {
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
            repairedItem = inventory.getResult();
        }
        if (ingot != null) {
            Map<Enchantment, Integer> enchantments = repairedItem.getEnchantments();
            int enchantLevel = 1;
            if (enchantments.size() > 0) {
                for (Enchantment ench : enchantments.keySet()) {
                    enchantLevel += repairedItem.getEnchantmentLevel(ench);
                }
            }
            double costIngotPerDurability = (double) item.getMaxDurability()/ingotCost/enchantLevel;
            int ingotCost = new Double(Math.ceil(repairedItem.getDurability() / costIngotPerDurability)).intValue();
            short durability = 0;
            if (ingot.getAmount() < ingotCost) {
                ingotCost = ingot.getAmount();
                durability = (short)(repairedItem.getDurability() - new Double(Math.ceil(ingotCost * costIngotPerDurability)).shortValue());
            }
            repairedItem.setDurability(durability);
            if (setInventory) {
                if (ingotCost-1 > 0) {
                    ingotCost = ingotCost-1;
                    inventory.getItem(ingotIndex).setAmount(ingot.getAmount()-ingotCost);
                }
                else if (ingotCost == 0) {
                    return null;
                }
            }
        }
        return repairedItem;
    }

}
