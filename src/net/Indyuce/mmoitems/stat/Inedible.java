package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Inedible extends ItemStat {
	public Inedible() {
		super(new ItemStack(Material.POISONOUS_POTATO), "Inedible", new String[] { "Players won't be able to", "right-click this consumable." }, "inedible", new String[] { "consumable" }, StatType.BOOLEAN);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getBoolean("inedible"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		if ((boolean) values[0])
			item.addItemTag(new ItemTag("MMOITEMS_INEDIBLE", (boolean) values[0]));
		return true;
	}
}
