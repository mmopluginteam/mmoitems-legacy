package net.Indyuce.mmoitems.stat;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.StatFormat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.version.nms.Attribute;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Armor_Toughness extends ItemStat {
	public Armor_Toughness() {
		super(new ItemStack(Material.DIAMOND_CHESTPLATE), "Armor Toughness", new String[] { "Armor toughness reduces damage taken." }, "armor-toughness", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor", "gem_stone" }, StatType.DOUBLE);

		if (MMOItems.plugin.getVersion().isBelowOrEqual(1, 8))
			disable();
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("armor-toughness")));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		double value = (double) values[0];

		for (String slot : item.getItemType().getSlots())
			item.addItemAttribute(new Attribute("armorToughness", value, slot));
		item.addItemTag(new ItemTag("MMOITEMS_ARMOR_TOUGHNESS", value));
		item.insertInLore("armor-toughness", format(value, "#", new StatFormat("##").format(value)));
		return true;
	}

	@Override
	public void applyOntoItem(Type type, Stat stat, double gemValue, double weaponValue, List<Attribute> attributes, List<ItemTag> tags) {
		weaponValue += gemValue;
		for (String slot : type.getSlots())
			attributes.add(new Attribute("armorToughness", gemValue, slot));
		tags.add(new ItemTag("MMOITEMS_ARMOR_TOUGHNESS", weaponValue));
	}
}
