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

public class Attack_Speed extends ItemStat {
	public Attack_Speed() {
		super(new ItemStack(Material.INK_SACK, 1, (short) 7), "Attack Speed", new String[] { "The speed at which your weapon strikes.", "In attacks/sec." }, "attack-speed", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "gem_stone" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("attack-speed")));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		double value = (double) values[0];

		if (item.getItemType().isMeleeWeapon())
			for (String slot : item.getItemType().getSlots())
				item.addItemAttribute(new Attribute("attackSpeed", value - 4, slot));
		item.addItemTag(new ItemTag("MMOITEMS_ATTACK_SPEED", value));
		item.insertInLore("attack-speed", format(value, "#", new StatFormat("##").format(value)));
		return true;
	}

	@Override
	public void applyOntoItem(Type type, Stat stat, double gemValue, double weaponValue, List<Attribute> attributes, List<ItemTag> tags) {
		weaponValue += gemValue;
		if (type.isMeleeWeapon())
			for (String slot : type.getSlots())
				attributes.add(new Attribute("attackSpeed", gemValue, slot));
		tags.add(new ItemTag("MMOITEMS_ATTACK_SPEED", weaponValue));
	}
}
