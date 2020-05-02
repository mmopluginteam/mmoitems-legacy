package net.Indyuce.mmoitems.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat.StatType;
import net.Indyuce.mmoitems.stat.Abilities;
import net.Indyuce.mmoitems.stat.Advanced_Crafting_Recipe;
import net.Indyuce.mmoitems.stat.Advanced_Crafting_Recipe_Permission;
import net.Indyuce.mmoitems.stat.Armor;
import net.Indyuce.mmoitems.stat.Armor_Toughness;
import net.Indyuce.mmoitems.stat.Arrow_Particles;
import net.Indyuce.mmoitems.stat.Attack_Damage;
import net.Indyuce.mmoitems.stat.Attack_Speed;
import net.Indyuce.mmoitems.stat.Commands;
import net.Indyuce.mmoitems.stat.Consume_Sound;
import net.Indyuce.mmoitems.stat.Crafting_Recipe;
import net.Indyuce.mmoitems.stat.Disable_Stat;
import net.Indyuce.mmoitems.stat.Display_Name;
import net.Indyuce.mmoitems.stat.Displayed_Type;
import net.Indyuce.mmoitems.stat.Durability;
import net.Indyuce.mmoitems.stat.Dye_Color;
import net.Indyuce.mmoitems.stat.Effects;
import net.Indyuce.mmoitems.stat.Elements;
import net.Indyuce.mmoitems.stat.Enchants;
import net.Indyuce.mmoitems.stat.Furnace_Recipe;
import net.Indyuce.mmoitems.stat.Gem_Sockets;
import net.Indyuce.mmoitems.stat.Hide_Enchants;
import net.Indyuce.mmoitems.stat.Hide_Potion_Effects;
import net.Indyuce.mmoitems.stat.Inedible;
import net.Indyuce.mmoitems.stat.Item_Particles;
import net.Indyuce.mmoitems.stat.Item_Set;
import net.Indyuce.mmoitems.stat.Item_Tier;
import net.Indyuce.mmoitems.stat.Item_Type_Restriction;
import net.Indyuce.mmoitems.stat.Knockback_Resistance;
import net.Indyuce.mmoitems.stat.Lore;
import net.Indyuce.mmoitems.stat.MMaterial;
import net.Indyuce.mmoitems.stat.Max_Custom_Durability;
import net.Indyuce.mmoitems.stat.Max_Health;
import net.Indyuce.mmoitems.stat.Movement_Speed;
import net.Indyuce.mmoitems.stat.Perm_Effects;
import net.Indyuce.mmoitems.stat.Permission;
import net.Indyuce.mmoitems.stat.Potion_Color;
import net.Indyuce.mmoitems.stat.Required_Class;
import net.Indyuce.mmoitems.stat.Required_Level;
import net.Indyuce.mmoitems.stat.Restore;
import net.Indyuce.mmoitems.stat.Shapeless_Recipe;
import net.Indyuce.mmoitems.stat.Shield_Pattern;
import net.Indyuce.mmoitems.stat.Skull_Texture;
import net.Indyuce.mmoitems.stat.Soulbound_Level;
import net.Indyuce.mmoitems.stat.Staff_Spirit;
import net.Indyuce.mmoitems.stat.Two_Handed;
import net.Indyuce.mmoitems.stat.Unbreakable;
import net.Indyuce.mmoitems.stat.Vanilla_Eating_Animation;
import net.Indyuce.mmoitems.stat.Will_Break;

public enum Stat {
	MATERIAL(new MMaterial()),
	DURABILITY(new Durability()),
	MAX_CUSTOM_DURABILITY(new Max_Custom_Durability()),
	WILL_BREAK(new Will_Break()),
	NAME(new Display_Name()),
	LORE(new Lore()),
	DISPLAYED_TYPE(new Displayed_Type()),
	ENCHANTS(new Enchants()),
	HIDE_ENCHANTS(new Hide_Enchants()),
	PERMISSION(new Permission()),
	ITEM_PARTICLES(new Item_Particles()),
	ARROW_PARTICLES(new Arrow_Particles()),
	DISABLE_INTERACTION(new Disable_Stat(Material.GRASS, "interaction", "Disable Interaction", "Disable any unwanted interaction:", "block placement, item use...")),
	DISABLE_CRAFTING(new Disable_Stat(Material.WORKBENCH, "crafting", "Disable Crafting", "Players can't use this item while crafting.")),
	DISABLE_SMELTING(new Disable_Stat(Material.FURNACE, "smelting", "Disable Smelting", "Players can't use this item in furnaces.")),
	DISABLE_ENCHANTING(new Disable_Stat(Material.ENCHANTMENT_TABLE, "enchanting", "Disable Enchanting", "Players can't enchant this item.")),
	DISABLE_REPAIRING(new Disable_Stat(Material.ANVIL, "repairing", "Disable Repairing", "Players can't use this item in anvils.")),
	DISABLE_ARROW_SHOOTING(new Disable_Stat(Material.ARROW, "arrow-shooting", "Disable Arrow Shooting", new DMaterial[] { ItemStat.dm(Material.ARROW) }, "Players can't shoot this", "item using a bow.")),
	DISABLE_ATTACK_PASSIVE(new Disable_Stat(Material.BARRIER, "attack-passive", "Disable Attack Passive", new String[] { "piercing", "slashing", "blunt" }, "Disables the blunt/slashing/piercing", "passive effects on attacks.")),
	REQUIRED_LEVEL(new Required_Level()),
	REQUIRED_CLASS(new Required_Class()),
	ATTACK_DAMAGE(new Attack_Damage()),
	ATTACK_SPEED(new Attack_Speed()),
	CRITICAL_STRIKE_CHANCE(new ItemStat(new ItemStack(Material.NETHER_STAR), "Critical Strike Chance", new String[] { "Critical Strikes deal more damage.", "In % chance." }, "critical-strike-chance", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	CRITICAL_STRIKE_POWER(new ItemStat(new ItemStack(Material.NETHER_STAR), "Critical Strike Power", new String[] { "The extra damage weapon crits deals.", "(Stacks with default value)", "In %." }, "critical-strike-power", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	BLOCK_POWER(new ItemStat(new ItemStack(Material.IRON_HELMET), "Block Power", new String[] { "The % of the damage your", "armor/shield can block.", "Default: 25%" }, "block-power", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	BLOCK_RATING(new ItemStat(new ItemStack(Material.IRON_HELMET, 1, (short) 1), "Block Rating", new String[] { "The chance your piece of armor", "has to block any entity attack." }, "block-rating", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	BLOCK_COOLDOWN_REDUCTION(new ItemStat(new ItemStack(Material.IRON_HELMET, 1, (short) 1), "Block Cooldown Reduction", new String[] { "Reduces the blocking cooldown (%)." }, "block-cooldown-reduction", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	DODGE_RATING(new ItemStat(new ItemStack(Material.FEATHER), "Dodge Rating", new String[] { "The chance to dodge an attack.", "Dodging completely negates", "the attack damage." }, "dodge-rating", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	DODGE_COOLDOWN_REDUCTION(new ItemStat(new ItemStack(Material.FEATHER), "Dodge Cooldown Reduction", new String[] { "Reduces the dodging cooldown (%)." }, "dodge-cooldown-reduction", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	PARRY_RATING(new ItemStat(new ItemStack(Material.BUCKET), "Parry Rating", new String[] { "The chance to parry an attack.", "Parrying negates the damage", "and knocks the attacker back." }, "parry-rating", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	PARRY_COOLDOWN_REDUCTION(new ItemStat(new ItemStack(Material.BUCKET), "Parry Cooldown Reduction", new String[] { "Reduces the parrying cooldown (%)." }, "parry-cooldown-reduction", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	RANGE(new ItemStat(new ItemStack(Material.STICK), "Range", new String[] { "The range of your item attacks." }, "range", new String[] { "staff", "whip", "wand", "musket" }, StatType.DOUBLE)),
	MANA_COST(new ItemStat(new ItemStack(Material.INK_SACK, 1, (short) 4), "Mana Cost", new String[] { "Mana spent by your weapon to be used." }, "mana-cost", new String[] { "piercing", "slashing", "blunt", "range" }, StatType.DOUBLE)),
	STAMINA_COST(new ItemStat(new ItemStack(Material.INK_SACK, 1, (short) 7), "Stamina Cost", new String[] { "Stamina spent by your weapon to be used." }, "stamina-cost", new String[] { "piercing", "slashing", "blunt", "range" }, StatType.DOUBLE)),
	ARROW_VELOCITY(new ItemStat(new ItemStack(Material.ARROW), "Arrow Velocity", new String[] { "Determins how far your", "crossbow can shoot.", "Default: 1.0" }, "arrow-velocity", new String[] { "crossbow" }, StatType.DOUBLE)),
	PVE_DAMAGE(new ItemStat(new ItemStack(Material.PORK), "PvE Damage", new String[] { "Additional damage against", "non human entities in %." }, "pve-damage", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	PVP_DAMAGE(new ItemStat(new ItemStack(Material.SKULL_ITEM), "PvP Damage", new String[] { "Additional damage", "against players in %." }, "pvp-damage", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	BLUNT_POWER(new ItemStat(new ItemStack(Material.IRON_AXE), "Blunt Power", new String[] { "The radius of the AoE attack.", "If set to 2.0, enemies within 2 blocks", "around your target will take damage." }, "blunt-power", new String[] { "blunt", "gem_stone" }, StatType.DOUBLE)),
	BLUNT_RATING(new ItemStat(new ItemStack(Material.CLAY_BRICK), "Blunt Rating", new String[] { "The force of the blunt attack.", "If set to 50%, enemies hit by the attack", "will take 50% of the initial damage." }, "blunt-rating", new String[] { "blunt", "gem_stone" }, StatType.DOUBLE)),
	MAGIC_DAMAGE(new ItemStat(new ItemStack(Material.BOOK), "Magic Damage", new String[] { "The additional damage spells deal.", "In %." }, "magic-damage", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	WEAPON_DAMAGE(new ItemStat(new ItemStack(Material.IRON_SWORD), "Weapon Damage", new String[] { "Additional on-hit weapon damage." }, "weapon-damage", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	DAMAGE_REDUCTION(new ItemStat(new ItemStack(Material.IRON_CHESTPLATE), "Damage Reduction", new String[] { "Reduces damage from any source.", "In %." }, "damage-reduction", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	FALL_DAMAGE_REDUCTION(new ItemStat(new ItemStack(Material.FEATHER), "Fall Damage Reduction", new String[] { "Reduces fall damage.", "In %." }, "fall-damage-reduction", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	FIRE_DAMAGE_REDUCTION(new ItemStat(new ItemStack(Material.BLAZE_POWDER), "Fire Damage Reduction", new String[] { "Reduces fire damage.", "In %." }, "fire-damage-reduction", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	MAGIC_DAMAGE_REDUCTION(new ItemStat(new ItemStack(Material.POTION), "Magic Damage Reduction", new String[] { "Reduce magic damage dealt by potions.", "In %." }, "magic-damage-reduction", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	UNDEAD_DAMAGE(new ItemStat(new ItemStack(Material.SKULL_ITEM), "Undead Damage", new String[] { "Deals additional damage to undead.", "In %." }, "undead-damage", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	REGENERATION(new ItemStat(new ItemStack(Material.BREAD), "Regeneration", new String[] { "Increases natural/magic health regen.", "In %." }, "regeneration", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	UNBREAKABLE(new Unbreakable()),
	TIER(new Item_Tier()),
	SET(new Item_Set()),
	ARMOR(new Armor()),
	ARMOR_TOUGHNESS(new Armor_Toughness()),
	MAX_HEALTH(new Max_Health()),
	MAX_MANA(new ItemStat(new ItemStack(Material.INK_SACK, 1, (short) 4), "Max Mana", new String[] { "Adds mana to your max mana bar." }, "max-mana", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE)),
	KNOCKBACK_RESISTANCE(new Knockback_Resistance()),
	MOVEMENT_SPEED(new Movement_Speed()),
	TWO_HANDED(new Two_Handed()),
	RESTORE(new Restore()),
	RESTORE_MANA(new ItemStat(new ItemStack(Material.INK_SACK, 1, (short) 4), "Restore Mana", new String[] { "The amount of mana", "your consumable restores." }, "restore-mana", new String[] { "consumable" }, StatType.DOUBLE)),
	RESTORE_STAMINA(new ItemStat(new ItemStack(Material.INK_SACK, 1, (short) 7), "Restore Stamina", new String[] { "The amount of stamina/power", "your consumable restores." }, "restore-stamina", new String[] { "consumable" }, StatType.DOUBLE)),
	CAN_IDENTIFY(new ItemStat(new ItemStack(Material.PAPER), "Can Identify?", new String[] { "Players can identify & make their", "item usable using this consumable." }, "can-identify", new String[] { "consumable" }, StatType.BOOLEAN)),
	CAN_DECONSTRUCT(new ItemStat(new ItemStack(Material.PAPER), "Can Deconstruct?", new String[] { "Players can deconstruct their item", "using this consumable, creating", "another random item." }, "can-deconstruct", new String[] { "consumable" }, StatType.BOOLEAN)),
	EFFECTS(new Effects()),
	PERM_EFFECTS(new Perm_Effects()),
	SOULBINDING_CHANCE(new ItemStat(new ItemStack(Material.EYE_OF_ENDER), "Soulbinding Chance", new String[] { "Defines the chance your item has to", "link another item to your soul,", "preventing other players from using it." }, "soulbinding-chance", new String[] { "consumable" }, StatType.DOUBLE)),
	SOULBOUND_BREAK_CHANCE(new ItemStat(new ItemStack(Material.EYE_OF_ENDER), "Soulbound Break Chance", new String[] { "The chance of breaking an item's", "soulbound when drag & drop'd on it.", "This chance is lowered depending", "on the soulbound's level." }, "soulbound-break-chance", new String[] { "consumable" }, StatType.DOUBLE)),
	SOULBOUND_LEVEL(new Soulbound_Level()),
	CONSUME_COOLDOWN(new ItemStat(new ItemStack(Material.COOKED_CHICKEN), "Consume Cooldown", new String[] { "The delay players must wait before", "eating twice the same consumable." }, "consume-cooldown", new String[] { "consumable" }, StatType.DOUBLE)),
	CONSUME_SOUND(new Consume_Sound()),
	DISABLE_RIGHT_CLICK_CONSUME(new Disable_Stat(Material.BARRIER, "right-click-consume", "Disable Right Click Consume", new String[] { "consumable" }, "This item will not be consumed", "when eaten by players.")),
	VANILLA_EATING_ANIMATION(new Vanilla_Eating_Animation()),
	INEDIBLE(new Inedible()),
	ITEM_TYPE_RESTRICTION(new Item_Type_Restriction()),
	SUCCESS_RATE(new ItemStat(new ItemStack(Material.EMERALD), "Success Rate", new String[] { "The chance of your gem to successfully", "apply onto an item. This value is 100%", "by default. If it is not successfully", "applied, the gem stone will be lost." }, "success-rate", new String[] { "gem_stone" }, StatType.DOUBLE)),
	CRAFTING_RECIPE(new Crafting_Recipe()),
	SHAPELESS_RECIPE(new Shapeless_Recipe()),
	FURNACE_RECIPE(new Furnace_Recipe()),
	ADVANCED_CRAFTING_RECIPE(new Advanced_Crafting_Recipe()),
	ADVANCED_CRAFTING_RECIPE_PERMISSION(new Advanced_Crafting_Recipe_Permission()),
	AUTOSMELT(new ItemStat(new ItemStack(Material.COAL, 1, (short) 1), "Autosmelt", new String[] { "If set to true, your tool will", "automaticaly smelt mined ores." }, "autosmelt", new String[] { "tool" }, StatType.BOOLEAN)),
	ELEMENTS(new Elements()),
	BOUNCING_CRACK(new ItemStat(new ItemStack(Material.COBBLE_WALL), "Bouncing Crack", new String[] { "If set to true, your tool will", "also break nearby blocks." }, "bouncing-crack", new String[] { "tool" }, StatType.BOOLEAN)),
	COMMANDS(new Commands()),
	STAFF_SPIRIT(new Staff_Spirit()),
	GEM_SOCKETS(new Gem_Sockets()),
	REPAIR(new ItemStat(new ItemStack(Material.ANVIL), "Repair", new String[] { "The amount of durability your item", "can repair when set an item." }, "repair", new String[] { "consumable" }, StatType.DOUBLE)),

	// musket
	KNOCKBACK(new ItemStat(new ItemStack(Material.IRON_BARDING), "Knockback", new String[] { "Using this musket will knock", "the user back if positive." }, "knockback", new String[] { "musket" }, StatType.DOUBLE)),
	RECOIL(new ItemStat(new ItemStack(Material.IRON_BARDING), "Recoil", new String[] { "Corresponds to the shooting innacuracy." }, "recoil", new String[] { "musket" }, StatType.DOUBLE)),
	// TODO ammunition

	// abilities
	ABILITIES(new Abilities()),

	// depending on material & durability
	SKULL_TEXTURE(new Skull_Texture()),
	DYE_COLOR(new Dye_Color()),
	POTION_COLOR(new Potion_Color()),
	SHIELD_PATTERN(new Shield_Pattern()),
	HIDE_POTION_EFFECTS(new Hide_Potion_Effects()),

	;

	private ItemStat clazz;

	private Stat(ItemStat clazz) {
		this.clazz = clazz;
	}

	public ItemStat c() {
		return clazz;
	}

	public static Stat safeValueOf(String name) {
		try {
			return Stat.valueOf(name);
		} catch (Exception e) {
			return null;
		}
	}
}
