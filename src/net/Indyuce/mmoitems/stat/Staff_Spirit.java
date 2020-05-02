package net.Indyuce.mmoitems.stat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.StaffSpirit;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Staff_Spirit extends ItemStat {
	public Staff_Spirit() {
		super(new ItemStack(Material.INK_SACK, 1, (short) 15), "Staff Spirit", new String[] { "Spirit changes the texture", "of the magic attack.", "&9Tip: /mi list spirit" }, "staff-spirit", new String[] { "staff", "wand" }, StatType.STRING);
	}

	@Override
	public boolean chatListener(Type type, String path, Player p, FileConfiguration config, String msg, Object... info) {
		StaffSpirit ss = null;
		String format = msg.toUpperCase().replace(" ", "_").replace("-", "_");
		try {
			ss = StaffSpirit.valueOf(format);
		} catch (Exception e1) {
			p.sendMessage(MMOItems.getPrefix() + ChatColor.RED + format + " is not a valid staff spirit.");
			p.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "See all Staff Spirits here: /mi list spirit.");
			return false;
		}

		config.set(path + ".staff-spirit", ss.name());
		type.saveConfigFile(config, path);
		new ItemEdition(p, type, path).open();
		p.sendMessage(MMOItems.getPrefix() + "Staff Spirit successfully changed to " + ss.getName() + ".");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		try {
			return apply(item, StaffSpirit.valueOf(config.getString("staff-spirit")));
		} catch (Exception e) {
			item.error("Staff Spirit");
			return false;
		}
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		StaffSpirit staffSpirit = (StaffSpirit) values[0];

		item.addItemTag(new ItemTag("MMOITEMS_STAFF_SPIRIT", staffSpirit.name()));
		item.insertInLore("staff-spirit", staffSpirit.getPrefix() + staffSpirit.getName());
		return true;
	}
}
