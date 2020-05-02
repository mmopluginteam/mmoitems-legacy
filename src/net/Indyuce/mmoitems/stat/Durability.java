package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;

public class Durability extends ItemStat {
	public Durability() {
		super(new ItemStack(Material.FISHING_ROD, 1, (short) 35), "Durability/ID", new String[] { "The durability of your item." }, "durability", new String[] { "all" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("durability")));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		short value = (short) (double) values[0];

		try {
			item.setDurability(value);
			return true;
		} catch (Exception e) {
			item.error("Durability");
			return false;
		}
	}
}
