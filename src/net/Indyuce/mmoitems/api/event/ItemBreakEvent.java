package net.Indyuce.mmoitems.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class ItemBreakEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	private ItemStack item;
	private boolean itemBreak;

	public ItemBreakEvent(Player player, ItemStack item, boolean itemBreak) {
		super(player);
		this.item = item;
		this.itemBreak = itemBreak;
	}

	public ItemStack getItem() {
		return item;
	}

	/*
	 * returns if the item really broke or if it just became unusable till a
	 * player repairs it
	 */
	public boolean doesItemBreak() {
		return itemBreak;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
