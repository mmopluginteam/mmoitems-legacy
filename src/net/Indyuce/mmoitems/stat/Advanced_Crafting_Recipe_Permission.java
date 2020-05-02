package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;

public class Advanced_Crafting_Recipe_Permission extends ItemStat {
	public Advanced_Crafting_Recipe_Permission() {
		super(new ItemStack(Material.SIGN), "Advanced Recipe Permission", new String[] { "The permission needed to craft this item.", "Changing this value requires &o/mi reload adv-recipes&7." }, "advanced-craft-permission", new String[] { "all" }, StatType.STRING);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item);
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		return true;
	}
}