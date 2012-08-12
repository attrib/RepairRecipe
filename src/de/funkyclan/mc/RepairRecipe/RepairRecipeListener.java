package de.funkyclan.mc.RepairRecipe;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class RepairRecipeListener implements Listener {

    private RepairRecipe plugin;

    public RepairRecipeListener(RepairRecipe repairRecipe) {
        plugin = repairRecipe;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ShapelessRepairRecipe recipe = null;

        ItemStack[] matrix = event.getInventory().getMatrix();
        for (ShapelessRepairRecipe rec : plugin.getRepairRecipes()) {
            if (rec.isMatrixRecipe(matrix)) {
                recipe = rec;
                break;
            }
        }

        if (recipe != null) {
            ItemStack repairedItem = recipe.repairItem(event.getInventory(), true);
            if (repairedItem == null) {
                event.setResult(Event.Result.DENY);
                return;
            }
            event.setCurrentItem(repairedItem);
        }


    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ShapelessRepairRecipe recipe = null;

        ItemStack[] matrix = event.getInventory().getMatrix();
        for (ShapelessRepairRecipe rec : plugin.getRepairRecipes()) {
            if (rec.isMatrixRecipe(matrix)) {
                recipe = rec;
                break;
            }
        }

        if (recipe != null) {
            ItemStack repairedItem = recipe.repairItem(event.getInventory(), false);
            event.getInventory().setResult(repairedItem);
        }
    }

}
