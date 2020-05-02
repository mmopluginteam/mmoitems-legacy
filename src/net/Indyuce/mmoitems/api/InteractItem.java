package net.Indyuce.mmoitems.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;

@SuppressWarnings("deprecation")
public class InteractItem {
	private Player player;
	private EquipmentSlot slot;
	private ItemStack item;

	private static final boolean offhand = MMOItems.plugin.getVersion().isStrictlyHigher(1, 8);

	/*
	 * determines in which hand the player has a specific item, prioritizing the
	 * main hand. it is used to easily replace one of the player's hand items
	 * when he uses tools like flint & steel, shears, bows, etc.
	 */
	public InteractItem(Player player, Material material) {
		this.player = player;
		this.slot = hasItem(player.getItemInHand(), material) ? EquipmentSlot.HAND : offhand && hasItem(player.getInventory().getItemInOffHand(), material) ? EquipmentSlot.OFF_HAND : null;
		if (!hasItem())
			return;

		this.item = slot != EquipmentSlot.HAND ? player.getInventory().getItemInOffHand() : player.getItemInHand();
	}

	/*
	 * works the same but with a material suffit like _HOE which allows to use
	 * that class for any tool made with any ingot/material
	 */
	public InteractItem(Player player, String suffix) {
		this.player = player;
		this.slot = hasItem(player.getItemInHand(), suffix) ? EquipmentSlot.HAND : offhand && hasItem(player.getInventory().getItemInOffHand(), suffix) ? EquipmentSlot.OFF_HAND : null;
		if (!hasItem())
			return;

		this.item = slot != EquipmentSlot.HAND ? player.getInventory().getItemInOffHand() : player.getItemInHand();
	}

	public boolean hasItem() {
		return slot != null;
	}

	public void setItem(ItemStack item) {
		if (slot != EquipmentSlot.HAND)
			player.getInventory().setItemInOffHand(item);
		else
			player.setItemInHand(item);
	}

	public ItemStack getItem() {
		return item;
	}

	private boolean hasItem(ItemStack item, Material material) {
		return item != null && item.getType() == material;
	}

	private boolean hasItem(ItemStack item, String suffix) {
		return item != null && item.getType().name().endsWith(suffix);
	}
}
