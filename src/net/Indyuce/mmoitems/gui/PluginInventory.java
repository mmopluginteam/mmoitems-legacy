package net.Indyuce.mmoitems.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Type;

public class PluginInventory implements InventoryHolder {
	protected int page;
	protected Player player;

	protected Type type;
	protected String id;

	private ItemStack cached;

	public PluginInventory(Player player) {
		this(player, null, null, 1, null);
	}

	public PluginInventory(Player player, int page) {
		this(player, null, null, page, null);
	}

	public PluginInventory(Player player, Type type, String id) {
		this(player, type, id, 1, null);
	}

	public PluginInventory(Player player, Type type, String id, int page) {
		this(player, type, id, page, null);
	}

	public PluginInventory(Player player, Type type, String id, int page, ItemStack cached) {
		this.page = page;
		this.player = player;
		this.type = type;
		this.id = id == null ? null : id.toUpperCase().replace("-", "_").replace(" ", "_");
		this.cached = cached;
	}

	public int getPage() {
		return page;
	}

	public Player getPlayer() {
		return player;
	}

	public Type getItemType() {
		return type;
	}

	public String getItemID() {
		return id;
	}

	public boolean isEditionInventory() {
		return type != null;
	}

	public ItemStack getCachedItem() {
		return cached != null ? cached : (cached = MMOItems.getItem(type, id));
	}

	@Override
	public Inventory getInventory() {
		return Bukkit.createInventory(this, 27, "How did you get here?");
	}

	public void whenClicked(InventoryClickEvent event) {
	}

	public void addEditionInventoryItems(Inventory inv, boolean backBool) {
		ItemStack get = new ItemStack(Material.SULPHUR);
		ItemMeta getMeta = get.getItemMeta();
		getMeta.addItemFlags(ItemFlag.values());
		getMeta.setDisplayName(ChatColor.GREEN + SpecialChar.fourEdgedClub + " Get the Item! " + SpecialChar.fourEdgedClub);
		List<String> getLore = new ArrayList<String>();
		getLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to generate the item and");
		getLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "add a copy to your inventory.");
		getLore.add("");
		getLore.add(ChatColor.YELLOW + "Alias: /mi " + type.getId() + " " + id);
		getMeta.setLore(getLore);
		get.setItemMeta(getMeta);

		if (backBool) {
			ItemStack back = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = back.getItemMeta();
			backMeta.setDisplayName(ChatColor.GREEN + SpecialChar.rightArrow + " Back");
			back.setItemMeta(backMeta);

			inv.setItem(6, back);
		}

		inv.setItem(2, get);
		inv.setItem(4, cached != null ? cached : MMOItems.getItem(type, id));
	}

	public void open() {
		getPlayer().openInventory(getInventory());
	}
}
