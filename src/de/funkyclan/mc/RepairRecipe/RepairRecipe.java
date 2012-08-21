package de.funkyclan.mc.RepairRecipe;

import de.funkyclan.mc.RepairRecipe.Listener.CraftingListener;
import de.funkyclan.mc.RepairRecipe.Recipe.ShapelessRepairRecipe;
import net.minecraft.server.Packet103SetSlot;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class RepairRecipe extends JavaPlugin {

    public static final Logger logger = Logger.getLogger("Minecraft");

    private Set<ShapelessRepairRecipe> repairRecipes;
    private RepairRecipeConfig config;

    public void onEnable() {

        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);

        config = new RepairRecipeConfig(this);

        repairRecipes = new HashSet<ShapelessRepairRecipe>();
        addRecipes();

        if (repairRecipes.size() == 0) {
            logger.info("[RepairRecipe] no recipes found, disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

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
        CraftItemStack craftItemStack;
        if (item instanceof CraftItemStack) {
            craftItemStack = (CraftItemStack) item;
        }
        else {
            craftItemStack = new CraftItemStack(item);
        }
        if (player instanceof CraftPlayer) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            if (craftPlayer.getHandle().activeContainer != null) {
                Packet103SetSlot packet = new Packet103SetSlot();
                packet.a = craftPlayer.getHandle().activeContainer.windowId;
                packet.b = index;
                packet.c = craftItemStack.getHandle();

                craftPlayer.getHandle().netServerHandler.sendPacket(packet);
            }
        }
    }

    private void addRecipes() {
        for(String key : config.getItemConfig().getKeys(false)) {
            Material item = Material.matchMaterial(key);
            if (item == null) {
                logger.info("[RepairRecipe] unknown item "+key);
                continue;
            }
            ConfigurationSection section = config.getItemConfig().getConfigurationSection(key);

            Material baseItem = Material.matchMaterial(section.getString("base_item"));
            if (baseItem == null) {
                logger.info("[RepairRecipe] unknown base item "+section.getString("base_item"));
                continue;
            }

            int baseAmount = section.getInt("base_amount");

            ShapelessRepairRecipe recipe = new ShapelessRepairRecipe(item, baseItem, baseAmount, this);

            recipe.setConfig("keep_enchantments_chance", section.getString("keep_enchantments_chance", null));
            recipe.setConfig("enchant_multiplier", section.getString("enchant_multiplier", null));

            recipe.setConfig("allow_over_repair", section.getString("allow_over_repair", null));
            recipe.setConfig("use_highest_enchant", section.getString("use_highest_enchant", null));

            repairRecipes.add(recipe);

            getServer().addRecipe(recipe);

            if (RepairRecipeConfig.DEBUG) RepairRecipe.logger.info("Added " +item+ " with " +baseAmount+ "x" +baseItem);
        }
    }

}
