# RepairRecipe #

[![Build Status](https://travis-ci.org/attrib/RepairRecipe.png)](https://travis-ci.org/attrib/RepairRecipe)

See also Bukkit plugin page: http://dev.bukkit.org/server-mods/repairrecipe/

Version 0.4.7

Compatible Bukkit Version: 1.5.2-R1.0

For older Version see Bukkit Page.

This Plugin allows you to repair armour/weapons/tools with the base item needed to craft the item and keep the enchantments.
Costs depend on damage of the item, amount of base items needed to craft the item and the level of the enchantments.
To repair an item, drop the item and the base item into the workbench.
The result is the repaired item with enchantments. __And almost everything is configurable!__

__No special block or construction needed. Just use the workbench__

Example:
* [Video by spitfire25565](http://www.youtube.com/watch?v=cX8JU1xc8eQ&feature=youtu.be)
* One iron ingot repairs 1/3 of an iron pickaxe without enchantments.
* One iron ingot repairs 1/9 of an iron pickaxe with a level 3 enchantment.

## Base items ##
* iron ingot for all iron tools, weapons, armour and shears
* iron ingot for chainmail armour (but cheaper than iron armour)
* leather for leather armour
* cobblestone for stone tools
* wooden planks for wood tools
* gold ingots for gold tools, weapons and armour
* diamond for diamond tools, weapons and armour
* string for bow and fishing rod

## Features ##
* Repair items with base item and keep enchantments
* Configure base item and amount needed for a item to repair
* Set a chance to randomly remove enchantments on repair (configurable globally and per group - Vault needed)
* Configure the costs for enchantment repair (configurable global and per group - Vault needed)
* Configure extra costs for special enchantments (configurable per enchant)
* Over-Repair - waste no base item and allow to get items repaired over 100% durability
* Discounts for Groups - set a discount for your sponsors (configurable global and per group - Vault needed)

## Installation ##

Put the RepairRecipe.jar into bukkits plugins folder.

Standard Config is created automatically.

## Configuration ##

### `allow_over_repair` ###
To allow Over-Repair set this to true (Default false)
```
allow_over_repair: false # or true
```

### `keep_enchantments_chance` ###
If you do not want to keep enchantments, set this to 0, if you want to keep them set to 100.
Any value between is the chance you will keep the enchantments. But keep in mind, the dice can roll up to three times for each enchant.
If you are lucky enough to keep the enchant, there will be another roll with the dice. If it hits you there, the enchant gets an random lower level.
And don't trust the result view of the workbench. You can't forecast the roll!
```
keep_enchantments_chance: 100 #value between 0 (remove enchants) to 100 (keep enchants)
keep_enchantments_chance_groups: {} #set it to different values foreach group
```

You can set this chance for each permission group, if you are using Vault. For non defined groups the global default is used.
```
keep_enchantments_chance: 10 # be a lucky bastard to keep any enchantment
keep_enchantments_chance_groups:
  Sponsor: 100 # but don't mess with sponsors!
```

### `enchant_multiplier` ###
Repairing items with enchantments is expensive. With this multiplier you can make it cheaper or make it even more expensive.
If you set this to 0 there will be no extra costs to repair enchanted items.
```
enchant_multiplier: 100 #value between 0 (no extra costs for enchantments) and 200 (around 30 diamonds for a pick axe with a level 4 enchantment)
enchant_multiplier_groups: {}
```

Its now possible to set this enchantment multiplier per group if Vault is enabled.
Is this option is used, enchant_multiplier is the fallback if the user has no group or if the group is not listed.
```
enchant_multiplier: 100 # around 15 diamonds for a pick axe with a level 4 enchantment
enchant_multiplier_groups:
  Sponsor: 20 # around 5 diamonds for a pick axe with a level 4 enchantment
```

### `special_enchant_multiplier` ###
You think some enchantments should cost more than other? Or some enchants should be free? Then this option is for you!
```
special_enchant_multiplier: {} # default - all enchants have the same multiplier (100)
```
This multiplier is multiplied by `enchant_multiplier`. If you set `enchant_multiplier` to 0, this setting has no influence at all.
Set this to for a enchant to 0 and this enchant will be free to repair.

Possible values:
PROTECTION_ENVIRONMENTAL, PROTECTION_FIRE, PROTECTION_FALL, PROTECTION_EXPLOSIONS, PROTECTION_PROJECTILE, OXYGEN, WATER_WORKER,
DAMAGE_ALL, DAMAGE_UNDEAD, DAMAGE_ARTHROPODS, KNOCKBACK, FIRE_ASPECT, LOOT_BONUS_MOBS, DIG_SPEED, SILK_TOUCH, DURABILITY, LOOT_BONUS_BLOCKS
```
special_enchant_multiplier:
    DURABILITY: 200 # value between 0 (for free) and 200
```

### `use_highest_enchant` ###
With use highest enchant you have another option to regulate the price for enchanted items.
If this is set to false the `enchant_multiplier` will be multiplied by the sum of all the levels of the enchants on the item.
For a tool with Unbreaking II, Fortune III and Efficiency IV this would be nine times `enchant_multiplier`.
If you set this option to true, only the highest enchant counts and it would only be four times `enchant_multiplier`
```
use_highest_enchant: false # or true
```

### `discount` ###
With this option you can set discounts for the repair price. Its like the 10% bonus repair with the Minecraft repair function.
A discount of 100 means it costs nothing. Even if over repair is activated it only repairs to 100%. One base item is still needed to activate the repair, but you get it back afterwards.
With a discount of 99 it will costs 1 base item, but with over repair activated it will always get to a durability of 200%.
```
discount: 10 # value between 0 (no discount) to 100 (one base item)
discount_groups: {}
```

Like most of the settings you can set this for each permission group, if vault is activated.
```
discount: 10 # around 14 diamonds for a pick axe with a level 4 enchantment (multiplier 100)
discount:
  Sponsor: 25 # around 4 diamonds for a pick axe with a level 4 enchantment (multiplier 20)
```

### base item configuration ###
If you want, it is possible to change the the base item and amount needed to repair an item with no enchants from 0% to 100% durability.
To this base costs the enchantment costs will be added and after all the discount subtracted.

The item configuration is in the `items.yml`. If you remove an item there, it is not possible to repair it, until you add it again.
But you can also additional items.
_If anyone wants to add none standard minecraft items, i will try to add this. Maybe it even works already? I have no experience with such plugins._
```
diamond_axe:            # this is the item you want to repair (possible are names or ids)
    base_item: diamond  # this is the base item, which you should add to the grid (possible are names or ids)
    base_amount: 3      # this is the amount of the base_item you need to repair the item from 0% to 100% durability
```

Because I like overkill, there are more options. None of the default recipes have this, so you have to manually add these if you want these.
With these additional configs you can overwrite the repair behaviour for some special items.
Like change the `keep_enchantments_chance`, `enchant_multiplier`, `allow_over_repair` and `use_highest_enchant`.
This overwrites the default and group values for this setting. If you want the default/group values, remove the option or set it to `default`.
```
diamond_sword:                         # see above
    base_item: diamond                 # see above
    base_amount: 2                     # see above
    keep_enchantments_chance: default  # use the default value set in congig.yml (global/group)
    enchant_multiplier: 200            # repair costs for weapon enchantments are more valuable
    allow_over_repair: false           # you are a PVP Server? Definitely set this to false for all armour and weapons.
    use_highest_enchant: true          # high multiplier but only for the highest enchant, sounds fair?
    # more are coming (exp costs for repair? exp get for repair? economy costs? ...?)
```

## Permissions ##

`RepairRecipe.repair` - Player is allowed to repair items. Default: true

`RepairRecipe.repair.enchant` - Player is allowed to keep enchantments. Player without this permission, can repair their items, but will lose all enchantments. Default: true

`RepairRecipe.repair.overRepair` - Player is allowed to over repair items. Default: false

`RepairRecipe.admin` - Player can use the commands /repairrecipe reload and /repairrecipe debug

## Commands ##

`/repairrecipe reload` - Reload the `config.yml` file. To reload `items.yml` restart the server.

`/repairrecipe debug [true|false]` - Enable debug mode. Many debug messages will appear in the server log and some to the player.
Do not enable this as long as you do not want to report a bug.

## Credits ##

I want to thank everyone who gives me feedback and tests the plugin. If you help me, I will add you to this list ;)

* Coding: attrib
* Tester: [Maresi](http://dev.bukkit.org/profiles/Maresi/), [TimeSpawning]{https://github.com/TimeSpawning}
* Video by [spitfire25565](http://www.youtube.com/user/Spitfire25565)
* Additional Feature Requests and Ideas: [Maresi](http://dev.bukkit.org/profiles/Maresi/), [OriginalMadman](http://dev.bukkit.org/profiles/OriginalMadman/)
