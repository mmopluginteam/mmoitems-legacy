package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.DurabilityItem;
import net.Indyuce.mmoitems.api.DurabilityState;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Max_Custom_Durability extends ItemStat {
	public Max_Custom_Durability() {
		super(new ItemStack(Material.SHEARS, 1, (short) 10), "Max Custom Durability", new String[] { "The amount of uses before your", "item becomes unusable/breaks." }, "max-durability", new String[] { "all" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("max-durability")));
	}

	// initializes the custom
	// durability on an item
	@Override
	public boolean apply(MMOItem item, Object... values) {
		double max = (double) values[0];
		try {
			DurabilityState state = DurabilityItem.getExpectedDurabilityState((int) max, (int) max);
			item.addItemTag(new ItemTag("MMOITEMS_MAX_DURABILITY", max), new ItemTag("MMOITEMS_DURABILITY", max), new ItemTag("MMOITEMS_DURABILITY_STATE", state.getID()));
			item.insertInLore("durability-state", state.getDisplay());
		} catch (Exception e) {
			item.error("Custom Durability", "Could not determine the initial durability state. Make sure you have a durability state that has its max use ratio at 100%.");
			return true;
		}
		return true;
	}
}
