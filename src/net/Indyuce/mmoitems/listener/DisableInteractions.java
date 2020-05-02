package net.Indyuce.mmoitems.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;

public class DisableInteractions implements Listener {
	// anvils
	@EventHandler
	public void a(InventoryClickEvent event) {
		Inventory inv = event.getClickedInventory();
		if (inv == null)
			return;

		if (inv.getType() != InventoryType.ANVIL || event.getSlot() != 2)
			return;

		ItemStack item = event.getCurrentItem();
		if (Type.get(item) != null)
			if (MMOItems.plugin.getConfig().getBoolean("disable-interactions.repair") || MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_DISABLE_REPAIRING"))
				event.setCancelled(true);
	}

	// enchanting tables
	@EventHandler
	public void b(EnchantItemEvent event) {
		ItemStack item = event.getItem();
		if (Type.get(item) != null)
			if (MMOItems.plugin.getConfig().getBoolean("disable-interactions.enchant") || MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_DISABLE_ENCHANTING"))
				event.setCancelled(true);
	}

	// smelting
	@EventHandler
	public void c(FurnaceSmeltEvent event) {
		ItemStack item = event.getSource();
		if (Type.get(item) != null)
			if (MMOItems.plugin.getConfig().getBoolean("disable-interactions.smelt") || MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_DISABLE_SMELTING"))
				event.setCancelled(true);
	}

	// interaction
	@EventHandler
	public void d(PlayerInteractEvent event) {
		if (!event.hasItem())
			return;

		ItemStack item = event.getItem();
		if (Type.get(item) != null)
			if (MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_DISABLE_INTERACTION"))
				event.setCancelled(true);
	}

	// workbench
	@EventHandler
	public void event(CraftItemEvent event) {
		boolean disableCrafting = MMOItems.plugin.getConfig().getBoolean("disable-interactions.craft");
		for (ItemStack item : event.getInventory().getMatrix())
			if (item != null)
				if (Type.get(item) != null)
					if (disableCrafting || MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_DISABLE_CRAFTING"))
						event.setCancelled(true);
	}

	// preventing the player from shooting the arrow
	@EventHandler
	public void f(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		int arrowSlot = firstArrow(player);
		if (arrowSlot < 0)
			return;

		ItemStack arrow = player.getInventory().getItem(arrowSlot);
		if (arrow == null)
			return;

		if (Type.get(arrow) != null)
			if (MMOItems.plugin.getConfig().getBoolean("disable-interactions.arrow-shooting") || MMOItems.plugin.getNMS().getBooleanTag(arrow, "MMOITEMS_DISABLE_ARROW_SHOOTING"))
				event.setCancelled(true);
	}

	private int firstArrow(Player player) {

		// check offhand first
		if (MMOItems.plugin.getVersion().isStrictlyHigher(1, 8))
			if (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType().name().contains("ARROW"))
				return 40;

		// check for every slot
		ItemStack[] storage = player.getInventory().getStorageContents();
		for (int j = 0; j < storage.length; j++) {
			ItemStack item = storage[j];
			if (item != null && item.getType().name().contains("ARROW"))
				return j;
		}
		return -1;
	}
}