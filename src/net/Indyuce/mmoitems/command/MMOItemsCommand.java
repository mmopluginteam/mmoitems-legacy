package net.Indyuce.mmoitems.command;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.Ability.AbilityData;
import net.Indyuce.mmoitems.api.AmountReader;
import net.Indyuce.mmoitems.api.DropItem;
import net.Indyuce.mmoitems.api.Identification;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.StaffSpirit;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.UpdaterData;
import net.Indyuce.mmoitems.comp.flags.FlagPlugin.CustomFlag;
import net.Indyuce.mmoitems.gui.ItemBrowser;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.listener.ItemUpdater;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class MMOItemsCommand implements CommandExecutor {
	private Random random = new Random();

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("mmoitems.op")) {
			Message.NOT_ENOUGH_PERMS_COMMAND.format(ChatColor.RED).send(sender);
			return true;
		}

		// ==================================================================================================================================
		if (args.length < 1) {
			new PluginHelp(sender).open(1);
			return true;
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("help")) {
			if (args.length < 2) {
				new PluginHelp(sender).open(1);
				return true;
			}

			int page = 0;
			try {
				page = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number.");
			}

			new PluginHelp(sender).open(page);
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("browse")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			new ItemBrowser((Player) sender).open();
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("checkflags")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			Player player = (Player) sender;
			sender.sendMessage("");
			sender.sendMessage("Plugin Used = " + ChatColor.AQUA + MMOItems.plugin.getFlags().getClass().getSimpleName());
			sender.sendMessage("");
			sender.sendMessage("pvp = " + ChatColor.AQUA + MMOItems.plugin.getFlags().isPvpAllowed(player.getLocation()));
			for (CustomFlag flag : CustomFlag.values())
				sender.sendMessage(flag.getPath() + " = " + ChatColor.AQUA + MMOItems.plugin.getFlags().isFlagAllowed(player, flag));
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("checkstat")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			Stat stat = Stat.safeValueOf(args[1].toUpperCase().replace("-", "_"));
			if (stat == null) {
				sender.sendMessage(ChatColor.RED + "Couldn't find the stat called " + args[1].toUpperCase().replace("-", "_") + ".");
				return true;
			}

			Player player = (Player) sender;
			player.sendMessage(ChatColor.AQUA + stat.name() + " = " + PlayerData.get((Player) sender).getStat(stat));
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("checkattribute")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			if (args.length < 2)
				return true;

			Player player = (Player) sender;
			try {
				AttributeInstance att = player.getAttribute(Attribute.valueOf(args[1].toUpperCase().replace("-", "_")));
				sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
				sender.sendMessage(ChatColor.AQUA + "Default Value = " + ChatColor.RESET + att.getDefaultValue());
				sender.sendMessage(ChatColor.AQUA + "Base Value = " + ChatColor.RESET + att.getBaseValue());
				sender.sendMessage(ChatColor.AQUA + "Value = " + ChatColor.RESET + att.getValue());
			} catch (Exception e) {
				player.sendMessage("Couldn't find attribute.");
			}
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("checkupdater")) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
				for (String s : ItemUpdater.getItemPaths())
					sender.sendMessage(ChatColor.RED + s + ChatColor.WHITE + " - " + ChatColor.RED + ItemUpdater.getData(s).getUUID().toString());
				return true;
			}
			try {
				UpdaterData data = ItemUpdater.getData(args[1].toUpperCase().replace("-", "_"));
				sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
				sender.sendMessage(ChatColor.AQUA + "UUID = " + ChatColor.RESET + data.getUUID().toString());
				sender.sendMessage(ChatColor.AQUA + "Keep Enchants = " + ChatColor.RESET + data.keepEnchants());
				sender.sendMessage(ChatColor.AQUA + "Keep Lore = " + ChatColor.RESET + data.keepLore());
				sender.sendMessage(ChatColor.AQUA + "Keep Durability = " + ChatColor.RESET + data.keepDurability());
				sender.sendMessage(ChatColor.AQUA + "Keep Name = " + ChatColor.RESET + data.keepName());
			} catch (Exception e) {
				sender.sendMessage("Couldn't find updater data.");
			}
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("checktags")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			Player player = (Player) sender;
			player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
			for (String s : MMOItems.plugin.getNMS().getTags(player.getItemInHand()))
				player.sendMessage("- " + s);
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("checktag")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			Player player = (Player) sender;
			if (args.length < 2)
				return true;

			player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
			player.sendMessage(ChatColor.AQUA + "Boolean = " + ChatColor.RESET + MMOItems.plugin.getNMS().getBooleanTag(player.getItemInHand(), "MMOITEMS_" + args[1].toUpperCase().replace("-", "_")));
			player.sendMessage(ChatColor.AQUA + "Double = " + ChatColor.RESET + MMOItems.plugin.getNMS().getDoubleTag(player.getItemInHand(), "MMOITEMS_" + args[1].toUpperCase().replace("-", "_")));
			player.sendMessage(ChatColor.AQUA + "String = " + ChatColor.RESET + MMOItems.plugin.getNMS().getStringTag(player.getItemInHand(), "MMOITEMS_" + args[1].toUpperCase().replace("-", "_")));
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("settag")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			Player player = (Player) sender;
			if (args.length < 3)
				return true;
			try {
				player.setItemInHand(MMOItems.plugin.getNMS().addTag(player.getItemInHand(), new ItemTag(args[1].toUpperCase().replace("-", "_"), args[2].replace("%%", " "))));
				player.sendMessage("Successfully set tag.");

			} catch (Exception e) {
				player.sendMessage("Couldn't set tag.");
			}
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("sudoconsole")) {
			if (args.length < 2)
				return true;

			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.join(" ", Arrays.asList(args)).split("\\ ", 2)[1]);
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("unidentify")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			Player player = (Player) sender;
			ItemStack item = player.getItemInHand();
			if (Type.get(item) == null) {
				sender.sendMessage(MMOItems.getPrefix() + "Couldn't unidentify the item you are holding.");
				return true;
			}

			if (MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_UNIDENTIFIED")) {
				sender.sendMessage(MMOItems.getPrefix() + "The item you are holding is already unidentified.");
				return true;
			}

			player.setItemInHand(new Identification(item).unidentify());
			sender.sendMessage(MMOItems.getPrefix() + "Successfully unidentified the item you are holding.");
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("identify")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			Player player = (Player) sender;
			ItemStack item = player.getItemInHand();
			String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_UNIDENTIFIED_ITEM");
			if (tag.equals("")) {
				sender.sendMessage(MMOItems.getPrefix() + "The item you are holding is already identified.");
				return true;
			}

			player.setItemInHand(new Identification(item).identify());
			sender.sendMessage(MMOItems.getPrefix() + "Successfully identified the item you are holding.");
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("info")) {
			Player player = args.length > 1 ? Bukkit.getPlayer(args[1]) : (sender instanceof Player ? (Player) sender : null);
			if (player == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Couldn't find the target player.");
				return true;
			}

			sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " Player Information " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
			sender.sendMessage(ChatColor.WHITE + "Information about " + ChatColor.LIGHT_PURPLE + player.getName());
			sender.sendMessage("");
			sender.sendMessage(ChatColor.WHITE + "Player Class: " + ChatColor.LIGHT_PURPLE + MMOItems.getRPG().getClass(player));
			sender.sendMessage(ChatColor.WHITE + "Player Level: " + ChatColor.LIGHT_PURPLE + MMOItems.getRPG().getLevel(player));
			sender.sendMessage(ChatColor.WHITE + "Player Mana: " + ChatColor.LIGHT_PURPLE + MMOItems.getRPG().getMana(player));
			sender.sendMessage(ChatColor.WHITE + "Player Stamina: " + ChatColor.LIGHT_PURPLE + MMOItems.getRPG().getStamina(player));
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("heal")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			Player player = (Player) sender;
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
			player.setFireTicks(0);
			player.setSaturation(12);
			for (PotionEffectType pe : new PotionEffectType[] { PotionEffectType.POISON, PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HUNGER, PotionEffectType.WEAKNESS, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING })
				player.removePotionEffect(pe);
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("reload")) {
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("adv-recipes")) {
					int crErrors = MMOItems.plugin.getRecipes().loadRecipes();
					sender.sendMessage(MMOItems.getPrefix() + ChatColor.GRAY + "Successfully reloaded the advanced recipes." + (crErrors > 0 ? " " + ChatColor.RED + crErrors + " error" + (crErrors > 1 ? "s" : "") + " " + (crErrors > 1 ? "were" : "was") + " found." : "") + ChatColor.GRAY + " There are now " + ChatColor.GREEN + MMOItems.plugin.getRecipes().getRecipes().size() + ChatColor.GRAY + " available recipes.");
				}
				return true;
			}

			MMOItems.getLanguage().reload();
			sender.sendMessage(MMOItems.getPrefix() + MMOItems.plugin.getName() + " " + MMOItems.plugin.getDescription().getVersion() + " reloaded.");
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("copy")) {
			if (args.length < 4) {
				sender.sendMessage(MMOItems.getPrefix() + "Usage: /mi copy <type> <copied-item-id> <new-item-id>");
				return true;
			}

			Type type = Type.safeValueOf(args[1]);
			if (type == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item type called " + args[1].toUpperCase().replace("-", "_") + ".");
				sender.sendMessage(MMOItems.getPrefix() + "Type " + ChatColor.GREEN + "/mi list type " + ChatColor.GRAY + "to see all the available item types.");
				return true;
			}

			FileConfiguration config = type.getConfigFile();
			String id1 = args[2].toUpperCase();
			if (!config.contains(id1)) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item called " + id1 + ".");
				return true;
			}

			String id2 = args[3].toUpperCase();
			if (config.contains(id2)) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is already an item called " + id2 + "!");
				return true;
			}

			config.set(id2, config.getConfigurationSection(id1));
			type.saveConfigFile(config, id2);
			if (sender instanceof Player)
				new ItemEdition((Player) sender, type, id2).open();
			sender.sendMessage(MMOItems.getPrefix() + ChatColor.GREEN + "You successfully copied " + id1 + " to " + id2 + "!");
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("allitems")) {
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
			sender.sendMessage(ChatColor.GREEN + "List of all mmoitems:");
			for (Type type : MMOItems.plugin.getTypes().getAll()) {
				FileConfiguration config = type.getConfigFile();
				for (String s : config.getKeys(false))
					sender.sendMessage("* " + ChatColor.GREEN + s + (config.getConfigurationSection(s).contains("name") ? " " + ChatColor.WHITE + "(" + ChatColor.translateAlternateColorCodes('&', config.getString(s + ".name")) + ChatColor.WHITE + ")" : ""));
			}
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("itemlist")) {
			if (args.length < 2) {
				sender.sendMessage(MMOItems.getPrefix() + "Usage: /mi itemlist <type>");
				return false;
			}

			Type type = Type.safeValueOf(args[1]);
			if (type == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item type called " + args[1].toUpperCase().replace("-", "_"));
				sender.sendMessage(MMOItems.getPrefix() + "Type " + ChatColor.GREEN + "/mi list type " + ChatColor.GRAY + "to see all the available item types.");
				return true;
			}

			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
			sender.sendMessage(ChatColor.GREEN + "List of all items in " + type.getId().toLowerCase() + ".yml:");
			FileConfiguration config = type.getConfigFile();
			if (!(sender instanceof Player)) {
				for (String s : config.getKeys(false))
					sender.sendMessage("* " + ChatColor.GREEN + s + (config.getConfigurationSection(s).contains("name") ? " " + ChatColor.WHITE + "(" + ChatColor.translateAlternateColorCodes('&', config.getString(s + ".name")) + ChatColor.WHITE + ")" : ""));
				return true;
			}
			for (String s : config.getKeys(false)) {
				String nameFormat = config.getConfigurationSection(s).contains("name") ? " " + ChatColor.WHITE + "(" + ChatColor.translateAlternateColorCodes('&', config.getString(s + ".name")) + ChatColor.WHITE + ")" : "";
				MMOItems.plugin.getNMS().sendJson((Player) sender, "{\"text\":\"* " + ChatColor.GREEN + s + nameFormat + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/mi edit " + type.getId() + " " + s + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Click to edit " + (nameFormat.equals("") ? s : ChatColor.translateAlternateColorCodes('&', config.getString(s + ".name"))) + ChatColor.WHITE + ".\",\"color\":\"white\"}}}");
			}
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("list")) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " MMOItems: lists " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "/mi list type " + ChatColor.WHITE + "shows all item types (sword, axe...)");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "/mi list spirit " + ChatColor.WHITE + "shows all available staff spirits");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + "/mi list ability " + ChatColor.WHITE + "shows all available abilities");
				sender.sendMessage("");
				sender.sendMessage("Materials/Blocks: " + ChatColor.LIGHT_PURPLE + "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
				sender.sendMessage("Entities/Monsters: " + ChatColor.LIGHT_PURPLE + "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html");
				sender.sendMessage("Potion Effects: " + ChatColor.LIGHT_PURPLE + "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html");
				sender.sendMessage("Enchantments: " + ChatColor.LIGHT_PURPLE + "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html");
				return true;
			}

			// ability list
			if (args[1].equalsIgnoreCase("ability")) {
				sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " Abilities " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
				sender.sendMessage(ChatColor.WHITE + "Here are all the abilities you can bind to items.");
				sender.sendMessage(ChatColor.WHITE + "The values inside brackets are " + ChatColor.UNDERLINE + "modifiers" + ChatColor.WHITE + " which allow you to change the ability values (cooldown, damage...)");
				for (Ability a : MMOItems.plugin.getAbilities().getAll()) {
					String modFormat = ChatColor.GRAY + String.join(ChatColor.WHITE + ", " + ChatColor.GRAY, a.getModifiers());
					modFormat = ChatColor.WHITE + "(" + modFormat + ChatColor.WHITE + ")";
					sender.sendMessage("* " + ChatColor.LIGHT_PURPLE + a.getName() + " " + modFormat);
				}
			}

			// item type list
			if (args[1].equalsIgnoreCase("type")) {
				sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " Item Types " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
				MMOItems.plugin.getTypes().getAll().forEach(type -> sender.sendMessage("* " + ChatColor.LIGHT_PURPLE + type.getName()));
			}

			// staff spirit list
			if (args[1].equalsIgnoreCase("spirit")) {
				sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " Staff Spirits " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
				for (StaffSpirit ss : StaffSpirit.values()) {
					String lore = !ss.hasLore() ? " " + ChatColor.WHITE + ">> " + ChatColor.GRAY + "" + ChatColor.ITALIC + ss.getLore() : "";
					sender.sendMessage("* " + ChatColor.LIGHT_PURPLE + ss.getName() + lore);
				}
			}
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("load")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			if (args.length < 3) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Usage: /mi " + args[0] + " <type> <item-id>");
				return false;
			}

			Type type = Type.safeValueOf(args[1]);
			if (type == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item type called " + args[1].toUpperCase().replace("-", "_") + ".");
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type" + ChatColor.RED + " to see all the available item types.");
				return true;
			}

			String name = args[2].toUpperCase().replace("-", "_");
			FileConfiguration config = type.getConfigFile();
			if (config.contains(name)) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is already an item called " + name + ".");
				return true;
			}

			ItemStack item = ((Player) sender).getItemInHand();
			if (args[0].equalsIgnoreCase("load")) {
				if (item == null || item.getType() == Material.AIR) {
					sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Please hold something in your hand.");
					return true;
				}

				config.set(name + ".durability", item.getDurability());
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasDisplayName())
						config.set(name + ".name", item.getItemMeta().getDisplayName().replace("§", "&"));
					if (item.getItemMeta().hasLore()) {
						List<String> lore = new ArrayList<>();
						for (String line : item.getItemMeta().getLore())
							lore.add(line.replace("§", "&"));
						config.set(name + ".lore", lore);
					}
					if (item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))
						config.set(name + ".hide-enchants", true);
					String skullTextureUrl = MMOUtils.getSkullTextureURL(item);
					if (!skullTextureUrl.equals(""))
						config.set(name + ".skull-texture", skullTextureUrl);
				}
				if (MMOItems.plugin.getNMS().getBooleanTag(item, "Unbreakable"))
					config.set(name + ".unbreakable", true);
				for (Enchantment enchant : item.getEnchantments().keySet())
					config.set(name + ".enchants." + enchant.getName(), item.getEnchantmentLevel(enchant));
			}
			config.set(name + ".material", args[0].equalsIgnoreCase("load") ? item.getType().name() : "NETHER_STALK");

			type.saveConfigFile(config, name);
			if (sender instanceof Player)
				new ItemEdition((Player) sender, type, name).open();
			sender.sendMessage(MMOItems.getPrefix() + ChatColor.GREEN + "You successfully " + args[0].replace("d", "de") + "d " + name + "!");
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("drop")) {
			if (args.length != 10) {
				sender.sendMessage(MMOItems.getPrefix() + "Usage: /mi drop <type> <item-id> <world-name> <x> <y> <z> <drop-chance> <[min]-[max]> <unidentified-chance>");
				return true;
			}

			Type type = Type.safeValueOf(args[1]);
			if (type == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item type called " + args[1].toUpperCase().replace("-", "_") + ".");
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type " + ChatColor.RED + "to see all the available item types.");
				return true;
			}

			String name = args[2].toUpperCase().replace("-", "_");
			FileConfiguration config = type.getConfigFile();
			if (!config.contains(name)) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item called " + name + ".");
				return true;
			}

			World world = Bukkit.getWorld(args[3]);
			if (world == null) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Couldn't find the world called " + args[3] + ".");
				return true;
			}

			double x, y, z, dropChance, unidentifiedChance;
			int min, max;

			try {
				x = Double.parseDouble(args[4]);
			} catch (Exception e) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + args[4] + " is not a valid number.");
				return true;
			}

			try {
				y = Double.parseDouble(args[5]);
			} catch (Exception e) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + args[5] + " is not a valid number.");
				return true;
			}

			try {
				z = Double.parseDouble(args[6]);
			} catch (Exception e) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + args[6] + " is not a valid number.");
				return true;
			}

			try {
				dropChance = Double.parseDouble(args[7]);
			} catch (Exception e) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + args[7] + " is not a valid number.");
				return true;
			}

			try {
				unidentifiedChance = Double.parseDouble(args[9]);
			} catch (Exception e) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + args[9] + " is not a valid number.");
				return true;
			}

			String[] splitAmount = args[8].split("\\-");
			if (splitAmount.length != 2) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "The drop quantity format is incorrect.");
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Format: [min]-[max]");
				return true;
			}

			try {
				min = Integer.parseInt(splitAmount[0]);
			} catch (Exception e) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + splitAmount[0] + " is not a valid number.");
				return true;
			}

			try {
				max = Integer.parseInt(splitAmount[1]);
			} catch (Exception e) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + splitAmount[1] + " is not a valid number.");
				return true;
			}

			DropItem dropItem = new DropItem(type, name, dropChance / 100, unidentifiedChance / 100, min, max);
			if (!dropItem.isDropped())
				return true;

			ItemStack item = dropItem.getItem();
			if (item == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "An error occured while attempting to generate the item called " + name + ".");
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "See console for more information!");
				return true;
			}

			world.dropItem(new Location(world, x, y, z), item);
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
			if (args.length < 3) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Usage: /mi " + args[0] + " <type> <item-id>");
				return false;
			}

			Type type = Type.safeValueOf(args[1]);
			if (type == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item type called " + args[1].toUpperCase().replace("-", "_") + ".");
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type" + ChatColor.RED + " to see all the available item types.");
				return true;
			}

			String id = args[2].toUpperCase().replace("-", "_");
			FileConfiguration config = type.getConfigFile();
			if (!config.contains(id)) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item called " + id + ".");
				return true;
			}

			config.set(id, null);
			type.saveConfigFile(config, id);

			/*
			 * remove the item updater data and uuid data from the plugin to
			 * prevent other severe issues from happening that could potentially
			 * spam your console
			 */
			String path = type.getId() + "." + id;
			ItemUpdater.disableUpdater(path);
			MMOItems.getLanguage().getItemUUIDs().set(path, null);

			sender.sendMessage(MMOItems.getPrefix() + ChatColor.GREEN + "You successfully deleted " + id + ".");
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("edit")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}

			if (args.length < 3) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Usage: /mi edit <type> <item-id>");
				return false;
			}

			Type type = Type.safeValueOf(args[1]);
			if (type == null) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item type called " + args[1].toUpperCase().replace("-", "_") + ".");
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type" + ChatColor.RED + " to see all the available item types.");
				return true;
			}

			String id = args[2].toUpperCase().replace("-", "_");
			FileConfiguration config = type.getConfigFile();
			if (!config.contains(id)) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item called " + id + ".");
				return true;
			}

			ItemStack item = MMOItems.getItem(type, id);
			if (item == null || item.getType() == Material.AIR) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + "An error occured while attempting to generate the item called " + args[2].toUpperCase() + ".");
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + "See console for more information!");
				return true;
			}

			long old = System.currentTimeMillis();
			new ItemEdition((Player) sender, type, args[2], 1, item).open();
			long ms = System.currentTimeMillis() - old;
			MMOItems.plugin.getNMS().sendActionBar((Player) sender, ChatColor.YELLOW + "Took " + ms + "ms (" + new DecimalFormat("#.##").format(ms / 50.) + "tick" + (ms > 99 ? "s" : "") + ") to open the menu.");
		}
		// ==================================================================================================================================
		else if (args[0].equalsIgnoreCase("ability")) {
			if (args.length < 3 && !(sender instanceof Player)) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Please specify a player to use this command.");
				return true;
			}

			if (args.length < 2) {
				((Player) sender).sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Usage: /mi ability <ability> (player) (modifier1) (value1) (modifier2) (value2)...");
				return false;
			}

			// target
			Player target = args.length > 2 ? Bukkit.getPlayer(args[2]) : (Player) sender;
			if (target == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Couldn't find player called " + args[2] + ".");
				return true;
			}

			// ability
			String key = args[1].toUpperCase().replace("-", "_");
			if (!MMOItems.plugin.getAbilities().hasAbility(key)) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Couldn't find ability " + key + ".");
				return true;
			}

			Ability ability = MMOItems.plugin.getAbilities().getAbility(key);

			AbilityData data = new AbilityData(ability);
			for (int j = 3; j < args.length - 1; j += 2) {
				String name = args[j];
				String value = args[j + 1];

				try {
					data.setModifier(name, Double.parseDouble(value));
				} catch (Exception e) {
					sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Wrong format: {" + name + " " + value + "}");
					return true;
				}
			}

			ability.cast(PlayerData.get(target), data);
		}
		// ==================================================================================================================================
		else if (args.length > 1) {
			if (args.length < 3 && !(sender instanceof Player)) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Please specify a player to use this command.");
				return false;
			}

			// type
			Type type = Type.safeValueOf(args[0]);
			if (type == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "There is no item type called " + args[0] + ".");
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type" + ChatColor.RED + " to see all the available item types.");
				return true;
			}

			// item
			ItemStack item = MMOItems.getItem(type, args[1]);
			if (item == null || item.getType() == Material.AIR) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Couldn't find/generate the item called " + args[1].toUpperCase() + ".");
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Check your console for potential item generation errors.");
				return true;
			}

			// amount
			int amount = 1;
			if (args.length > 3) {
				AmountReader amountReader = new AmountReader(args[3]);
				if (!amountReader.isValid()) {
					sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "" + args[3] + " is not a valid amount.");
					return true;
				}
				amount = amountReader.getRandomAmount();
			}
			item.setAmount(amount);

			// target
			Player target = args.length > 2 ? Bukkit.getPlayer(args[2]) : (Player) sender;
			if (target == null) {
				sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Couldn't find player called " + args[2] + ".");
				return true;
			}

			// unidentified chance
			double unidentifiedChance = 0;
			if (args.length > 4) {
				try {
					unidentifiedChance = Double.parseDouble(args[4]);
				} catch (Exception e) {
					sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "" + args[4] + " is not a valid number.");
					return true;
				}
			}

			// drop chance
			double dropChance = 0;
			if (args.length > 5) {
				try {
					dropChance = Double.parseDouble(args[5]);
				} catch (Exception e) {
					sender.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "" + args[5] + " is not a valid number.");
					return true;
				}
			}

			if (dropChance > 0 && new Random().nextDouble() > dropChance / 100)
				return true;

			if (unidentifiedChance > 0 && random.nextDouble() < unidentifiedChance / 100)
				item = new Identification(item).unidentify();

			// message
			if (sender != target)
				Message.GAVE_ITEM.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(item), "#amount#", (item.getAmount() > 1 ? " x" + item.getAmount() : ""), "#player#", target.getName()).send(sender);
			Message.RECEIVED_ITEM.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(item), "#amount#", (item.getAmount() > 1 ? " x" + item.getAmount() : "")).send(target);

			// item
			if (target.getInventory().firstEmpty() == -1) {
				target.getWorld().dropItem(target.getLocation(), item);
				return true;
			}
			target.getInventory().addItem(item);
		}

		return false;
	}
}
