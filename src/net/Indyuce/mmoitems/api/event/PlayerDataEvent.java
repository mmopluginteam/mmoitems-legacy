package net.Indyuce.mmoitems.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.Indyuce.mmoitems.api.PlayerData;

public abstract class PlayerDataEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;
	private PlayerData playerData;

	public PlayerDataEvent(PlayerData playerData) {
		super(playerData.getPlayer());
		this.playerData = playerData;
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		cancelled = value;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
