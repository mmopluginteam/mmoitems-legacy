package net.Indyuce.mmoitems.stat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Item_Tier extends ItemStat {
	public Item_Tier() {
		super(new ItemStack(Material.DIAMOND), "Item Tier", new String[] { "The tier defines how rare your item is", "and what item is dropped when your", "item is deconstructed.", "&9Tiers can be configured in the tiers.yml file" }, "tier", new String[] { "all" }, StatType.STRING);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getString("tier"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		String path = (String) values[0];

		// do not send an error otherwise previously
		// generated items will break
		ItemTier tier = new ItemTier(path);
		if (!tier.exists())
			return true;
		
		item.addItemTag(new ItemTag("MMOITEMS_TIER", path));
		item.insertInLore("tier", translate().replace("#", tier.getName()));
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		String format = msg.toUpperCase().replace(" ", "_").replace("-", "_");

		ItemTier tier = new ItemTier(format);
		if (!tier.exists()) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Couldn't find the tier called " + format + ".");
			return false;
		}

		config.set(path + ".tier", format);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "Tier successfully changed to " + tier.getName() + ChatColor.GRAY + ".");
		return true;
	}
}
