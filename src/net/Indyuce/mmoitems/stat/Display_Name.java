package net.Indyuce.mmoitems.stat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;

public class Display_Name extends ItemStat {
	public Display_Name() {
		super(new ItemStack(Material.SIGN), "Display Name", new String[] { "The item display name." }, "name", new String[] { "all" }, StatType.STRING);
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getString("name"));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		String name = (String) values[0];

		// name placeholders
		String[] split = name.split("\\<");
		if (split.length > 1)
			// starting at 0 is pointless
			for (int j = 1; j < split.length; j++) {
				String jstr = split[j];
				if (!jstr.contains(">"))
					continue;

				String phName = jstr.split("\\>")[0];
				String placeholder = MMOItems.getLanguage().getNamePlaceholder(phName);
				if (placeholder != null)
					name = name.replace("<" + phName + ">", placeholder);
			}

		item.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		return true;
	}
}
