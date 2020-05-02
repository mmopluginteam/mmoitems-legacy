package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;

public class Hide_Enchants extends ItemStat {
	public Hide_Enchants() {
		super(new ItemStack(Material.BOOK), "Hide Enchantments", new String[] { "Enable to completely hide your item", "enchants. You can still see the glowing effect." }, "hide-enchants", new String[] { "all", "!gem_stone" }, StatType.BOOLEAN);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getBoolean("hide-enchants"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		if ((boolean) values[0])
			item.addItemFlag(ItemFlag.HIDE_ENCHANTS);
		return true;
	}
}
