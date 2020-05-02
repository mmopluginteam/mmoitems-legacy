package net.Indyuce.mmoitems.gui.edition;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.UpdaterData;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.listener.ItemUpdater;

public class ItemUpdaterEdition extends PluginInventory {
	public ItemUpdaterEdition(Player player, Type type, String id) {
		this(player, type, id, null);
	}

	public ItemUpdaterEdition(Player player, Type type, String id, ItemStack cached) {
		super(player, type, id, 1, cached);
	}

	@Override
	public Inventory getInventory() {
		Inventory inv = Bukkit.createInventory(this, 54, ChatColor.UNDERLINE + "Item Updater: " + id);

		// setup if not in map
		String itemPath = type.getId() + "." + id;
		if (!ItemUpdater.hasData(itemPath)) {
			ItemUpdater.enableUpdater(itemPath);
			player.sendMessage(ChatColor.YELLOW + "Successfully enabled the item updater for " + id + ".");
		}

		UpdaterData did = ItemUpdater.getData(itemPath);

		ItemStack disable = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemMeta disableMeta = disable.getItemMeta();
		disableMeta.setDisplayName(ChatColor.GREEN + "Disable");
		List<String> disableLore = new ArrayList<String>();
		disableLore.add(ChatColor.GRAY + "Your item won't update anymore.");
		disableLore.add("");
		disableLore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to disable the item updater.");
		disableMeta.setLore(disableLore);
		disable.setItemMeta(disableMeta);

		inv.setItem(20, getBooleanItem("Name", did.keepName(), "Your item will keep its", "old display name when updated."));
		inv.setItem(21, getBooleanItem("Lore", did.keepLore(), "Any lore starting with '&7' will be", "kept when updating your item.", ChatColor.RED + "May not support every enchant plugin."));
		inv.setItem(29, getBooleanItem("Gems", did.keepGems(), "Your item will keep its", "old gems when updated."));
		inv.setItem(30, getBooleanItem("Enchants", did.keepEnchants(), "Your item will keep its", "old enchants when updated."));
		inv.setItem(39, getBooleanItem("Durability", did.keepDurability(), "Your item will keep its", "old durability when updated."));
		inv.setItem(32, disable);

		inv.setItem(4, getCachedItem());

		return inv;
	}

	private ItemStack getBooleanItem(String name, boolean bool, String... lines) {
		ItemStack stack = new ItemStack(Material.INK_SACK, 1, (short) (bool ? 10 : 8));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Keep " + name + "?");
		List<String> lore = new ArrayList<String>();
		for (String line : lines)
			lore.add(ChatColor.GRAY + line);
		lore.add("");
		lore.add(bool ? ChatColor.RED + SpecialChar.listDash + " Click to toggle off." : ChatColor.GREEN + SpecialChar.listDash + " Click to toggle on.");
		meta.setLore(lore);
		stack.setItemMeta(meta);

		return stack;
	}

	@Override
	public void whenClicked(InventoryClickEvent e) {
		ItemStack i = e.getCurrentItem();

		e.setCancelled(true);
		if (e.getInventory() != e.getClickedInventory() || !MMOUtils.isPluginItem(i, false))
			return;

		// safe check
		String path = type.getId() + "." + id;
		if (!ItemUpdater.hasData(path)) {
			player.closeInventory();
			return;
		}

		UpdaterData did = ItemUpdater.getData(path);
		if (i.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Keep Lore?")) {
			did.setKeepLore(!did.keepLore());
			new ItemUpdaterEdition(player, type, id, getCachedItem()).open();
		}
		if (i.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Keep Enchants?")) {
			did.setKeepEnchants(!did.keepEnchants());
			new ItemUpdaterEdition(player, type, id, getCachedItem()).open();
		}
		if (i.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Keep Durability?")) {
			did.setKeepDurability(!did.keepDurability());
			new ItemUpdaterEdition(player, type, id, getCachedItem()).open();
		}
		if (i.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Keep Name?")) {
			did.setKeepName(!did.keepName());
			new ItemUpdaterEdition(player, type, id, getCachedItem()).open();
		}
		if (i.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Keep Gems?")) {
			did.setKeepGems(!did.keepGems());
			new ItemUpdaterEdition(player, type, id, getCachedItem()).open();
		}
		if (i.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Disable")) {
			ItemUpdater.disableUpdater(path);
			player.closeInventory();
			player.sendMessage(ChatColor.YELLOW + "Successfully disabled the item updater for " + id + ".");
		}
	}
}