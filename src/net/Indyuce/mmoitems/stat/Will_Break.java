package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Will_Break extends ItemStat {
	public Will_Break() {
		super(new ItemStack(Material.SHEARS, 1, (short) 237), "Will Break?", new String[] { "If set to true, the item will break", "once it reaches 0 durability.", "&c&oOnly works with custom durability." }, "will-break", new String[] { "all" }, StatType.BOOLEAN);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getBoolean("will-break"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		if ((boolean) values[0])
			item.addItemTag(new ItemTag("MMOITEMS_WILL_BREAK", true));
		return true;
	}
}