package net.Indyuce.mmoitems.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import net.Indyuce.mmoitems.api.Type;

public class AdvancedRecipePreview extends PluginInventory {
	private static final int[] glassSlots = { 0, 1, 2, 6, 7, 8, 9, 11, 15, 17, 18, 19, 20, 24, 25, 26 };
	private static final int[] ingredientSlots = { 3, 4, 5, 12, 13, 14, 21, 22, 23 };

	public AdvancedRecipePreview(Player player, Type type, String id) {
		super(player);
		this.type = type;
		this.id = id;
	}

	@Override
	public Inventory getInventory() {
		AdvancedRecipe data = MMOItems.plugin.getRecipes().getData(type, id);
		ItemStack item = data.getPreviewItem().clone();
		Inventory inv = Bukkit.createInventory(this, 27, Message.ADVANCED_RECIPES.formatRaw(ChatColor.UNDERLINE));

		String[] ingredients = data.getParsed().split("\\|");
		// mat:durability:name|mat:dur:name|mat:dur:name|...
		// or type:id|type:id|type:id|...
		for (int j = 0; j < 9; j++) {
			String formattedIngredient = ingredients[j];
			if (formattedIngredient.equals("AIR"))
				continue;

			String[] split = formattedIngredient.split("\\:");

			// type, id
			if (split.length == 2 && !formattedIngredient.endsWith(":")) {
				ItemStack ingredient = MMOItems.getItem(MMOItems.plugin.getTypes().get(split[0]), split[1]);
				ingredient.setAmount(data.getAmount(j));
				inv.setItem(ingredientSlots[j], ingredient);
				continue;
			}

			// material, name, durability
			ItemStack ingredient = new ItemStack(Material.valueOf(split[0]), data.getAmount(j), (short) Double.parseDouble(split[1]));
			ingredient.setAmount(data.getAmount(j));
			ItemMeta meta = ingredient.getItemMeta();
			meta.setDisplayName((split.length > 2 ? split[2] : ""));
			ingredient.setItemMeta(meta);

			inv.setItem(ingredientSlots[j], ingredient);
		}

		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(ChatColor.GREEN + "");
		glass.setItemMeta(glassMeta);

		for (int j : glassSlots)
			inv.setItem(j, glass);
		inv.setItem(16, item);
		inv.setItem(10, CustomItem.BACK.getItem());

		return inv;
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();

		event.setCancelled(true);
		if (MMOUtils.isPluginItem(item, false))
			if (item.equals(CustomItem.BACK.getItem()))
				new AdvancedRecipeList(player, type, 1).open();
	}
}
