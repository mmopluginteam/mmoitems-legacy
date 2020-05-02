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

public class Lore extends ItemStat {
	public Lore() {
		super(new ItemStack(Material.BOOK_AND_QUILL), "Lore", new String[] { "The item lore." }, "lore", new String[] { "all" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.LORE).enable("Write in the chat the lore line you want to add.");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			if (config.getConfigurationSection(path).contains("lore")) {
				List<String> lore = config.getStringList(path + ".lore");
				if (lore.size() < 1)
					return true;
				String last = lore.get(lore.size() - 1);
				lore.remove(last);
				config.set(path + ".lore", lore);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed '" + ChatColor.translateAlternateColorCodes('&', last) + ChatColor.GRAY + "'.");
			}
		}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		List<String> lore = config.getConfigurationSection(path).getKeys(false).contains("lore") ? config.getStringList(path + ".lore") : new ArrayList<String>();
		lore.add(msg);
		config.set(path + ".lore", lore);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "Lore successfully added.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		if (!config.getConfigurationSection(path).contains("lore"))
			lore.add(ChatColor.RED + "No lore.");
		else if (config.getStringList(path + ".lore").isEmpty())
			lore.add(ChatColor.RED + "No lore.");
		else
			for (String s1 : config.getStringList(path + ".lore"))
				lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + s1);
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to add a line.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the last line.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getStringList("lore"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean apply(MMOItem item, Object... values) {
		List<String> lore = (List<String>) values[0];

		List<String> lore1 = new ArrayList<String>();
		for (String s : lore)
			lore1.add(ChatColor.translateAlternateColorCodes('&', s));
		item.insertInLore("lore", lore1);
		return true;
	}
}
