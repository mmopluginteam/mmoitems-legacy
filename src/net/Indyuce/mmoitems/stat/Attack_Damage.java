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

public class Attack_Damage extends ItemStat {
	public Attack_Damage() {
		super(new ItemStack(Material.IRON_SWORD), "Attack Damage", new String[] { "The amount of damage", "your weapon deals." }, "attack-damage", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "gem_stone" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("attack-damage")));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		double value = (double) values[0];

		if (item.getItemType().isMeleeWeapon())
			for (String slot : item.getItemType().getSlots())
				item.addItemAttribute(new Attribute("attackDamage", value - 1, slot));
		item.addItemTag(new ItemTag("MMOITEMS_ATTACK_DAMAGE", value));
		item.insertInLore("attack-damage", format(value, "#", new StatFormat("##").format(value)));
		return true;
	}

	@Override
	public void applyOntoItem(Type type, Stat stat, double gemValue, double weaponValue, List<Attribute> attributes, List<ItemTag> tags) {
		weaponValue += gemValue;
		if (type.isMeleeWeapon())
			for (String slot : type.getSlots())
				attributes.add(new Attribute("attackDamage", gemValue, slot));
		tags.add(new ItemTag("MMOITEMS_ATTACK_DAMAGE", weaponValue));
	}
}
