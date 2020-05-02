package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Consume_Sound extends ItemStat {
	public Consume_Sound() {
		super(new ItemStack(Material.NOTE_BLOCK), "Consume Sound", new String[] { "The sound played when", "eating the consumable." }, "consume-sound", new String[] { "consumable" }, StatType.STRING);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getString("consume-sound").toUpperCase().replace("-", "_").replace(" ", "_"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		String value = (String) values[0];

		item.addItemTag(new ItemTag("MMOITEMS_CONSUME_SOUND", value));
		return true;
	}
}
