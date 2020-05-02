package net.Indyuce.mmoitems.comp.rpgplugin;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.version.VersionSound;

public class RPGProfile {
	private String className;
	private double mana, stamina;
	private int level;
	private PlayerData playerData;
	private Player player;

	@Deprecated
	public RPGProfile(Player player) {
		this(PlayerData.get(player));
	}

	/*
	 * used to temporarily store info about the current rpg plugin so it does
	 * not have to be calculated for each item in the inventory. also strongly
	 * reduces map checkups to get the player data
	 */
	public RPGProfile(PlayerData playerData) {
		this.player = playerData.getPlayer();
		this.playerData = playerData;
	}

	public String getClassName() {
		return className;
	}

	public int getLevel() {
		return level;
	}

	public double getMana() {
		return mana;
	}

	public double getStamina() {
		return stamina;
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	public void setClass(String className) {
		this.className = className;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setStamina(double stamina) {
		this.stamina = stamina;
	}

	public void setMana(double mana) {
		this.mana = mana;
	}

	public boolean canUse(ItemStack item, boolean msg) {

		// item permission
		String perm = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_PERMISSION");
		if (!perm.equals("") && !player.hasPermission("mmoitems.bypass.item") && MMOItems.plugin.getConfig().getBoolean("permissions.items")) {

			String[] split = perm.split("\\|");
			for (String s : split)
				if (!player.hasPermission(s)) {
					if (msg) {
						Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(player, "cant-use-item");
						player.playSound(player.getLocation(), VersionSound.ENTITY_VILLAGER_NO.getSound(), 1, 1.5f);
					}
					return false;
				}
		}

		// durability (<0 => unusable)
		int durability = (int) MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_DURABILITY");
		if (durability < 0) {
			if (msg) {
				Message.ZERO_DURABILITY.format(ChatColor.RED).send(player, "cant-use-item");
				player.playSound(player.getLocation(), VersionSound.ENTITY_VILLAGER_NO.getSound(), 1, 1.5f);
			}
			return false;
		}

		// required class
		String requiredClass = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_REQUIRED_CLASS");
		if (!requiredClass.equals("") && !hasRightClass(requiredClass) && !player.hasPermission("mmoitems.bypass.class")) {
			if (msg) {
				Message.WRONG_CLASS.format(ChatColor.RED).send(player, "cant-use-item");
				player.playSound(player.getLocation(), VersionSound.ENTITY_VILLAGER_NO.getSound(), 1, 1.5f);
			}
			return false;
		}

		// required level
		int level = (int) MMOItems.plugin.getStats().getStat(item, Stat.REQUIRED_LEVEL);
		if (MMOItems.getRPG().getLevel(player) < level && !player.hasPermission("mmoitems.bypass.level")) {
			if (msg) {
				Message.NOT_ENOUGH_LEVELS.format(ChatColor.RED).send(player, "cant-use-item");
				player.playSound(player.getLocation(), VersionSound.ENTITY_VILLAGER_NO.getSound(), 1, 1.5f);
			}
			return false;
		}

		return true;
	}

	private boolean hasRightClass(String requiredClass) {
		String name = ChatColor.stripColor(MMOItems.getRPG().getClass(player));
		for (String s : requiredClass.split(Pattern.quote(", ")))
			if (s.equalsIgnoreCase(name))
				return true;
		return false;
	}
}
