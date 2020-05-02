package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Unbreakable extends ItemStat {
	public Unbreakable() {
		super(new ItemStack(Material.ANVIL), "Unbreakable", new String[] { "Infinite durability if set to true." }, "unbreakable", new String[] { "all" }, StatType.BOOLEAN);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getBoolean("unbreakable"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		if ((boolean) values[0]) {
			item.addItemFlag(ItemFlag.HIDE_UNBREAKABLE);
			item.addItemTag(new ItemTag("Unbreakable", true));
		}
		return true;
	}
}
