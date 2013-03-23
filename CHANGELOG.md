# Changelog #

## v0.4.5 ##

- Fixed item duplication, if user has no permission to repair
- Added support for global groups, not only world specific groups

## v0.4.4 ##

- Updating for Bukkit 1.4.7-R1.0

## v0.4.3-beta ##

- Fixed Ingots in Workbench doesn't get updated

## v0.4.2-beta2 ##

- Added Repair Sound
- Updated to Bukkit 1.4.6-R0.1 - not downward compatible!

## v0.4.2-beta ##

- Fixed a exception when repairing a item with level 1 enchantment and setup a keep enchantment chance
- Tested with Bukkit 1.4.2-R0.1 (dev)

## v0.4.1 ##

* Fixed a dupe bug if you put it more than one tool (Using StackableItems Plugin or /give <nick> 276 900 15)

## v0.4.0 ##

* Fixed Repaired Item is not getting updated, if only base item amount changed
* Fixed base item amount is not getting updated, if repair costs more than one item
* Fixed base item doesn't disappear, if repair costs is exactly base item amount
* Added possibility for free repairs
* Changed discount of 100 means its free as in free beer ;)
* Fixed NPE when adding new item to a empty slot in the grid to a already working repair recipe
* Added base item configuration
* Added special enchant multiplier
* Added Metrics - http://mcstats.org/
* Fixed Bow base_amount
* Fixed `allow_over_repair` needs plugin system
* Added Debug Mode Command (/repairrecipe debug)
* Added Config Reload Command (/repairrecipe reload)

## v0.2.3 ##

* Fixed repair costs with `use_highest_enchant: true`

## v0.2.2 ##

* Rewrote Repair cost formula
* Added `use_highest_enchant` configuration
* Changed `discount` to `discount_groups` and added `discount` as global configuration
* Replaced configuration `keep_enchantments` with `keep_enchantments_chance` and `keep_enchantments_chance_groups`

**Configuration File gets updated automatically.**

## v0.2 ##

* Fixed Group Discount doesn't work correctly
* Added Permission `RepairRecipe.repair.overRepair`
* Fixed Enchantment Multiplier could reduce repair costs
* Added Enchantment Multiplier per Group Configuration
* Fixed Bug when repairing Items with Vault activated but no Permission Plugin
* Fixed Bug when enabling Plugin without Vault

## v0.2-alpha1 ##

* Added Permissions `RepairRecipe.repair` and `RepairRecipe.repair.enchant`
* Added OverRepair Configuration
* Added Group Discount Configuration
* Added Keep Enchantments Configuration
* Added Enchantment Multiplier Configuration


## v0.1.1 ##

* Added Shears

## v0.1 ##

* Initial Commit
