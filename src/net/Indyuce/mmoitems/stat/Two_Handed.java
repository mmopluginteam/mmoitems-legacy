package net.Indyuce.mmoitems.stat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat;

public class Two_Handed extends ItemStat {
	public Two_Handed() {
		super(new ItemStack(Material.IRON_INGOT), "Two Handed", new String[] { "If set to true, a player will be", "significantly slower if holding two", "items, one being Two Handed." }, "two-handed", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool" }, StatType.BOOLEAN);

		if (MMOItems.plugin.getVersion().isBelowOrEqual(1, 8))
			disable();
	}
}
