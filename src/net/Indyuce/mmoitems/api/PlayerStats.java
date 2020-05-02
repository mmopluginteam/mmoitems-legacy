package net.Indyuce.mmoitems.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;

public class PlayerStats {
	private PlayerData playerData;
	private final Player player;

	public Map<String, Double> stats = new HashMap<>();

	/*
	 * This is not a player data class. This class is used to temporarily save
	 * player stats in case of runnable-skills or item effects. This is to
	 * prevent player stats from changing when skills/effects are not INSTANTLY
	 * cast.
	 */
	public PlayerStats(PlayerData playerData, Stat... targetStats) {
		this.playerData = playerData;
		this.player = playerData.getPlayer();

		for (Stat stat : targetStats)
			stats.put(stat.name(), playerData.hasSetBonuses() ? playerData.getSetBonuses().getStat(stat) : 0);

		for (ItemStack item : playerData.getMMOItems())
			MMOItems.plugin.getNMS().requestDoubleTags(item, stats);
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	public double getStat(Stat stat) {
		return stats.get(stat.name());
	}

	public boolean hasStat(Stat stat) {
		return stats.containsKey(stat.name());
	}

	/*
	 * this field is made final so even when the player logs out, the ability
	 * can still be cast without any additional errors. this allows not to add a
	 * safe check in every ability loop.
	 */
	public Player getPlayer() {
		return player;
	}

	public void setStat(Stat stat, double value) {
		stats.put(stat.name(), value);
	}
}
