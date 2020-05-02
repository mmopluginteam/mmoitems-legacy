package net.Indyuce.mmoitems.stat;

import java.util.ArrayList;
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

public class Perm_Effects extends ItemStat {
	public Perm_Effects() {
		super(new ItemStack(Material.POTION), "Permanent Effects", new String[] { "The potion effects your", "item grants to the holder." }, "perm-effects", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "armor" });
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (event.getAction() == InventoryAction.PICKUP_ALL) 
			new StatEdition(inv, Stat.PERM_EFFECTS).enable("Write in the chat the permanent potion effect you want to add.", "Format: [POTION_EFFECT] [AMPLIFIER]");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			if (config.getConfigurationSection(path).contains("perm-effects")) {
				Set<String> set = config.getConfigurationSection(path + ".perm-effects").getKeys(false);
				String last = new ArrayList<String>(set).get(set.size() - 1);
				config.set(path + ".perm-effects." + last, null);
				if (set.size() <= 1)
					config.set(path + ".perm-effects", null);
				type.saveConfigFile(config, path);
				new ItemEdition(player, type, path).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed " + last.substring(0, 1).toUpperCase() + last.substring(1).toLowerCase() + "§7.");
			}
		}
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		String[] split = msg.split("\\ ");
		if (split.length != 2) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + msg + " is not a valid [POTION_EFFECT] [AMPLIFIER].");
			player.sendMessage(MMOItems.getPrefix() + ChatColor.GREEN + "Example: 'INCREASE_DAMAGE 4' stands for Strength 4.");
			return false;
		}
		PotionEffectType effect = null;
		for (PotionEffectType effect1 : PotionEffectType.values())
			if (effect1 != null)
				if (effect1.getName().equalsIgnoreCase(split[0].replace("-", "_")))
					effect = effect1;
		if (effect == null) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[0] + " is not a valid potion effect!");
			player.sendMessage(MMOItems.getPrefix() + "All potion effects can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html");
			return false;
		}
		int amplifier = 0;
		try {
			amplifier = (int) Double.parseDouble(split[1]);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[1] + " is not a valid number!");
			return false;
		}

		config.set(path + ".perm-effects." + effect.getName(), amplifier);
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + effect.getName() + " " + amplifier + " successfully added.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");
		if (!config.getConfigurationSection(path).contains("perm-effects"))
			lore.add(ChatColor.RED + "No permanent effect.");
		else if (config.getConfigurationSection(path + ".perm-effects").getKeys(false).isEmpty())
			lore.add(ChatColor.RED + "No permanent effect.");
		else
			for (String s1 : config.getConfigurationSection(path + ".perm-effects").getKeys(false)) {
				String effect = s1;
				effect = effect.replace("-", " ").replace("_", " ");
				effect = effect.substring(0, 1).toUpperCase() + effect.substring(1).toLowerCase();
				String level = MMOUtils.intToRoman(config.getInt(path + ".perm-effects." + s1));
				lore.add(ChatColor.GRAY + "* " + ChatColor.GREEN + effect + " " + level);
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
		for (String effect : config.getConfigurationSection("perm-effects").getKeys(false)) {
			PotionEffectType peType = null;
			for (PotionEffectType type1 : PotionEffectType.values()) {
				if (type1 != null)
					if (type1.getName().equalsIgnoreCase(effect.replace("-", "_"))) {
						peType = type1;
						break;
					}
			}

			if (peType == null) {
				item.error("Permanent Effects", effect + " is not a valid potion effect name.");
				return false;
			}

			int amplifier = config.getInt("perm-effects." + effect);
			loreEffects.add(ItemStat.translate("perm-effect").replace("#", MMOItems.getLanguage().getPotionEffectName(peType) + " " + MMOUtils.intToRoman(amplifier)));
			stringTag += (stringTag.length() > 0 ? ";" : "") + peType.getName() + ":" + amplifier;
		}
		item.insertInLore("perm-effects", loreEffects);
		item.addItemTag(new ItemTag("MMOITEMS_PERM_EFFECTS", stringTag));
		return true;
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		item.error("Permanent Effects", "To add permanent effects to an item, please use MMOItem#setPermanentEffects(PotionEffect[]) instead.");
		return false;
	}
}
