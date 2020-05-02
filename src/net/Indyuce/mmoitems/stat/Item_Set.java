package net.Indyuce.mmoitems.stat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemSet;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Item_Set extends ItemStat {
	public Item_Set() {
		super(new ItemStack(Material.LEATHER_CHESTPLATE), "Item Set", new String[] { "Item sets can give to the player extra", "bonuses that depend on how many items", "from the same set your wear." }, "set", new String[] { "!gem_stone", "!accessory", "!consumable", "!material", "!miscellaneous", "all" }, StatType.STRING);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getString("set"));
	}

	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path) {
		if (event.getAction() == InventoryAction.PICKUP_ALL) {
			new StatEdition(inv, Stat.SET).enable("Write in the chat the item set ID.");
			player.sendMessage("");
			for (ItemSet set : MMOItems.getLanguage().getItemSets())
				player.sendMessage(ChatColor.GRAY + "* " + ChatColor.GREEN + set.getID() + ChatColor.GRAY + " (" + set.getName() + ChatColor.GRAY + ")");
			return true;
		}
		
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			config.set(path + "." + path, null);
			type.saveConfigFile(config, path);
			new ItemEdition(player, type, path).open();
			player.sendMessage(MMOItems.getPrefix() + "Successfully removed Item Set.");
			return true;
		}
		new StatEdition(inv, Stat.SET).enable("Write in the chat the item set name.");
	
		return true;
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		String path = (String) values[0];

		// do not send an error otherwise previously
		// generated items will break
		ItemSet set = MMOItems.getLanguage().getItemSet(path);
		if (set == null) {
			item.error("Item Set", "Couldn't find the item set named " + path + ".");
			return false;
		}

		item.addItemTag(new ItemTag("MMOITEMS_ITEM_SET", path));
		item.insertInLore("set", set.getLoreTag());
		return true;
	}

	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		String format = msg.toUpperCase().replace(" ", "_").replace("-", "_");

		ItemSet set = MMOItems.getLanguage().getItemSet(format);
		if (set == null) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Couldn't find the set named " + format + ".");
			return false;
		}

		config.set(path + ".set", format);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "Set successfully changed to " + set.getName() + ChatColor.GRAY + ".");
		return true;
	}
}
