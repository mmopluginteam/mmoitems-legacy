package net.Indyuce.mmoitems.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.PlayerData;

public class AbilityUseEvent extends PlayerDataEvent {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;
	private Ability ability;

	// on-hit ability target
	private LivingEntity target;

	public AbilityUseEvent(PlayerData playerData, Ability ability) {
		this(playerData, ability, null);
	}

	public AbilityUseEvent(PlayerData playerData, Ability ability, LivingEntity target) {
		super(playerData);
		this.ability = ability;
		this.target = target;
	}

	public Ability getAbility() {
		return ability;
	}

	public LivingEntity getTarget() {
		return target;
	}

	public void setCancelled(boolean bool) {
		cancelled = bool;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
