package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Soulbound_Level extends ItemStat {
	public Soulbound_Level() {
		super(new ItemStack(Material.EYE_OF_ENDER), "Soulbinding Level", new String[] { "The soulbound level defines how much", "damage players will take when trying", "to use a soulbound item. It also determines", "how hard it is to break the binding." }, "soulbound-level", new String[] { "consumable" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		int value = (int) MMOUtils.randomValue(config.getString("soulbound-level"));
		item.addItemTag(new ItemTag("MMOITEMS_SOULBOUND_LEVEL", value));
		item.insertInLore("soulbound-level", format(value, "#", MMOUtils.intToRoman(value)));
		return true;
	}
}
