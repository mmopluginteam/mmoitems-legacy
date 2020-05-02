package net.Indyuce.mmoitems.stat;

import java.util.ArrayList;
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
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Required_Class extends ItemStat {
	public Required_Class() {
		super(new ItemStack(Material.BOOK_AND_QUILL), "Required Class", new String[] { "The class you need to", "profress to use your item." }, "required-class", new String[] { "all" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.REQUIRED_CLASS).enable("Write in the chat the class you want your item to support.");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			if (config.getConfigurationSection(path).getKeys(false).contains("required-class")) {
				List<String> supportedClasses = config.getStringList(path + ".required-class");
				if (supportedClasses.size() < 1)
					return true;

				String last = supportedClasses.get(supportedClasses.size() - 1);
				supportedClasses.remove(last);
				config.set(path + ".required-class", supportedClasses.size() == 0 ? null : supportedClasses);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed " + last + ".");
			}
		}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		List<String> lore = (config.getConfigurationSection(path).getKeys(false).contains("required-class") ? config.getStringList(path + ".required-class") : new ArrayList<>());
		lore.add(msg);
		config.set(path + ".required-class", lore);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "Required Class successfully added.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		if (!config.getConfigurationSection(path).contains("required-class"))
			lore.add(ChatColor.RED + "No required class.");
		else
			for (String s : config.getStringList(path + ".required-class"))
				lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + s);
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to add a class.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the last class.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getStringList("required-class"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean apply(MMOItem item, Object... values) {
		String joined = String.join(", ", (List<String>) values[0]);

		item.insertInLore("required-class", translate().replace("#", joined));
		item.addItemTag(new ItemTag("MMOITEMS_REQUIRED_CLASS", joined));
		return true;
	}
}
