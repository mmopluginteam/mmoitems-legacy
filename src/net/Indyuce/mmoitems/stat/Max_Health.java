package net.Indyuce.mmoitems.stat;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.StatFormat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.version.nms.Attribute;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Max_Health extends ItemStat {
	public Max_Health() {
		super(new ItemStack(Material.GOLDEN_APPLE), "Max Health", new String[] { "The amount of health your", "item gives to the holder." }, "max-health", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("max-health")));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		double value = (double) values[0];

		for (String slot : item.getItemType().getSlots())
			item.addItemAttribute(new Attribute("maxHealth", value, slot));
		item.addItemTag(new ItemTag("MMOITEMS_MAX_HEALTH", value));
		item.insertInLore("max-health", format(value, "#", new StatFormat("##").format(value)));
		return true;
	}

	@Override
	public void applyOntoItem(Type type, Stat stat, double gemValue, double weaponValue, List<Attribute> attributes, List<ItemTag> tags) {
		weaponValue += gemValue;
		for (String slot : type.getSlots())
			attributes.add(new Attribute("maxHealth", gemValue, slot));
		tags.add(new ItemTag("MMOITEMS_MAX_HEALTH", weaponValue));
	}
}
