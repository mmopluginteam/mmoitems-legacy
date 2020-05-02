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

public class Movement_Speed extends ItemStat {
	public Movement_Speed() {
		super(new ItemStack(Material.LEATHER_BOOTS), "Movement Speed", new String[] { "Movement Speed increase walk speed.", "Default MC walk speed: 0.1" }, "movement-speed", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("movement-speed")));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		double value = (double) values[0];

		for (String slot : item.getItemType().getSlots())
			item.addItemAttribute(new Attribute("movementSpeed", value, slot));
		item.addItemTag(new ItemTag("MMOITEMS_MOVEMENT_SPEED", value));
		item.insertInLore("movement-speed", format(value, "#", new StatFormat("####").format(value)));
		return true;
	}

	@Override
	public void applyOntoItem(Type type, Stat stat, double gemValue, double weaponValue, List<Attribute> attributes, List<ItemTag> tags) {
		weaponValue += gemValue;
		for (String slot : type.getSlots())
			attributes.add(new Attribute("movementSpeed", gemValue, slot));
		tags.add(new ItemTag("MMOITEMS_MOVEMENT_SPEED", weaponValue));
	}
}
