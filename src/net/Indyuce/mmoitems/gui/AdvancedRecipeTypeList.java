package net.Indyuce.mmoitems.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.CustomItem;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class AdvancedRecipeTypeList extends PluginInventory {
	private static final int[] slots = { 1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25 };

	public AdvancedRecipeTypeList(Player player, int page) {
		super(player, page);
	}

	@Override
	public Inventory getInventory() {
		Inventory inv = Bukkit.createInventory(this, 27, Message.ADVANCED_RECIPES.formatRaw(ChatColor.UNDERLINE));

		List<Type> types = MMOItems.plugin.getRecipes().getAvailableTypes();
		int min = (page - 1) * 21;
		int max = page * 21;
		for (int j = min; j < Math.min(max, types.size()); j++) {
			Type type = types.get(j);
			int recipes = MMOItems.plugin.getRecipes().getTypeRecipes(type).size();

			ItemStack item = CustomItem.TYPE_DISPLAY.getItem().clone();
			item.setType(type.getItem().getType());
			item.setAmount(Math.max(1, Math.min(64, recipes)));

			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(meta.getDisplayName().replace("#type#", type.getName()));
			meta.addItemFlags(ItemFlag.values());
			List<String> lore = meta.getLore();
			for (int k = 0; k < lore.size(); k++)
				lore.set(k, lore.get(k).replace("#recipes#", "" + recipes));
			meta.setLore(lore);
			item.setItemMeta(meta);

			item = MMOItems.plugin.getNMS().addTag(item, new ItemTag("typeId", type.getId()));

			inv.setItem(slots[j - min], item);
		}

		ItemStack noType = CustomItem.NO_TYPE.getItem();
		for (int j : slots)
			if (inv.getItem(j) == null)
				inv.setItem(j, noType);

		inv.setItem(9, page > 1 ? CustomItem.PREVIOUS_PAGE.getItem() : CustomItem.BACK.getItem());
		inv.setItem(17, inv.getItem(slots[slots.length - 1]).equals(noType) ? null : CustomItem.NEXT_PAGE.getItem());

		return inv;
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();

		event.setCancelled(true);
		if (event.getClickedInventory() != event.getInventory())
			return;

		if (MMOUtils.isPluginItem(item, false)) {
			if (item.equals(CustomItem.BACK.getItem()))
				new AdvancedWorkbench(player).open();

			if (item.equals(CustomItem.NEXT_PAGE.getItem())) {
				page++;
				open();
			}

			if (item.equals(CustomItem.PREVIOUS_PAGE.getItem()) && page > 1) {
				page--;
				open();
			}
		}

		// show recipe when click
		String tag = MMOItems.plugin.getNMS().getStringTag(item, "typeId");
		if (!tag.equals(""))
			new AdvancedRecipeList(player, MMOItems.plugin.getTypes().get(tag), 1).open();
	}
}
