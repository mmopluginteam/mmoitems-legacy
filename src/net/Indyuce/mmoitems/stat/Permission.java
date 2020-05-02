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

public class Permission extends ItemStat {
	public Permission() {
		super(new ItemStack(Material.SIGN), "Permission", new String[] { "The permission needed to use this item." }, "permission", new String[] { "all" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.PERMISSION).enable("Write in the chat the permission you want your item to require.");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			if (config.getConfigurationSection(path).getKeys(false).contains("permission")) {
				List<String> requiredPerms = config.getStringList(path + ".permission");
				if (requiredPerms.size() < 1)
					return true;
				String last = requiredPerms.get(requiredPerms.size() - 1);
				requiredPerms.remove(last);
				config.set(path + ".permission", requiredPerms.size() == 0 ? null : requiredPerms);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed " + last + ".");
			}
		}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		if (msg.contains("|")) {
			player.sendMessage(MMOItems.getPrefix() + "Your perm node must not contain any | symbol.");
			return false;
		}

		List<String> lore = config.getConfigurationSection(path).getKeys(false).contains("permission") ? config.getStringList(path + ".permission") : new ArrayList<String>();
		lore.add(msg);
		config.set(path + ".permission", lore);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "Permission successfully added.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		// reset if no command
		if (!config.getConfigurationSection(path).contains("permission"))
			lore.add(ChatColor.RED + "No permission.");
		else
			for (String s : config.getStringList(path + ".permission"))
				lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + s);
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to add a required permission.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the last permission.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getStringList("permission"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean apply(MMOItem item, Object... values) {
		item.addItemTag(new ItemTag("MMOITEMS_PERMISSION", String.join("|", (List<String>) values[0])));
		return true;
	}
}
