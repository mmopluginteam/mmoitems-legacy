package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;

public class Hide_Potion_Effects extends ItemStat {
	public Hide_Potion_Effects() {
		super(new ItemStack(Material.POTION), "Hide Potion Effects", new String[] { "Hides potion effects & 'No Effects'", "from your item lore." }, "hide-potion-effects", new String[] { "all" }, StatType.BOOLEAN, dm(Material.POTION));
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getBoolean("hide-potion-effects"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		if ((boolean) values[0])
			item.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
		return true;
	}
}
