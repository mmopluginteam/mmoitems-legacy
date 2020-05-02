package net.Indyuce.mmoitems.api;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;

public class ItemTier {
	private ConfigurationSection section;
	private boolean wasFound = true;

	public ItemTier(String id) {
		if (!MMOItems.getLanguage().getTiers().contains(id)) {
			wasFound = false;
			return;
		}

		this.section = MMOItems.getLanguage().getTiers().getConfigurationSection(id);
	}

	public boolean exists() {
		return wasFound;
	}

	public String getName() {
		return ChatColor.translateAlternateColorCodes('&', section.getString("name"));
	}

	// reads a random item in the drop table
	public List<ItemStack> generateDeconstructedItem() {
		return new DropTable(section.getConfigurationSection("deconstruct-item")).read(false);
	}
}
