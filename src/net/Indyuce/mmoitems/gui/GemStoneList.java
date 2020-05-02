package net.Indyuce.mmoitems.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.CustomItem;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.StatFormat;

@SuppressWarnings("deprecation")
public class GemStoneList extends PluginInventory {
	public static final String gemSeparator = "|;;;|";
	public static final String statSeparator = "|;;|";
	public static final String nameSeparator = "|;|";

	public GemStoneList(Player player) {
		super(player);
	}

	@Override
	public Inventory getInventory() {
		ItemStack item = player.getItemInHand();
		int[] slots = new int[] { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43 };

		String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_GEM_STONE");
		String[] splitGems = tag.split(Pattern.quote(gemSeparator));
		int sockets = (tag.equals("") ? 0 : splitGems.length) + getEmptySockets(item.getItemMeta().getLore());
		Inventory inv = Bukkit.createInventory(this, getSlots(sockets), Message.GEM_STATS.formatRaw(ChatColor.UNDERLINE));

		try {
			for (int n = 0; n < sockets; n++) {
				if (n < splitGems.length && !splitGems[n].equals("")) {
					String[] splitGem = splitGems[n].split(Pattern.quote(nameSeparator));

					ItemStack gem = new ItemStack(Material.EMERALD);
					ItemMeta gemMeta = gem.getItemMeta();
					List<String> lore = new ArrayList<String>();
					if (splitGem.length > 1)
						for (String splitStat : splitGem[1].split(Pattern.quote(statSeparator))) {
							String[] splitStatData = splitStat.split("\\=");
							double value = Double.parseDouble(splitStatData[1]);
							lore.add(ChatColor.GRAY + Stat.valueOf(splitStatData[0]).c().format(value, "#", new StatFormat("##").format(value)));
						}

					gemMeta.setLore(lore);
					gemMeta.setDisplayName(ChatColor.GREEN + splitGem[0]);
					gem.setItemMeta(gemMeta);

					inv.setItem(slots[n], gem);
					continue;
				}

				inv.setItem(slots[n], CustomItem.NO_GEM_STONE.getItem());
			}
		} catch (Exception e) {
			/*
			 * couldn't read gem data from the item, meaning the item is
			 * outdated and is using an older gem stone data format
			 */
		}

		return inv;
	}

	private int getSlots(int gemSlots) {
		return 9 * (2 + Math.min(4, Math.max(1, (int) Math.ceil(((double) gemSlots) / 7d))));
	}

	private int getEmptySockets(List<String> lore) {
		int n = 0;
		for (String line : lore)
			if (line.equals(ItemStat.translate("empty-gem-socket").replace("#d", SpecialChar.diamond)))
				n++;
		return n;
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		event.setCancelled(true);
	}
}
