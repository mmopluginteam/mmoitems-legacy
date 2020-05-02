package net.Indyuce.mmoitems.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Commands extends ItemStat {
	public Commands() {
		super(new ItemStack(Material.COMMAND_MINECART), "Commands", new String[] { "The commands your item", "performs when right clicked.", "&9Put a '.' before your command", "&9if you need to add an extra /." }, "commands", new String[] { "!armor", "!gem_stone", "all" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.COMMANDS).enable("Write in the chat the command you want to add.", ChatColor.AQUA + "Format: <COMMAND>|<COOLDOWN>|<DELAY>");

		if (event.getAction() == InventoryAction.PICKUP_HALF)
			if (config.getConfigurationSection(path).contains("commands")) {
				Set<String> set = config.getConfigurationSection(path + ".commands").getKeys(false);
				String last = Arrays.asList(set.toArray(new String[0])).get(set.size() - 1);
				String format = config.getString(path + ".commands." + last + ".command");
				config.set(path + ".commands." + last, null);
				if (set.size() <= 1)
					config.set(path + ".commands", null);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed /" + format + ChatColor.GRAY + ".");
			}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		String[] split = msg.split("\\|");
		if (split.length != 3) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + msg + " is not a valid <COMMAND>|<COOLDOWN>|<DELAY>.");
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Example: 'tp Notch|10|3' is /tp Notch with a 10s cooldown and a 3s delay.");
			return false;
		}

		if (config.getConfigurationSection(path).contains("commands"))
			if (config.getConfigurationSection(path + ".commands").getKeys(false).size() > 8) {
				// max command number = 8
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Your item has reached the 8 commands limit.");
				return false;
			}

		int number = -1;
		if (!config.getConfigurationSection(path).contains("commands"))
			number = 1;
		else
			for (int j = 1; j < 9; j++) {
				if (config.getConfigurationSection(path + ".commands").contains("" + j))
					continue;
				number = j;
				break;
			}
		if (number == -1) {
			player.sendMessage(MMOItems.getPrefix() + "An error occured. Please manually delete the 'commands' parameter in the item file!");
			return false;
		}

		String command = split[0];
		if (command.startsWith("."))
			command = command.replaceFirst(".", "/");

		double cooldown = 0;
		try {
			cooldown = Double.parseDouble(split[1]);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[1] + " is not a valid number!");
			return false;
		}

		double delay = 0;
		try {
			delay = Double.parseDouble(split[2]);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[1] + " is not a valid number!");
			return false;
		}

		config.set(path + ".commands." + number + ".command", command);
		config.set(path + ".commands." + number + ".cooldown", cooldown > 0 ? cooldown : null);
		config.set(path + ".commands." + number + ".delay", delay > 0 ? delay : null);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "/" + command + " successfully added.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		// reset if no command
		if (!config.getConfigurationSection(path).contains("commands"))
			lore.add(ChatColor.RED + "No command.");
		else if (config.getConfigurationSection(path + ".commands").getKeys(false).isEmpty()) {
			config.set(path + ".commands", null);
			lore.add(ChatColor.RED + "No command.");
		} else {
			for (String s : config.getConfigurationSection(path + ".commands").getKeys(false)) {
				if (!config.getConfigurationSection(path + ".commands." + s).contains("command")) {
					continue;
				}
				String format = config.getString(path + ".commands." + s + ".command");
				if (config.getConfigurationSection(path + ".commands." + s).contains("cooldown"))
					format += ChatColor.GRAY + " - " + ChatColor.GREEN + config.getDouble(path + ".commands." + s + ".cooldown") + "s" + ChatColor.GRAY;
				if (config.getConfigurationSection(path + ".commands." + s).contains("delay"))
					format += ChatColor.GRAY + " - " + ChatColor.GREEN + config.getDouble(path + ".commands." + s + ".delay") + "s delay";
				lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + "/" + format);
			}
		}
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to add a command.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the last command.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		String stringTag = "";
		List<String> commands = new ArrayList<String>();
		for (String cmd : config.getConfigurationSection("commands").getKeys(false))
			if (config.getConfigurationSection("commands." + cmd).contains("command")) {
				String command = config.getString("commands." + cmd + ".command");
				double cooldown = config.getDouble("commands." + cmd + ".cooldown");
				double delay = config.getDouble("commands." + cmd + ".delay");

				stringTag += (stringTag.length() > 0 ? "|nextCommand|" : "") + command + "|cmdCd|" + cooldown + (delay > 0 ? "|cmdCd|" + delay : "");
				commands.add(ItemStat.translate("command").replace("#cd", "" + cooldown).replace("#c", "/" + command).replace("#d", "" + delay));
			}

		item.insertInLore("commands", commands);
		item.addItemTag(new ItemTag("MMOITEMS_COMMANDS", stringTag));
		return true;
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		item.error("Commands", "To add commands to an item, please use MMOItem#setCommands(ValueCouple...) instead.");
		return false;
	}
}
