package net.Indyuce.mmoitems.gui.edition;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class ItemEdition extends PluginInventory {
	private static final int[] slots = { 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43 };

	public ItemEdition(Player player, Type type, String id) {
		super(player, type, id);
	}

	public ItemEdition(Player player, Type type, String id, int page, ItemStack cached) {
		super(player, type, id, page, cached);
	}

	@Override
	public Inventory getInventory() {
		int min = (page - 1) * slots.length;
		int max = page * slots.length;
		int n = 0;

		/*
		 * it has to determin what stats can be applied first because otherwise
		 * the for loop will just let some slots empty
		 */
		List<Stat> appliable = new ArrayList<>();
		Stat[] stats = Stat.values();
		for (Stat stat : stats)
			if (stat.c().isEnabled() && type.canHaveStat(stat) && stat.c().hasValidMaterial(getCachedItem()))
				appliable.add(stat);

		FileConfiguration config = type.getConfigFile();
		Inventory inv = Bukkit.createInventory(this, 54, ChatColor.UNDERLINE + "Item Edition: " + id);
		for (int j = min; j < Math.min(appliable.size(), max); j++) {
			Stat stat = appliable.get(j);
			ItemStack item = stat.c().getItem().clone();
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.values());
			meta.setDisplayName(ChatColor.GREEN + stat.c().getName());
			List<String> lore = new ArrayList<String>();
			for (String s1 : stat.c().getLore())
				lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', s1));

			if (!stat.c().displayValue(lore, config, id))
				return null;

			meta.setLore(lore);
			item.setItemMeta(meta);
			item = MMOItems.plugin.getNMS().addTag(item, new ItemTag("guiStat", stat.name()));

			inv.setItem(slots[n++], item);
		}

		// save corrections applied by the different stats
		type.saveConfigFile(config, null);

		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(ChatColor.RED + "- No ItemStat -");
		glass.setItemMeta(glassMeta);

		ItemStack next = new ItemStack(Material.ARROW);
		ItemMeta nextMeta = next.getItemMeta();
		nextMeta.setDisplayName(ChatColor.GREEN + "Next Page");
		next.setItemMeta(nextMeta);

		ItemStack previous = new ItemStack(Material.ARROW);
		ItemMeta previousMeta = previous.getItemMeta();
		previousMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
		previous.setItemMeta(previousMeta);

		addEditionInventoryItems(inv, false);

		while (n < slots.length)
			inv.setItem(slots[n++], glass);
		inv.setItem(27, page > 1 ? previous : null);
		inv.setItem(35, appliable.size() <= max ? null : next);

		return inv;
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		event.setCancelled(true);
		if (event.getInventory() != event.getClickedInventory())
			return;

		ItemStack item = event.getCurrentItem();
		if (!MMOUtils.isPluginItem(item, false) || event.getInventory().getItem(4) == null)
			return;

		if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next Page"))
			new ItemEdition(player, type, id, page + 1, getCachedItem()).open();

		if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous Page"))
			new ItemEdition(player, type, id, page - 1, getCachedItem()).open();

		String tag = MMOItems.plugin.getNMS().getStringTag(item, "guiStat");
		if (tag.equals(""))
			return;

		Stat stat = Stat.valueOf(tag);
		stat.c().guiClick(this, event, player, type, id, stat);
	}
}