package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;

public class Displayed_Type extends ItemStat {
	public Displayed_Type() {
		super(new ItemStack(Material.SIGN), "Displayed Type", new String[] { "This option will only affect the", "type displayed on the item lore." }, "displayed-type", new String[] { "all" }, StatType.STRING);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getString("displayed-type"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		item.setDisplayedType((String) values[0]);
		return true;
	}
}
