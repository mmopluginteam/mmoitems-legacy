package net.Indyuce.mmoitems.stat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;

public class Enchants extends ItemStat {
	public Enchants() {
		super(new ItemStack(Material.ENCHANTED_BOOK), "Enchantments", new String[] { "The item enchants." }, "enchants", new String[] { "all" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.ENCHANTS).enable("Write in the chat the enchant you want to add.", ChatColor.AQUA + "Format: [ENCHANT] [LEVEL]");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			if (config.getConfigurationSection(path).getKeys(false).contains("enchants")) {
				Set<String> set = config.getConfigurationSection(path + ".enchants").getKeys(false);
				String last = Arrays.asList(set.toArray(new String[0])).get(set.size() - 1);
				config.set(path + ".enchants." + last, null);
				if (set.size() <= 1)
					config.set(path + ".enchants", null);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed " + last.substring(0, 1).toUpperCase() + last.substring(1).toLowerCase().replace("_", " ") + "§7.");
			}
		}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		String[] split = msg.split("\\ ");
		if (split.length != 2) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + msg + " is not a valid [ENCHANT] [LEVEL].");
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Example: 'DAMAGE_ALL 10' stands for Sharpness 10.");
			return false;
		}
		Enchantment enchant = null;
		for (Enchantment enchant1 : Enchantment.values()) {
			if (enchant1 == null)
				continue;
			if (enchant1.getName().equalsIgnoreCase(split[0].replace("-", "_")))
				enchant = enchant1;
		}
		if (enchant == null) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[0] + " is not a valid enchantment!");
			player.sendMessage(MMOItems.getPrefix() + "All enchants can be found here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html");
			return false;
		}
		int level = 0;
		try {
			level = (int) Double.parseDouble(split[1]);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[1] + " is not a valid number!");
			return false;
		}

		config.set(path + ".enchants." + enchant.getName(), level);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + enchant.getName() + " " + MMOUtils.intToRoman(level) + " successfully added.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		if (!config.getConfigurationSection(path).contains("enchants"))
			lore.add(ChatColor.RED + "No enchantment.");
		else if (config.getConfigurationSection(path + ".enchants").getKeys(false).isEmpty())
			lore.add(ChatColor.RED + "No enchantment.");
		else
			for (String s1 : config.getConfigurationSection(path + ".enchants").getKeys(false)) {
				String enchant = MMOUtils.caseOnWords(s1.toLowerCase().replace("_", " ").replace("-", " "));
				String level = MMOUtils.intToRoman(config.getInt(path + ".enchants." + s1));
				lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + enchant + " " + level);
			}
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to add an enchant.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the last enchant.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		for (String enchant : config.getConfigurationSection("enchants").getKeys(false)) {
			int level = config.getInt("enchants." + enchant);
			Enchantment enchantType = null;
			for (Enchantment type1 : Enchantment.values()) {
				if (type1 != null)
					if (type1.getName().equals(enchant.toUpperCase().replace("-", "_")))
						enchantType = type1;
			}

			if (enchantType == null) {
				item.error("Enchantments", enchant + " is not a valid enchantment name.");
				return false;
			}

			item.addEnchantment(enchantType, level);
		}
		return true;
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		item.error("Enchantments", "To add enchants to an item, please use MMOItem#addEnchantment(Enchantment, Integer) instead.");
		return false;
	}
}
