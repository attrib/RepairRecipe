package de.funkyclan.mc.RepairRecipe;

import net.minecraft.server.Packet103SetSlot;
import net.minecraft.server.Packet53BlockChange;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.CraftingInventory;
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

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onInventoryEvent(InventoryClickEvent event) {
//        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
//            ShapelessRepairRecipe recipe = null;
//
//            if (event.getInventory().getType() == InventoryType.WORKBENCH) {
//                CraftingInventory inventory = (CraftingInventory) event.getInventory();
//                ItemStack[] matrix = inventory.getMatrix().clone();
//
//                ItemStack item = matrix[event.getRawSlot()-1];
//RepairRecipe.logger.info("IngotCount "+item.getAmount());
////                if (event.isRightClick()) {
////                    item.setAmount(item.getAmount()+1);
////                }
////                else if (event.isLeftClick()) {
////                    item.setAmount(item.getAmount()+event.getCursor().getAmount());
////                }
////                else if (event.isShiftClick()) {
////                    item.setAmount(0);
////                }
//RepairRecipe.logger.info("IngotCount "+item.getAmount());
//                for (ShapelessRepairRecipe rec : plugin.getRepairRecipes()) {
//                    if (rec.isMatrixRecipe(matrix)) {
//                        recipe = rec;
//                        break;
//                    }
//                }
//
//                if (recipe != null) {
//                    ItemStack repairedItem = recipe.repairItem(inventory, false, event.getViewers());
//                    inventory.setResult(repairedItem);
//RepairRecipe.logger.info("Set Result "+repairedItem.getDurability());
//
//                    Packet103SetSlot packet = new Packet103SetSlot();
//                    packet.a = 1;
//                    packet.b = 0;
//                    packet.c = CraftItemStack.createNMSItemStack(repairedItem);
//                    for (HumanEntity entity : event.getViewers()) {
//                        if (entity instanceof CraftPlayer) {
//                            CraftPlayer player = (CraftPlayer) entity;
//                            RepairRecipe.logger.info("send packet ");
//                            //packet.handle(player.getHandle().netServerHandler);
//                            //player.getHandle().netServerHandler.sendPacket(packet);
//                            player.getHandle().netServerHandler.sendPacket(packet);
//                        }
//                    }
//                }
//            }
//        }
//
//    }


}
