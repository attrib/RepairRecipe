package de.funkyclan.mc.RepairRecipe;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RepairRecipeListener implements Listener {

    private RepairRecipe plugin;

    public RepairRecipeListener(RepairRecipe repairRecipe) {
        plugin = repairRecipe;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        ShapelessRepairRecipe recipe = null;

        ItemStack[] matrix = event.getInventory().getMatrix();
        for (ShapelessRepairRecipe rec : plugin.getRepairRecipes()) {
            if (rec.isMatrixRecipe(matrix)) {
                recipe = rec;
                break;
            }
        }

        if (recipe != null) {
            List<HumanEntity> players = new ArrayList<HumanEntity>();
            players.add(player);
            ItemStack repairedItem = recipe.repairItem(event.getInventory(), true, players);
            if (repairedItem == null) {
                event.setResult(Event.Result.DENY);
                return;
            }

            if (!plugin.getConfigurator().hasPermission(player, RepairRecipeConfig.PERM_REPAIR)) {
                event.setResult(Event.Result.DENY);
                player.sendMessage("Insufficient permissions to repair item.");
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
            ItemStack repairedItem = recipe.repairItem(event.getInventory(), false, event.getViewers());
            event.getInventory().setResult(repairedItem);
        }
    }

}
