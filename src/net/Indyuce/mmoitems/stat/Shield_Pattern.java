package net.Indyuce.mmoitems.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BlockStateMeta;

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

public class Shield_Pattern extends ItemStat {
	public Shield_Pattern() {
		super("Shield Pattern", new String[] { "The color & patterns", "of your shield." }, "shield-pattern", new String[] { "all" });

		if (MMOItems.plugin.getVersion().isBelowOrEqual(1, 8)) {
			disable();
			return;
		}

		setItemType(Material.SHIELD);
		addCompatibleMaterial(dm(Material.SHIELD));
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
		Banner banner = (Banner) meta.getBlockState();

		// apply dye color
		if (config.getConfigurationSection("shield-pattern").contains("color")) {
			String colorFormat = config.getString("shield-pattern.color").toUpperCase().replace("-", "_").replace(" ", "_");
			try {
				banner.setBaseColor(DyeColor.valueOf(colorFormat));
			} catch (Exception e1) {
				item.error("Shield Pattern", colorFormat + " is not a valid dye color!");
				return false;
			}
		}

		// apply shield patterns
		for (String s : config.getConfigurationSection("shield-pattern").getKeys(false)) {
			if (s.equalsIgnoreCase("color"))
				continue;

			String patternFormat = config.getString("shield-pattern." + s + ".pattern").toUpperCase().replace("-", "_").replace(" ", "_");
			PatternType patternType;
			try {
				patternType = PatternType.valueOf(patternFormat);
			} catch (Exception e1) {
				item.error("Shield Pattern", patternFormat + " is not a valid pattern type!");
				return false;
			}

			String colorFormat = config.getString("shield-pattern." + s + ".color").toUpperCase().replace("-", "_").replace(" ", "_");
			DyeColor dyeColor;
			try {
				dyeColor = DyeColor.valueOf(colorFormat);
			} catch (Exception e1) {
				item.error("Shield Pattern", patternFormat + " is not a valid dye color!");
				return false;
			}

			banner.addPattern(new Pattern(dyeColor, patternType));
		}

		item.setBannerBlockState(banner);
		item.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
		return true;
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		item.error("Shield Pattern", "To apply a shield pattern, please use MMOItem#setShieldPattern(Color, Pattern...) instead.");
		return false;
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.SHIELD_PATTERN, 0).enable("Write in the chat the color of your shield.");

		if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			config.set(path + ".shield-pattern.color", null);

			type.saveConfigFile(config, path);
			new ItemEdition(player, type, path).open();
			player.sendMessage(MMOItems.getPrefix() + "Successfully reset the shield color.");
		}
		if (event.getAction() == InventoryAction.PICKUP_HALF)
			new StatEdition(inv, Stat.SHIELD_PATTERN, 1).enable("Write in the chat the pattern you want to add.", ChatColor.AQUA + "Format: [PATTERN_TYPE] [DYE_COLOR]");

		if (event.getAction() == InventoryAction.DROP_ONE_SLOT) {
			if (!config.getConfigurationSection(path).contains("shield-pattern"))
				return false;

			Set<String> set = config.getConfigurationSection(path + ".shield-pattern").getKeys(false);
			String last = new ArrayList<String>(set).get(set.size() - 1);
			if (last.equalsIgnoreCase("color"))
				return false;

			config.set(path + ".shield-pattern." + last, null);
			type.saveConfigFile(config, path);
			new ItemEdition(player, type, path).open();
			player.sendMessage(MMOItems.getPrefix() + "Successfully removed the last pattern.");
		}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		int editedStatData = (int) info[0];

		if (editedStatData == 1) {
			String[] split = msg.split("\\ ");
			if (split.length != 2) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + msg + " is not a valid [PATTERN_TYPE] [DYE_COLOR].");
				return false;
			}

			String patternFormat = split[0].toUpperCase().replace("-", "_").replace(" ", "_");
			PatternType patternType;
			try {
				patternType = PatternType.valueOf(patternFormat);
			} catch (Exception e1) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + patternFormat + " is not a valid pattern type!");
				return false;
			}

			String colorFormat = split[1].toUpperCase().replace("-", "_").replace(" ", "_");
			DyeColor dyeColor;
			try {
				dyeColor = DyeColor.valueOf(colorFormat);
			} catch (Exception e1) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + colorFormat + " is not a valid dye color!");
				return false;
			}

			int availableKey = getNextAvailableKey(config.getConfigurationSection(path + ".shield-pattern"));
			if (availableKey < 0) {
				player.sendMessage(MMOItems.getPrefix() + "You can have more than 100 shield patterns on a single item.");
				return false;
			}

			config.set(path + ".shield-pattern." + availableKey + ".pattern", patternType.name());
			config.set(path + ".shield-pattern." + availableKey + ".color", dyeColor.name());
			type.saveConfigFile(config, path);
			new ItemEdition(player, type, path).open();
			player.sendMessage(MMOItems.getPrefix() + MMOUtils.caseOnWords(patternType.name().toLowerCase().replace("_", " ")) + " successfully added.");
			return true;
		}

		DyeColor color;
		try {
			color = DyeColor.valueOf(msg.toUpperCase().replace("-", "_").replace(" ", "_"));
		} catch (Exception e) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + msg + " is not a valid color!");
			return false;
		}

		config.set(path + ".shield-pattern.color", color.name());
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "Shield color successfully changed.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		if (!config.getConfigurationSection(path).contains("shield-pattern"))
			lore.add(ChatColor.RED + "No shield pattern.");
		else {

			// display shield base color
			if (config.getConfigurationSection(path + ".shield-pattern").contains("color")) {
				String format = config.getString(path + ".shield-pattern.color").toUpperCase().replace("-", "_").replace(" ", "_");
				try {
					lore.add(ChatColor.GRAY + "* Shield Base Color: " + ChatColor.GREEN + DyeColor.valueOf(format).name());
				} catch (Exception e) {
					lore.add(ChatColor.DARK_RED + "Wrong base color.");
				}
			}

			// display patterns
			for (String s : config.getConfigurationSection(path + ".shield-pattern").getKeys(false)) {
				if (s.equalsIgnoreCase("color"))
					continue;

				String colorFormat = config.getString(path + ".shield-pattern." + s + ".color").toUpperCase().replace("-", "_").replace(" ", "_");
				String patternFormat = config.getString(path + ".shield-pattern." + s + ".pattern").toUpperCase().replace("-", "_").replace(" ", "_");
				try {
					lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + PatternType.valueOf(patternFormat).name() + ChatColor.GRAY + " - " + ChatColor.GREEN + DyeColor.valueOf(colorFormat).name());
				} catch (Exception e) {
					lore.add(ChatColor.DARK_RED + "Wrong shield pattern.");
				}
			}
		}
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Left Click to change the shield color.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Shift Left Click to reset the shield color.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right Click to add a pattern.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Drop to remove the last pattern.");
		return true;
	}

	private int getNextAvailableKey(ConfigurationSection section) {
		for (int j = 0; j < 100; j++)
			if (!section.contains("" + j))
				return j;
		return -1;
	}
}
