package net.Indyuce.mmoitems.stat;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.AdvancedRecipeEdition;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;

public class Advanced_Crafting_Recipe extends ItemStat {
	public Advanced_Crafting_Recipe() {
		super(new ItemStack(Material.WORKBENCH), "Advanced Crafting Recipe", new String[] { "The advanced recipe of your item.", "Changing this value requires &o/mi reload adv-recipes&7." }, "advanced-craft", new String[] { "all" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new AdvancedRecipeEdition(player, type, path).open();
		if (event.getAction() == InventoryAction.PICKUP_HALF)
			if (config.getConfigurationSection(path).contains("advanced-craft")) {
				config.set(path + ".advanced-craft", null);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Advanced Crafting Recipe successfully removed.");
			}
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item);
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to edit the advanced crafting recipe.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the advanced crafting recipe.");
		return true;
	}
}
