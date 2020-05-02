package net.Indyuce.mmoitems.gui;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.AdvancedRecipe;
import net.Indyuce.mmoitems.api.CustomItem;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.version.VersionSound;

public class AdvancedWorkbench extends PluginInventory {
	private List<InventoryAction> allowedCraftingClicks = Arrays.asList(new InventoryAction[] { InventoryAction.PICKUP_ALL, InventoryAction.MOVE_TO_OTHER_INVENTORY });

	public AdvancedWorkbench(Player player) {
		super(player);
	}

	@Override
	public Inventory getInventory() {
		new BukkitRunnable() {
			public void run() {
				Inventory inv = player.getOpenInventory().getTopInventory();
				if (!inv.getTitle().equals(Message.ADVANCED_WORKBENCH.formatRaw(ChatColor.UNDERLINE))) {
					cancel();
					return;
				}

				AdvancedRecipe data = MMOItems.plugin.getRecipes().getCurrentRecipe(player, inv);
				if (data != null) {
					inv.setItem(16, data.getPreviewItem());
					return;
				}
				inv.setItem(16, null);
			}
		}.runTaskTimer(MMOItems.plugin, 0, 10);

		Inventory inv = (Inventory) Bukkit.createInventory(this, 27, Message.ADVANCED_WORKBENCH.formatRaw(ChatColor.UNDERLINE));

		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(ChatColor.GREEN + "");
		glass.setItemMeta(glassMeta);

		for (int j : new int[] { 0, 1, 2, 6, 7, 8, 9, 10, 11, 15, 17, 18, 19, 20, 24, 25, 26 })
			inv.setItem(j, glass);

		// advanced recipes list
		if (MMOItems.plugin.getConfig().getBoolean("advanced-workbench.recipe-list"))
			inv.setItem(10, CustomItem.RECIPE_LIST.getItem());

		return inv;
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		Inventory inv = event.getInventory();

		// prevent dupe bug
		if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
			event.setCancelled(true);
			return;
		}

		if (MMOUtils.isPluginItem(item, false))
			if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "")) {
				event.setCancelled(true);
				return;
			}

		/*
		 * click to open the advanced recipe list, it's not compulsory to check
		 * the current item display name, since the item is always on the same
		 * slot, simply check for the clicked slot
		 */
		if (event.getRawSlot() == 10) {
			event.setCancelled(true);
			for (int j : MMOItems.plugin.getRecipes().recipeSlots)
				if (inv.getItem(j) != null) {
					Message.EMPTY_WORKBENCH_FIRST.format(ChatColor.YELLOW).send(player);
					return;
				}
			new AdvancedRecipeTypeList(player, 1).open();
			return;
		}

		// click to craft
		if (event.getRawSlot() == 16) {

			if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
				event.setCancelled(true);
				return;
			}
			
			// check for recipe
			AdvancedRecipe data = MMOItems.plugin.getRecipes().getCurrentRecipe(player, event.getInventory());
			if (data == null) {
				event.setCancelled(true);
				return;
			}

			// prevent any buguing click
			if ((event.getCursor() != null && event.getCursor().getType() != Material.AIR) || !allowedCraftingClicks.contains(event.getAction())) {
				event.setCancelled(true);
				return;
			}

			// check for permission
			if (!data.hasPermission(player)) {
				event.setCancelled(true);
				Message.NOT_ENOUGH_PERMS_CRAFT.format(ChatColor.RED).send(player);
				return;
			}

			// generate new item so the item isn't always the same
			player.playSound(player.getLocation(), VersionSound.BLOCK_ANVIL_USE.getSound(), 1, 1);
			inv.setItem(16, data.generateNewItem());

			// remove used ingredients
			for (int j = 0; j < 9; j++) {
				int slot = MMOItems.plugin.getRecipes().recipeSlots[j];
				ItemStack slotItem = inv.getItem(slot);
				if (slotItem == null || slotItem.getType() == Material.AIR)
					continue;

				slotItem.setAmount(inv.getItem(slot).getAmount() - data.getAmount(j));
				inv.setItem(slot, slotItem.getAmount() < 1 ? null : slotItem);
			}
		}
	}
}
