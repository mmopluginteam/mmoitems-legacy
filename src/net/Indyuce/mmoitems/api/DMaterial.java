package net.Indyuce.mmoitems.api;

import org.bukkit.Material;

public class DMaterial {
	private Material material;
	private short durability;

	public DMaterial(Material material, int durability) {
		this.material = material;
		this.durability = (short) durability;
	}

	public Material getType() {
		return material;
	}

	public short getDurability() {
		return durability;
	}
}
