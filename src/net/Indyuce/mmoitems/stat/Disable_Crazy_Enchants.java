package net.Indyuce.mmoitems.stat;

import org.bukkit.Bukkit;
import org.bukkit.Material;

public class Disable_Crazy_Enchants extends Disable_Stat {
	public Disable_Crazy_Enchants() {
		super(Material.BARRIER, "attack-passive", "Disable Attack Passive", new String[] { "piercing", "slashing", "blunt" }, "Disables the blunt/slashing/piercing", "passive effects on attacks.");

		if (Bukkit.getPluginManager().getPlugin("CrazyEnchantments") == null)
			disable();
	}
}
