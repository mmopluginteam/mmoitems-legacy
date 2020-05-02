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
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;

public class Furnace_Recipe extends ItemStat {
	/*
	 * the legacy version of MMOItems does not support cooking time and recipe
	 * experience because of an API bug. only the 1.13 version does
	 */
	public Furnace_Recipe() {
		super(new ItemStack(Material.FURNACE), "Furnace Recipe", new String[] { "Defines what item you need", "to smelt to get your item.", "Changing this value requires a reload." }, "furnace-craft", new String[] { "all" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.FURNACE_RECIPE).enable("Write in the chat the material you want to be the ingredient.");

		if (event.getAction() == InventoryAction.PICKUP_HALF)
			if (config.getConfigurationSection(path).contains("furnace-craft")) {
				config.set(path + ".furnace-craft", null);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed the furnace recipe.");
			}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		Material material = null;
		String format = msg.toUpperCase().replace("-", "_").replace(" ", "_");
		try {
			material = Material.valueOf(format);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + format + " is not a valid material!");
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "All materials can be found here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			return false;
		}

		config.set(path + ".furnace-craft.input", format);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + ChatColor.GOLD + MMOUtils.caseOnWords(material.name().replace("_", " ").toLowerCase()) + ChatColor.GRAY + " successfully set as the furnace recipe ingredient.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		if (!config.getConfigurationSection(path).contains("furnace-craft"))
			lore.add(ChatColor.RED + "No furnace recipe.");
		else
			lore.add(ChatColor.GRAY + "* Input: " + ChatColor.GREEN + config.getString(path + ".furnace-craft.input"));
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to set the ingredient.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to disable the furnace recipe.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item);
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		return true;
	}
}
