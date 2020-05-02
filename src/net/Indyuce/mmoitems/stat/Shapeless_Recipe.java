package net.Indyuce.mmoitems.stat;

import java.util.ArrayList;
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
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;

public class Shapeless_Recipe extends ItemStat {
	public Shapeless_Recipe() {
		super(new ItemStack(Material.WOOD), "Shapeless Recipe", new String[] { "A shapeless recipe has no ingredient", "pattern in the workbench.", "Changing this value requires a reload.", "", "&9In order to disable the shapeless recipe,", "&9just remove all the ingredients from it." }, "shapeless-craft", new String[] { "all" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.SHAPELESS_RECIPE).enable("Write in the chat the ingredient you want to add.", ChatColor.AQUA + "Format: [MATERIAL] or [MATERIAL]:[DURABILITY]");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			if (config.getConfigurationSection(path).contains("shapeless-craft")) {
				List<String> shapelessRecipe = config.getStringList(path + ".shapeless-craft");
				if (shapelessRecipe.size() < 1)
					return true;

				String last = shapelessRecipe.get(shapelessRecipe.size() - 1);
				shapelessRecipe.remove(last);
				config.set(path + ".shapeless-craft", shapelessRecipe.isEmpty() ? null : shapelessRecipe);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed '" + last + "'.");
			}
		}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		Material material = null;
		String[] split = msg.toUpperCase().replace("-", "_").replace(" ", "_").split("\\:");
		try {
			material = Material.valueOf(split[0]);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[0] + " is not a valid material!");
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "All materials can be found here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			return false;
		}

		int durability = 0;
		if (split.length > 1)
			try {
				durability = Integer.parseInt(split[1]);
			} catch (Exception e1) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[1] + " is not a valid number!");
				return false;
			}

		List<String> list = config.getConfigurationSection(path).getKeys(false).contains("shapeless-craft") ? config.getStringList(path + ".shapeless-craft") : new ArrayList<String>();
		String format = material.name() + (durability > 0 ? ":" + durability : "");
		list.add(format);

		config.set(path + ".shapeless-craft", list);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + MMOUtils.caseOnWords(material.name().replace("_", " ").toLowerCase()) + " successfully added to the shapeless crafting recipe.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		if (!config.getConfigurationSection(path).contains("shapeless-craft"))
			lore.add(ChatColor.RED + "No shapeless recipe.");
		else if (config.getStringList(path + ".shapeless-craft").isEmpty())
			lore.add(ChatColor.RED + "No shapeless recipe.");
		else
			for (String s1 : config.getStringList(path + ".shapeless-craft"))
				lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + s1);
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to add an ingredient.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the last ingredient.");
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
}
