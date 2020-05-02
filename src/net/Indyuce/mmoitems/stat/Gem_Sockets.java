package net.Indyuce.mmoitems.stat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;

public class Gem_Sockets extends ItemStat {
	public Gem_Sockets() {
		super(new ItemStack(Material.EMERALD), "Gem Sockets", new String[] { "The amount of gem", "sockets your weapon has." }, "gem-sockets", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor" }, StatType.DOUBLE);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, MMOUtils.randomValue(config.getString("gem-sockets")));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		int n = (int) (double) values[0];

		List<String> loreGemSockets = new ArrayList<String>();
		for (int j = 0; j < n; j++)
			loreGemSockets.add(ItemStat.translate("empty-gem-socket"));
		item.insertInLore("gem-stones", loreGemSockets);
		return true;
	}
}
