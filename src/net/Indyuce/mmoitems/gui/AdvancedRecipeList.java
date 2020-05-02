package net.Indyuce.mmoitems.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.AdvancedRecipe;
import net.Indyuce.mmoitems.api.CustomItem;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class AdvancedRecipeList extends PluginInventory {

	/*
	 * caches the list of recipes the player can see. this way the plugin does
	 * not have to calculate again the recipes he has the permission to see each
	 * time the player changes the current page.
	 */
	private List<AdvancedRecipe> recipes;

	public AdvancedRecipeList(Player player, Type type, int page) {
		super(player, page);
		this.type = type;
	}

	@Override
	public Inventory getInventory() {
		Inventory inv = Bukkit.createInventory(this, 27, Message.ADVANCED_RECIPES.formatRaw(ChatColor.UNDERLINE));

		Integer[] slots = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25 };
		recipes = MMOItems.plugin.getRecipes().getRecipesAvailableForPlayer(type, player);
		int min = (page - 1) * 21;
		int max = page * 21;
		for (int j = min; j < max && j < recipes.size(); j++) {

			AdvancedRecipe recipe = recipes.get(j);
			ItemStack item = recipe.getPreviewItem().clone();

			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<String>();
			itemLore.add("");
			itemLore.add(Message.CLICK_ADVANCED_RECIPE.formatRaw(ChatColor.YELLOW, "#d", SpecialChar.listDash));
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);
			item = MMOItems.plugin.getNMS().addTag(item, new ItemTag("itemId", recipe.getItemId()));

			inv.setItem(slots[j - min], item);
		}

		ItemStack noItem = CustomItem.NO_ITEM.getItem();
		for (int j : slots)
			if (inv.getItem(j) == null)
				inv.setItem(j, noItem);

		inv.setItem(9, page > 1 ? CustomItem.PREVIOUS_PAGE.getItem() : CustomItem.BACK.getItem());
		inv.setItem(17, inv.getItem(slots[slots.length - 1]).equals(noItem) ? null : CustomItem.NEXT_PAGE.getItem());

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
				new AdvancedRecipeTypeList(player, 1).open();

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
		String tag = MMOItems.plugin.getNMS().getStringTag(item, "itemId");
		if (!tag.equals(""))
			new AdvancedRecipePreview(player, type, tag).open();
	}
}
