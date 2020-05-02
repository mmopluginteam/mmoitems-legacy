package net.Indyuce.mmoitems.stat;

import java.util.ArrayList;
import java.util.Arrays;
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
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.gui.edition.RecipeEdition;

public class Crafting_Recipe extends ItemStat {
	public Crafting_Recipe() {
		super(new ItemStack(Material.WORKBENCH), "Crafting Recipe", new String[] { "The recipe of your item.", "Changing this value requires a reload." }, "craft", new String[] { "all" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new RecipeEdition(player, type, path).open();
		
		if (event.getAction() == InventoryAction.PICKUP_HALF)
			if (config.getConfigurationSection(path).contains("craft")) {
				config.set(path + ".craft", null);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Crafting Recipe successfully removed.");
			}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		Material material = null;
		int durability = 0;
		String[] split = msg.toUpperCase().replace("-", "_").replace(" ", "_").split("\\:");
		try {
			material = Material.valueOf(split[0]);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[0] + " is not a valid material!");
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "All materials can be found here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			return false;
		}
		if (split.length > 1)
			try {
				durability = Integer.parseInt(split[1]);
			} catch (Exception e1) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[1] + " is not a valid number!");
				return false;
			}

		int n = (int) info[0];
		List<String> list = new ArrayList<String>();
		if (config.getConfigurationSection(path).getKeys(false).contains("craft"))
			list = config.getStringList(path + ".craft");
		while (list.size() < 3)
			list.add("AIR AIR AIR");

		String old = list.get(n / 3).split(" ")[n % 3];
		List<String> line = Arrays.asList(list.get(n / 3).split("\\ "));
		while (line.size() < 3)
			line.add("AIR");
		line.set(n % 3, material.name() + (durability > 0 ? ":" + durability : ""));
		String line_format = line.toString();
		line_format = line_format.replace("[", "").replace("]", "").replace(",", "");
		list.set(n / 3, line_format);

		config.set(path + ".craft", list);
		type.saveConfigFile(config, path);
		new RecipeEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + old + " changed to " + material.name() + (durability > 0 ? ":" + durability : "") + ".");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		// reset if null crafting recipe.
		if (config.getStringList(path + ".craft").equals(Arrays.asList(new String[] { "AIR AIR AIR", "AIR AIR AIR", "AIR AIR AIR" })))
			config.set(path + ".craft", null);
		if (!config.getConfigurationSection(path).contains("craft"))
			lore.add(ChatColor.RED + "No crafting recipe.");
		else if (config.getStringList(path + ".craft").isEmpty())
			lore.add(ChatColor.RED + "No crafting recipe.");
		else
			for (String s1 : config.getStringList(path + ".craft")) {
				lore.add(ChatColor.GRAY + s1);
			}
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to edit the crafting recipe.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the crafting recipe.");
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
