package net.Indyuce.mmoitems.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;

public class ItemLoseDurabilityEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;
	private ItemStack item;
	private int oldDurability, newDurability, durabilityLoss, maxDurability = 0;

	public ItemLoseDurabilityEvent(Player player, ItemStack item, int old, int loss) {
		super(player);
		this.item = item;
		this.oldDurability = old;
		this.durabilityLoss = loss;
		this.newDurability = old - loss;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean bool) {
		cancelled = bool;
	}

	public ItemStack getItem() {
		return item;
	}

	public int getNewDurability() {
		return newDurability;
	}

	public int getOldDurability() {
		return oldDurability;
	}

	public int getDurabilityLoss() {
		return durabilityLoss;
	}

	public int getMaxDurability() {
		return maxDurability == 0 ? maxDurability = (int) MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_MAX_DURABILITY") : maxDurability;
	}

	public boolean isItemBroken() {
		return newDurability < 0;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
