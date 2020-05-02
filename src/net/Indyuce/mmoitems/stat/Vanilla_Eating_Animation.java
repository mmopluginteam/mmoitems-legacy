package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Vanilla_Eating_Animation extends ItemStat {
	public Vanilla_Eating_Animation() {
		super(new ItemStack(Material.COOKED_BEEF), "Vanilla Eating Animation", new String[] { "When enabled, players have to wait", "for the vanilla eating animation", "in order to eat the consumable." }, "vanilla-eating", new String[] { "consumable" }, StatType.BOOLEAN);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getBoolean("vanilla-eating"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		if ((boolean) values[0])
			item.addItemTag(new ItemTag("MMOITEMS_VANILLA_EATING", (boolean) values[0]));
		return true;
	}
}
