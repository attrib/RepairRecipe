package de.funkyclan.mc.RepairRecipe;

import de.funkyclan.mc.RepairRecipe.Listener.CraftingListener;
import de.funkyclan.mc.RepairRecipe.Recipe.ShapelessRepairRecipe;
import net.minecraft.server.Packet103SetSlot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class RepairRecipe extends JavaPlugin {

    public static final Logger logger = Logger.getLogger("Minecraft");

    private Set<ShapelessRepairRecipe> repairRecipes;
    private RepairRecipeConfig config;

    public void onEnable() {

        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);

        repairRecipes = new HashSet<ShapelessRepairRecipe>();

        repairRecipes.add(new ShapelessRepairRecipe(Material.CHAINMAIL_BOOTS, Material.IRON_INGOT, 2, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.CHAINMAIL_CHESTPLATE, Material.IRON_INGOT, 4, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.CHAINMAIL_HELMET, Material.IRON_INGOT, 2, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.CHAINMAIL_LEGGINGS, Material.IRON_INGOT, 3, this));

        repairRecipes.add(new ShapelessRepairRecipe(Material.LEATHER_BOOTS, Material.LEATHER, 4, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.LEATHER_CHESTPLATE, Material.LEATHER, 8, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.LEATHER_HELMET, Material.LEATHER, 5, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.LEATHER_LEGGINGS, Material.LEATHER, 7, this));

        repairRecipes.add(new ShapelessRepairRecipe(Material.STONE_AXE, Material.COBBLESTONE, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.STONE_HOE, Material.COBBLESTONE, 2, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.STONE_PICKAXE, Material.COBBLESTONE, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.STONE_SPADE, Material.COBBLESTONE, 1, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.STONE_SWORD, Material.COBBLESTONE, 2, this));

        repairRecipes.add(new ShapelessRepairRecipe(Material.WOOD_AXE, Material.WOOD, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.WOOD_HOE, Material.WOOD, 2, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.WOOD_PICKAXE, Material.WOOD, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.WOOD_SPADE, Material.WOOD, 1, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.WOOD_SWORD, Material.WOOD, 2, this));

        repairRecipes.add(new ShapelessRepairRecipe(Material.IRON_AXE, Material.IRON_INGOT, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.IRON_BOOTS, Material.IRON_INGOT, 4, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.IRON_CHESTPLATE, Material.IRON_INGOT, 8, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.IRON_HELMET, Material.IRON_INGOT, 5, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.IRON_HOE, Material.IRON_INGOT, 2, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.IRON_LEGGINGS, Material.IRON_INGOT, 7, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.IRON_PICKAXE, Material.IRON_INGOT, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.IRON_SPADE, Material.IRON_INGOT, 1, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.IRON_SWORD, Material.IRON_INGOT, 2, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.SHEARS, Material.IRON_INGOT, 1, this));

        repairRecipes.add(new ShapelessRepairRecipe(Material.GOLD_AXE, Material.GOLD_INGOT, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.GOLD_BOOTS, Material.GOLD_INGOT, 4, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.GOLD_CHESTPLATE, Material.GOLD_INGOT, 8, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.GOLD_HELMET, Material.GOLD_INGOT, 5, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.GOLD_HOE, Material.GOLD_INGOT, 2, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.GOLD_LEGGINGS, Material.GOLD_INGOT, 7, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.GOLD_PICKAXE, Material.GOLD_INGOT, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.GOLD_SPADE, Material.GOLD_INGOT, 1, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.GOLD_SWORD, Material.GOLD_INGOT, 2, this));

        repairRecipes.add(new ShapelessRepairRecipe(Material.DIAMOND_AXE, Material.DIAMOND, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.DIAMOND_BOOTS, Material.DIAMOND, 4, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.DIAMOND_CHESTPLATE, Material.DIAMOND, 8, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.DIAMOND_HELMET, Material.DIAMOND, 5, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.DIAMOND_HOE, Material.DIAMOND, 2, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.DIAMOND_LEGGINGS, Material.DIAMOND, 7, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.DIAMOND_PICKAXE, Material.DIAMOND, 3, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.DIAMOND_SPADE, Material.DIAMOND, 1, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.DIAMOND_SWORD, Material.DIAMOND, 2, this));

        repairRecipes.add(new ShapelessRepairRecipe(Material.BOW, Material.STRING, 2, this));
        repairRecipes.add(new ShapelessRepairRecipe(Material.FISHING_ROD, Material.STRING, 2, this));

        for (ShapelessRepairRecipe recipe : repairRecipes) {
            getServer().addRecipe(recipe);
        }

        config = new RepairRecipeConfig(this);

        logger.info("[RepairRecipe] added "+ repairRecipes.size() +" Recipes for repair.");
        logger.info("[RepairRecipe] successfully loaded.");
    }

    public ShapelessRepairRecipe getRepairRecipe(ItemStack itemStack) {
        for (ShapelessRepairRecipe recipe : repairRecipes) {
            if (recipe.getResult().getType().equals(itemStack.getType())) {
                return recipe;
            }
        }
        return null;
    }

    public RepairRecipeConfig getConfigurator() {
        return config;
    }

    public void updateSlotInventory(HumanEntity player, ItemStack item, int index) {
        if (player instanceof CraftPlayer && item instanceof CraftItemStack) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            CraftItemStack craftItemStack = (CraftItemStack) item;
            if (craftPlayer.getHandle().activeContainer != null) {
                Packet103SetSlot packet = new Packet103SetSlot();
                packet.a = craftPlayer.getHandle().activeContainer.windowId;
                packet.b = index;
                packet.c = craftItemStack.getHandle();

                craftPlayer.getHandle().netServerHandler.sendPacket(packet);
            }
        }
    }

}
