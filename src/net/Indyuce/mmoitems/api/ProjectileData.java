package net.Indyuce.mmoitems.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProjectileData {
	private ItemStack sourceItem;
	private PlayerStats playerStats;

	public ProjectileData(ItemStack sourceItem, PlayerStats playerStats) {
		this.playerStats = playerStats;
		this.sourceItem = sourceItem;
	}

	public ItemStack getSourceItem() {
		return sourceItem;
	}

	public PlayerStats getPlayerStats() {
		return playerStats;
	}

	public Player getPlayer() {
		return playerStats.getPlayer();
	}
}
