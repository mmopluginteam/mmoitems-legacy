package net.Indyuce.mmoitems.stat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

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
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Effects extends ItemStat {
	private DecimalFormat durationFormat = new DecimalFormat("0.#");

	public Effects() {
		super(new ItemStack(Material.POTION), "Effects", new String[] { "The potion effects your", "consumable item grants." }, "effects", new String[] { "consumable" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(inv, Stat.EFFECTS).enable("Write in the chat the permanent potion effect you want to add.", ChatColor.AQUA + "Format: [POTION_EFFECT] [DURATION] [AMPLIFIER]");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			if (config.getConfigurationSection(path).getKeys(false).contains("effects")) {
				Set<String> set = config.getConfigurationSection(path + ".effects").getKeys(false);
				String last = Arrays.asList(set.toArray(new String[0])).get(set.size() - 1);
				config.set(path + ".effects." + last, null);
				if (set.size() <= 1)
					config.set(path + ".effects", null);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed " + last.substring(0, 1).toUpperCase() + last.substring(1).toLowerCase() + ChatColor.GRAY + ".");
			}
		}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		String[] split = msg.split("\\ ");
		if (split.length != 3) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + msg + " is not a valid [POTION_EFFECT] [DURATION] [AMPLIFIER].");
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Example: 'FAST_DIGGING 30 3' stands for Haste 3 for 30 seconds.");
			return false;
		}

		PotionEffectType effect = null;
		for (PotionEffectType effect1 : PotionEffectType.values())
			if (effect1 != null)
				if (effect1.getName().equalsIgnoreCase(split[0].replace("-", "_"))) {
					effect = effect1;
					break;
				}

		if (effect == null) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[0] + " is not a valid potion effect!");
			player.sendMessage(MMOItems.getPrefix() + "All potion effects can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html");
			return false;
		}

		double duration = 0;
		try {
			duration = Double.parseDouble(split[1]);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[1] + " is not a valid number!");
			return false;
		}

		int amplifier = 0;
		try {
			amplifier = (int) Double.parseDouble(split[2]);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[2] + " is not a valid number!");
			return false;
		}

		config.set(path + ".effects." + effect.getName(), duration + "," + amplifier);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + effect.getName() + " " + amplifier + " successfully added.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		if (!config.getConfigurationSection(path).contains("effects"))
			lore.add(ChatColor.RED + "No effect.");
		else if (config.getConfigurationSection(path + ".effects").getKeys(false).isEmpty())
			lore.add(ChatColor.RED + "No effect.");
		else
			for (String s1 : config.getConfigurationSection(path + ".effects").getKeys(false)) {
				String effect = s1;
				effect = effect.replace("-", " ").replace("_", " ");
				effect = effect.substring(0, 1).toUpperCase() + effect.substring(1).toLowerCase();
				String[] split = config.getString(path + ".effects." + s1).split("\\,");
				String durationFormat = "";
				try {
					durationFormat = "" + Double.parseDouble(split[0]);
				} catch (Exception e) {
					durationFormat = "?";
				}

				if (split.length == 1)
					lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + effect + " I " + ChatColor.GRAY + "(" + ChatColor.GREEN + durationFormat + ChatColor.GRAY + "s)");

				if (split.length == 2) {
					String amplifierFormat = "";
					try {
						amplifierFormat = "" + MMOUtils.intToRoman(Integer.parseInt(split[1]));
					} catch (Exception e) {
						amplifierFormat = "?";
					}
					lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + effect + " " + amplifierFormat + " " + ChatColor.GRAY + "(" + ChatColor.GREEN + durationFormat + ChatColor.GRAY + "s)");
				}
			}
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to add an effect.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove the last effect.");
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		String stringTag = "";
		List<String> loreEffects = new ArrayList<String>();
		for (String effect : config.getConfigurationSection("effects").getKeys(false)) {
			PotionEffectType peType = null;
			for (PotionEffectType type1 : PotionEffectType.values())
				if (type1 != null)
					if (type1.getName().equals(effect.toUpperCase().replace("-", "_"))) {
						peType = type1;
						break;
					}

			if (peType == null) {
				item.error("Potion Effects", effect + " is not a valid potion effect name.");
				return false;
			}

			String[] split = config.getString("effects." + effect).split("\\,");
			double duration = 0;
			try {
				duration = Double.parseDouble(split[0]);
			} catch (Exception e) {
				item.error("Potion Effects", split[0] + " is not a valid number.");
				return false;
			}

			if (split.length == 1) {
				String format = ItemStat.translate("effect").replace("#e", MMOItems.getLanguage().getPotionEffectName(peType) + " I").replace("#d", durationFormat.format(duration));
				loreEffects.add(format);
			}

			if (split.length == 2) {
				int amplifier = 0;
				try {
					amplifier = Integer.parseInt(split[1]);
				} catch (Exception e) {
					item.error("Potion Effects", split[1] + " is not a valid integer.");
					return false;
				}

				String format = ItemStat.translate("effect").replace("#e", MMOItems.getLanguage().getPotionEffectName(peType) + " " + MMOUtils.intToRoman(amplifier)).replace("#d", durationFormat.format(duration));
				loreEffects.add(format);
				stringTag += (stringTag.length() > 0 ? ";" : "") + peType.getName() + ":" + duration + ":" + amplifier;
			}
		}
		item.insertInLore("effects", loreEffects);
		item.addItemTag(new ItemTag("MMOITEMS_EFFECTS", stringTag));
		return true;
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		item.error("Effects", "To add effects to an item, please use MMOItem#setEffects(PotionEffect[]) instead.");
		return false;
	}
}
