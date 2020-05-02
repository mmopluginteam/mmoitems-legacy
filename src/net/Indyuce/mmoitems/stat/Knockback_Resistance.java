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

public class Knockback_Resistance extends ItemStat {
	public Knockback_Resistance() {
		super(new ItemStack(Material.CHAINMAIL_CHESTPLATE), "Knockback Resistance", new String[] { "The chance of your item to block the", "knockback from explosions, creepers...", "1.0 corresponds to 100%, 0.7 to 70%..." }, "knockback-resistance", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("knockback-resistance")));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		double value = (double) values[0];

		for (String slot : item.getItemType().getSlots())
			item.addItemAttribute(new Attribute("knockbackResistance", value, slot));
		item.addItemTag(new ItemTag("MMOITEMS_KNOCKBACK_RESISTANCE", value));
		item.insertInLore("knockback-resistance", format(value, "#", new StatFormat("####").format(value)));
		return true;
	}

	@Override
	public void applyOntoItem(Type type, Stat stat, double gemValue, double weaponValue, List<Attribute> attributes, List<ItemTag> tags) {
		weaponValue += gemValue;
		for (String slot : type.getSlots())
			attributes.add(new Attribute("knockbackResistance", gemValue, slot));
		tags.add(new ItemTag("MMOITEMS_KNOCKBACK_RESISTANCE", weaponValue));
	}
}
