package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.DMaterial;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Disable_Stat extends ItemStat {
	private String tag;

	public Disable_Stat(Material material, String path, String name, String... lore) {
		super(new ItemStack(material), name, lore, "disable-" + path, new String[] { "all" }, StatType.BOOLEAN);
		this.tag = path.toUpperCase().replace("-", "_");
	}

	public Disable_Stat(Material material, String path, String name, DMaterial[] materials, String... lore) {
		super(new ItemStack(material), name, lore, "disable-" + path, new String[] { "all" }, StatType.BOOLEAN, materials);
		this.tag = path.toUpperCase().replace("-", "_");
	}

	public Disable_Stat(Material material, String path, String name, String[] types, String... lore) {
		super(new ItemStack(material), name, lore, "disable-" + path, types, StatType.BOOLEAN);
		this.tag = path.toUpperCase().replace("-", "_");
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getBoolean(getPath()));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		boolean value = (boolean) values[0];

		item.addItemTag(new ItemTag("MMOITEMS_DISABLE_" + tag, value));
		return true;
	}
}
