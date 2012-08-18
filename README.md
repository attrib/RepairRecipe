RepairRecipe
============

See also Bukkit plugin page: http://dev.bukkit.org/server-mods/repairrecipe/

Version 0.2-alpha1

Compatible Bukkit Version: 1.2.5-R5.0, 1.3.1-R1.0

This Plugin allows you to repair armour/weapons/tools with the base item needed to craft the item and keep the enchantments.
Costs are depending on the damage of the item, amout of base items needed to craft the item and the level of the enchantments.
To repair an item drop the item and the base item into the workbench.
The result is the repaired item with enchantments.

__No special block or construction needed. Just use the workbench__

Example:
* One iron ingot repairs 1/3 of an iron pickaxe without enchantments.
* One iron ingot repairs 1/9 of an iron pickaxe with a level 3 enchantment.

Base items:
* iron ingot for all iron tools, weapons, armour and shears
* iron ingot for chainmail armour (but cheaper than iron armour)
* leather for leather armour
* cobblestone for stone tools
* wooden planks for wood tools
* gold ingots for gold tools, weapons and armour
* diamond for diamond tools, weapons and armour
* string for bow and fishing rod

Features:
* Repair items with base item and keep enchantments
* Over-Repair - waste no base item and allow to get items repaired over 100% durability
* Discounts for Groups - set a discount for your sponsors (Vault needed)

Installation
------------

Put the RepairRecipe.jar into bukkits plugins folder.

Configuration
-------------

To allow Over-Repair set this to true (Default false)
```
allow_over_repair: false #or true
```

If you do not want to keep enchantments, set this to false (Default true)
```
keep_enchantments: true #or false
```

Repairing items with enchantments is expansive. With this multiplier you can make it cheaper or make it even more expansive.
If you set this to 0 there will be no extra costs to repair enchanted items.
```
enchant_multiplier: 100 #value between 0 (no extra costs for enchantments) and 200 (around 30 diamonds for a pick axe with a level 4 enchantment)
```

With this option you can set discounts for your Sponsors and VIPs. A discount of 100 means it only costs one base item whatever the durability of the item is.
```
discount:
  Member: 10
  Sponsor: 20
  Admin: 100
```

Default - no discount for anyone
```
discount: false
```


Permissions
-----------

`RepairRecipe.repair` - Player is allowed to repair items. Default: true

`RepairRecipe.repair.enchant` - Player is allowed to keep enchantments. Player without this permission, can repair their items, but will lose all enchantments. Default: true