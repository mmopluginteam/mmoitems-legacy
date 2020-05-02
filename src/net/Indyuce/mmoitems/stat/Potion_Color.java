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
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;

public class Potion_Color extends ItemStat {
	public Potion_Color() {
		super(new ItemStack(Material.POTION), "Potion Color", new String[] { "The color of your potion.", "(Doesn't impact the effects)." }, "potion-color", new String[] { "all" }, StatType.STRING, dm(Material.POTION));
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.POTION_COLOR).enable("Write in the chat the RGB color you want.", ChatColor.AQUA + "Format: [RED] [GREEN] [BLUE]");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			config.set(path + ".potion-color", null);
			type.saveConfigFile(config, path);
			new ItemEdition(player, type, path).open();
			player.sendMessage(MMOItems.getPrefix() + "Successfully removed Potion Color.");
		}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		String[] split = msg.split("\\ ");
		if (split.length != 3) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + msg + " is not a valid [RED] [GREEN] [BLUE].");
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Example: '75 0 130' stands for Indigo Purple.");
			return false;
		}
		for (String s : split)
			try {
				Integer.parseInt(s);
			} catch (Exception e1) {
				player.sendMessage(MMOItems.getPrefix() + s + " is not a valid number.");
				return false;
			}

		config.set(path + ".potion-color", msg);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "Potion Color successfully changed to " + msg + ".");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		if (!config.getConfigurationSection(path).contains("potion-color")) {
			lore.add(ChatColor.GRAY + "Current Value:");
			lore.add(ChatColor.RED + "No value.");
		} else
			lore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN + config.getString(path + ".potion-color"));
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to change this value.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the potion color.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		try {
			String[] rgb = config.getString("potion-color").split("\\ ");
			return apply(item, Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
		} catch (Exception e) {
			item.error("Dye Color", "Please use this format: [red] [green] [blue]");
			return false;
		}
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		if (!item.getType().name().contains("POTION"))
			return true;

		int red = (int) values[0];
		int green = (int) values[1];
		int blue = (int) values[2];

		item.setPotionColor(red, green, blue);
		return true;
	}
}
