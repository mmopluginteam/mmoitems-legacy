package net.Indyuce.mmoitems.api;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;

public enum StaffSpirit {
	NETHER_SPIRIT(ChatColor.GOLD, "Nether Spirit", "Shoots fire beams."),
	VOID_SPIRIT(ChatColor.DARK_GRAY, "Void Spirit", "Shoots shulker missiles."),
	MANA_SPIRIT(ChatColor.BLUE, "Mana Spirit", "Summons mana bolts."),
	LIGHTNING_SPIRIT(ChatColor.WHITE, "Lightning Spirit", "Summons lightning bolts."),
	XRAY_SPIRIT(ChatColor.RED, "X-Ray Spirit", "Fires piercing & powerful X-rays."),
	THUNDER_SPIRIT(ChatColor.YELLOW, "Thunder Spirit", "Fires AoE damaging thunder strikes."),
	SUNFIRE_SPIRIT(ChatColor.RED, "Sunfire Spirit", "Fires AoE damaging fire comets."),
	// CURSED_SPIRIT(ChatColor.DARK_PURPLE, "Cursed Spirit", "Fires a targeted
	// cursed projectile."),
	;

	private ChatColor prefix;
	private String name, lore;

	private StaffSpirit(ChatColor prefix, String name) {
		this(prefix, name, null);
	}

	private StaffSpirit(ChatColor prefix, String name, String lore) {
		this.prefix = prefix;
		this.name = name;
		this.lore = lore;
	}

	public static StaffSpirit get(ItemStack i) {
		try {
			return StaffSpirit.valueOf(MMOItems.plugin.getNMS().getStringTag(i, "MMOITEMS_STAFF_SPIRIT"));
		} catch (Exception e) {
			return null;
		}
	}

	public ChatColor getPrefix() {
		return prefix;
	}

	public String getDefaultName() {
		return name;
	}

	public String getName() {
		return MMOItems.getLanguage().getStaffSpiritName(this);
	}

	public boolean hasLore() {
		return lore != null;
	}

	public String getLore() {
		return lore;
	}
}
