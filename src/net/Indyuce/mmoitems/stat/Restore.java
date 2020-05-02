package net.Indyuce.mmoitems.stat;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.StatFormat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Restore extends ItemStat {
	public Restore() {
		super(new ItemStack(Material.INK_SACK, 1, (short) 1), "Restore", new String[] { "The amount of health/food/saturation", "your consumable item restores." }, "restore", new String[] { "consumable" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		new StatEdition(inv, Stat.RESTORE).enable("Write in the chat the values you want.", ChatColor.AQUA + "Format: [HEALTH] [FOOD] [SATURATION]");
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		String[] split = msg.split("\\ ");
		if (split.length != 3) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + msg + " is not a valid [HEALTH] [FOOD] [SATURATION].");
			return false;
		}
		for (String s : split)
			try {
				Double.parseDouble(s);
			} catch (Exception e1) {
				player.sendMessage(MMOItems.getPrefix() + s + " is not a valid number.");
				return false;
			}
		double health = Double.parseDouble(split[0]);
		double food = Double.parseDouble(split[1]);
		double saturation = Double.parseDouble(split[2]);

		config.set(path + ".restore.health", (health <= 0 ? null : health));
		config.set(path + ".restore.food", (food <= 0 ? null : food));
		config.set(path + ".restore.saturation", (saturation <= 0 ? null : saturation));

		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "Restore successfully changed to " + msg + ".");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		if (!config.getConfigurationSection(path).contains("restore"))
			lore.add(ChatColor.RED + "No restore stat.");
		else if (config.getConfigurationSection(path + ".restore").getKeys(false).size() <= 0) {
			config.set(path + ".restore", null);
			lore.add(ChatColor.RED + "No restore stat.");
		} else {
			ConfigurationSection restore = config.getConfigurationSection(path + ".restore");
			lore.add(ChatColor.GRAY + "Current Value:");
			if (restore.contains("health")) {
				if (restore.getDouble("health") <= 0)
					config.set(path + ".restore.health", null);
				else
					lore.add(ChatColor.GRAY + "* Health: " + ChatColor.GREEN + config.getDouble(path + ".restore.health"));
			}
			if (restore.contains("food")) {
				if (restore.getDouble("food") <= 0)
					config.set(path + ".restore.food", null);
				else
					lore.add(ChatColor.GRAY + "* Food: " + ChatColor.GREEN + config.getDouble(path + ".restore.food"));
			}
			if (restore.contains("saturation")) {
				if (restore.getDouble("saturation") <= 0)
					config.set(path + ".restore.saturation", null);
				else
					lore.add(ChatColor.GRAY + "* Saturation: " + ChatColor.GREEN + config.getDouble(path + ".restore.saturation"));
			}
		}
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click change these values.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getDouble("restore.health"), config.getDouble("restore.food"), config.getDouble("restore.saturation"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		double health = (double) values[0];
		double food = (double) values[1];
		double saturation = (double) values[2];

		if (health != 0) {
			item.addItemTag(new ItemTag("MMOITEMS_RESTORE_HEALTH", health));
			item.insertInLore("restore-health", ItemStat.translate("restore-health").replace("#", new StatFormat("##").format(health)));
		}
		if (food != 0) {
			item.addItemTag(new ItemTag("MMOITEMS_RESTORE_FOOD", food));
			item.insertInLore("restore-food", ItemStat.translate("restore-food").replace("#", new StatFormat("##").format(food)));
		}
		if (saturation != 0) {
			item.addItemTag(new ItemTag("MMOITEMS_RESTORE_SATURATION", saturation));
			item.insertInLore("restore-saturation", ItemStat.translate("restore-saturation").replace("#", new StatFormat("##").format(saturation)));
		}
		return true;
	}
}
