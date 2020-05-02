package net.Indyuce.mmoitems.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.UpdaterData;
import net.Indyuce.mmoitems.api.item.GemStone;
import net.Indyuce.mmoitems.gui.GemStoneList;
import net.Indyuce.mmoitems.version.nms.Attribute;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class ItemUpdater implements Listener {
	private static Map<String, UpdaterData> map = new HashMap<>();

	public static UpdaterData getData(String path) {
		return map.get(path);
	}

	public static boolean hasData(String path) {
		return map.containsKey(path);
	}

	public static Collection<UpdaterData> getDatas() {
		return map.values();
	}

	public static Set<String> getItemPaths() {
		return map.keySet();
	}

	public static void disableUpdater(String path) {
		map.remove(path);
	}

	public static void enableUpdater(String path) {
		enableUpdater(path, new UpdaterData(path, UUID.randomUUID()));
	}

	public static void enableUpdater(String path, UpdaterData data) {
		map.put(path, data);
	}

	// updates every item when clicked
	@EventHandler
	public void a(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR)
			return;

		ItemStack newItem = getUpdated(item);
		if (!newItem.isSimilar(item))
			event.setCurrentItem(newItem);
	}

	// updates every item when joining
	@EventHandler
	public void b(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		player.getEquipment().setHelmet(getUpdated(player.getEquipment().getHelmet()));
		player.getEquipment().setChestplate(getUpdated(player.getEquipment().getChestplate()));
		player.getEquipment().setLeggings(getUpdated(player.getEquipment().getLeggings()));
		player.getEquipment().setBoots(getUpdated(player.getEquipment().getBoots()));

		for (int j = 0; j < 9; j++)
			player.getInventory().setItem(j, getUpdated(player.getInventory().getItem(j)));
		if (MMOItems.plugin.getVersion().isStrictlyHigher(1, 8))
			player.getEquipment().setItemInOffHand(getUpdated(player.getEquipment().getItemInOffHand()));
	}

	public static ItemStack getUpdated(ItemStack item) {
		Type type = Type.get(item);
		if (type == null)
			return item;

		// check if contained in map
		String id = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_ID");
		String path = type.getId() + "." + id;
		if (!map.containsKey(path))
			return item;

		// no need to update
		UpdaterData did = map.get(path);
		if (did.getUUID().toString().equals(MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_UUID")))
			return item;

		ItemStack newItem = MMOItems.getItem(type, id);
		ItemMeta newItemMeta = newItem.getItemMeta();

		// data from the item that might have to be updated
		List<ItemTag> tags = new ArrayList<ItemTag>();
		List<Attribute> attributes = new ArrayList<Attribute>();
		List<String> lore = newItemMeta.getLore();

		// remember amount
		newItem.setAmount(item.getAmount());

		/*
		 * add old enchants to the item. warning - if enabled the item will
		 * remember of ANY enchant on the old item, even the enchants that were
		 * removed!
		 */
		if (did.keepEnchants()) {
			Map<Enchantment, Integer> enchants = item.getItemMeta().getEnchants();
			for (Enchantment enchant : enchants.keySet())
				newItemMeta.addEnchant(enchant, enchants.get(enchant), true);
		}

		if (did.keepGems()) {
			String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_GEM_STONE");
			tags.add(new ItemTag("MMOITEMS_GEM_STONE", tag));

			/*
			 * stats must be loaded in the map since if you don't calculate the
			 * sum you can't calculate the total value for each stat that needs
			 * to be applied onto the item if two same stats are applied
			 * simultaneously
			 */
			Map<Stat, Double> map = new HashMap<>();

			for (String formattedGemStone : tag.split(Pattern.quote(GemStoneList.gemSeparator))) {
				if (formattedGemStone.equals(""))
					continue;

				String[] gemSplit = formattedGemStone.split(Pattern.quote(GemStoneList.nameSeparator));

				// if there's no more slot, just don't add newer gems
				int emptySlot = GemStone.getFirstSocket(lore);
				if (emptySlot < 0)
					break;

				lore.set(emptySlot, ChatColor.GREEN + SpecialChar.diamond + " " + gemSplit[0]);
				for (String formattedStat : gemSplit[1].split(Pattern.quote(GemStoneList.statSeparator))) {
					String[] statSplit = formattedStat.split("\\=");
					Stat stat = Stat.valueOf(statSplit[0]);
					map.put(stat, (map.containsKey(stat) ? map.get(stat) : 0) + Double.parseDouble(statSplit[1]));
				}
			}

			for (Stat stat : map.keySet())
				stat.c().applyOntoItem(type, stat, map.get(stat), MMOItems.plugin.getNMS().getDoubleTag(newItem, "MMOITEMS_" + stat.name()), attributes, tags);
		}

		/*
		 * keepLore is used to save enchants from custom enchants plugins that
		 * only use lore to save enchant data
		 */
		if (did.keepLore()) {
			int n = 0;
			for (String s : item.getItemMeta().getLore()) {
				if (!s.startsWith(ChatColor.GRAY + ""))
					break;
				lore.add(n++, s);
			}
		}

		/*
		 * keep durability can be used for tools to save their durability so
		 * users do not get extra durability when the item is updated
		 */
		if (did.keepDurability())
			newItem.setDurability(item.getDurability());

		/*
		 * keep name so players who renamed the item in the anvil does not have
		 * to rename it again
		 */
		if (did.keepName())
			if (item.getItemMeta().hasDisplayName())
				newItemMeta.setDisplayName(item.getItemMeta().getDisplayName());

		newItemMeta.setLore(lore);
		newItem.setItemMeta(newItemMeta);

		// apply attributes if the list is not empty
		// if there are attributes, they're always tags so only need to check
		// one list
		if (!tags.isEmpty()) {
			newItem = MMOItems.plugin.getNMS().addAttribute(newItem, attributes);
			newItem = MMOItems.plugin.getNMS().addTag(newItem, tags);
		}

		return newItem;
	}
}