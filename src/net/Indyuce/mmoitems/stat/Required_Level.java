package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Required_Level extends ItemStat {
	public Required_Level() {
		super(new ItemStack(Material.EXP_BOTTLE), "Required Level", new String[] { "The level your item needs", "in order to be used." }, "required-level", new String[] { "all" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("required-level")));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		double n = (double) values[0];

		item.addItemTag(new ItemTag("MMOITEMS_REQUIRED_LEVEL", (double) n));
		item.insertInLore("required-level", format(n, "#", "" + (int) n));
		return true;
	}
}
