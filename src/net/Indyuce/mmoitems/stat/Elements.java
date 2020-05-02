package net.Indyuce.mmoitems.stat;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Element;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ElementsEdition;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;

public class Elements extends ItemStat {
	public Elements() {
		super(new ItemStack(Material.SLIME_BALL), "Elements", new String[] { "The elements of your item." }, "element", new String[] { "slashing", "piercing", "blunt", "offhand", "range", "tool", "armor" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new ElementsEdition(player, type, path).open();
		
		if (event.getAction() == InventoryAction.PICKUP_HALF)
			if (config.getConfigurationSection(path).contains("element")) {
				config.set(path + ".element", null);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Elements successfully removed.");
			}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		String elementPath = ElementsEdition.correspondingSlot.get(info[0]);
		double value = 0;
		try {
			value = Double.parseDouble(msg);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + msg + " is not a valid number.");
			return false;
		}
		config.set(path + ".element." + elementPath, value);
		if (value == 0)
			config.set(path + ".element." + elementPath, null);

		// clear element config section
		String elementName = elementPath.split("\\.")[0];
		if (config.getConfigurationSection(path).contains("element")) {
			if (config.getConfigurationSection(path + ".element").contains(elementName))
				if (config.getConfigurationSection(path + ".element." + elementName).getKeys(false).isEmpty())
					config.set(path + ".element." + elementName, null);
			if (config.getConfigurationSection(path + ".element").getKeys(false).isEmpty())
				config.set(path + ".element", null);
		}

		type.saveConfigFile(config, path);
		new ElementsEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + MMOUtils.caseOnWords(elementPath.replace(".", " ")) + ChatColor.GRAY + " successfully changed to " + value + ".");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		if (!config.getConfigurationSection(path).contains("element"))
			lore.add(ChatColor.RED + "No element.");
		else if (config.getConfigurationSection(path + ".element").getKeys(false).isEmpty())
			lore.add(ChatColor.RED + "No element.");
		else
			for (String s1 : config.getConfigurationSection(path + ".element").getKeys(false)) {
				String element = s1.substring(0, 1).toUpperCase() + s1.substring(1);
				lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + element + ChatColor.GRAY + ": " + ChatColor.RED + "" + ChatColor.BOLD + config.getDouble(path + ".element." + s1 + ".damage") + "%" + ChatColor.GRAY + " | " + ChatColor.WHITE + "" + ChatColor.BOLD + config.getDouble(path + ".element." + s1 + ".defense") + "%");
			}
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to access the elements edition menu.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove all the elements.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		for (Element element : Element.values()) {
			String elementPath = element.name().toLowerCase();
			if (!config.getConfigurationSection("element").contains(elementPath))
				continue;

			for (Element.StatType statType : Element.StatType.values()) {
				String statTypePath = statType.name().toLowerCase();
				double value = config.getDouble("element." + elementPath + "." + statTypePath);

				if (value != 0)
					item.addElementStat(element, statType, value);
			}
		}

		return true;
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		item.error("Elements", "To add elements to an item, please use MMOItem#addElement(Element, StatType, double) instead.");
		return false;
	}
}
