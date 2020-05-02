package net.Indyuce.mmoitems.comp.placeholder;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.StatFormat;

public class MMOItemsPlaceholders extends PlaceholderExpansion {
	private DecimalFormat oneDigit = new DecimalFormat("0.#");

	@Override
	public String getAuthor() {
		return "Indyuce";
	}

	@Override
	public String getIdentifier() {
		return "mmoitems";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@SuppressWarnings("deprecation")
	public String onPlaceholderRequest(Player player, String identifier) {
		if (identifier.startsWith("stat_"))
			return new StatFormat("##").format(PlayerData.get(player).getStat(identifier.substring(5)));

		if (identifier.equals("durability"))
			return "" + (int) MMOItems.plugin.getNMS().getDoubleTag(player.getItemInHand(), "MMOITEMS_DURABILITY");

		if (identifier.equals("durability_max"))
			return "" + (int) MMOItems.plugin.getNMS().getDoubleTag(player.getItemInHand(), "MMOITEMS_MAX_DURABILITY");

		if (identifier.equals("durability_ratio")) {
			ItemStack item = player.getItemInHand();
			double durability = MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_DURABILITY");
			double maxDurability = MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_MAX_DURABILITY");
			return oneDigit.format(durability / maxDurability * 100);
		}

		if (identifier.equals("durability_bar_square"))
			return getCurrentDurabilityBar(player.getItemInHand(), SpecialChar.square, 10);

		if (identifier.equals("durability_bar_diamond"))
			return getCurrentDurabilityBar(player.getItemInHand(), SpecialChar.diamond, 15);

		if (identifier.equals("durability_bar_thin"))
			return getCurrentDurabilityBar(player.getItemInHand(), "|", 20);
		return null;
	}

	private String getCurrentDurabilityBar(ItemStack item, String barChar, int length) {
		double durability = MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_DURABILITY");
		double maxDurability = MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_MAX_DURABILITY");
		long r = Math.round(durability / maxDurability * length);
		String bar = "" + ChatColor.GREEN;
		for (int j = 0; j < length; j++)
			bar += (j == r ? ChatColor.WHITE : "") + barChar;
		return bar;
	}
}
