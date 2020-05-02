package net.Indyuce.mmoitems.stat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.Ability.CastingMode;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.AbilityEdition;
import net.Indyuce.mmoitems.gui.edition.AbilityListEdition;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Abilities extends ItemStat {
	private final DecimalFormat modifierFormat = new DecimalFormat("0.###");

	public Abilities() {
		super(new ItemStack(Material.BLAZE_POWDER), "Item Abilities", new String[] { "Make your item cast amazing abilities", "to kill monsters or buff yourself." }, "ability", new String[] { "piercing", "slashing", "blunt", "offhand", "range", "tool", "consumable", "armor" });
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		List<String> abilityLore = new ArrayList<>();
		boolean splitter = !MMOItems.getLanguage().abilitySplitter.equals("");
		String abilitiesTag = "";

		for (String key : config.getConfigurationSection("ability").getKeys(false)) {
			ConfigurationSection section = config.getConfigurationSection("ability." + key);
			if (!section.contains("type") || !section.contains("mode"))
				continue;

			String abilityFormat = config.getString("ability." + key + ".type").toUpperCase().replace("-", "_").replace(" ", "_");
			if (!MMOItems.plugin.getAbilities().hasAbility(abilityFormat)) {
				item.error("Item Abilities", abilityFormat + " is not a valid ability ID.");
				return false;
			}

			String modeFormat = config.getString("ability." + key + ".mode").toUpperCase().replace("-", "_").replace(" ", "_");
			CastingMode castMode = CastingMode.safeValueOf(modeFormat);
			if (castMode == null) {
				item.error("Item Abilities", modeFormat + " is not a valid casting mode.");
				return false;
			}

			Ability ability = MMOItems.plugin.getAbilities().getAbility(abilityFormat);
			if (!ability.isAllowedMode(castMode)) {
				item.error("Item Abilities", ability.getName() + " does not support " + castMode.getName() + " (casting mode).");
				return false;
			}

			/*
			 * the ability is valid, the string tag is initialized and tags are
			 * added. the tag is then added to the main tag that will contain
			 * all the abilities
			 */
			abilitiesTag += (abilitiesTag.isEmpty() ? "" : "%") + ability.getID() + "|" + castMode.name();

			abilityLore.add(ItemStat.translate("ability-format").replace("#c", MMOItems.getLanguage().getCastingModeName(castMode)).replace("#a", MMOItems.getLanguage().getAbilityName(ability)));
			String modifierFormat = ItemStat.translate("ability-modifier");
			for (String modifier : section.getKeys(false))
				if (!modifier.equals("type") && !modifier.equals("mode") && ability.getModifiers().contains(modifier)) {
					double value = section.getDouble(modifier);
					abilitiesTag += "|" + modifier + "=" + value;
					abilityLore.add(modifierFormat.replace("#m", MMOItems.getLanguage().getModifierName(modifier)).replace("#v", this.modifierFormat.format(value)));
				}
			if (splitter)
				abilityLore.add(MMOItems.getLanguage().abilitySplitter);
		}
		if (splitter && abilityLore.size() > 0)
			abilityLore.remove(abilityLore.size() - 1);

		item.insertInLore("abilities", abilityLore);
		item.addItemTag(new ItemTag("MMOITEMS_ABILITIES", abilitiesTag));
		return true;
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		return false;
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String id, Stat stat) {
		new AbilityListEdition(player, type, id).open();
		return true;
	}

	@Override
	public boolean chatListener(Type type, String id, Player player, FileConfiguration config, String message, Object... info) {
		String configKey = (String) info[0];
		String edited = (String) info[1];

		if (edited.equals("ability")) {
			String format = message.toUpperCase().replace("-", "_").replace(" ", "_").replaceAll("[^A-Z_]", "");
			if (!MMOItems.plugin.getAbilities().hasAbility(format)) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + format + " is not a valid ability!");
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "See all abilities: /mi list ability.");
				return false;
			}

			Ability ability = MMOItems.plugin.getAbilities().getAbility(format);

			config.set(id + ".ability." + configKey, null);
			config.set(id + ".ability." + configKey + ".type", format);
			type.saveConfigFile(config, id);
			new AbilityEdition(player, type, id, configKey).open();
			player.sendMessage(MMOItems.getPrefix() + "Successfully set the ability to " + ChatColor.GOLD + ability.getName() + ChatColor.GRAY + ".");
			return true;
		}

		if (edited.equals("mode")) {
			CastingMode castMode = CastingMode.safeValueOf(message);
			if (castMode == null) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Make sure you enter a valid casting mode.");
				return false;
			}

			Ability ability = MMOItems.plugin.getAbilities().getAbility(config.getString(id + ".ability." + configKey + ".type").toUpperCase().replace("-", "_").replace(" ", "_").replaceAll("[^A-Z_]", ""));
			if (!ability.isAllowedMode(castMode)) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "This ability does not support this casting mode.");
				return false;
			}

			config.set(id + ".ability." + configKey + ".mode", castMode.name());
			type.saveConfigFile(config, id);
			new AbilityEdition(player, type, id, configKey).open();
			player.sendMessage(MMOItems.getPrefix() + "Successfully set the casting mode to " + ChatColor.GOLD + castMode.getName() + ChatColor.GRAY + ".");
			return true;
		}

		double value = 0;
		try {
			value = Double.parseDouble(message);
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + message + " is not a valid number!");
			return false;
		}

		config.set(id + ".ability." + configKey + "." + edited, value);
		type.saveConfigFile(config, id);
		new AbilityEdition(player, type, id, configKey).open();
		player.sendMessage(MMOItems.getPrefix() + ChatColor.GOLD + MMOUtils.caseOnWords(edited.replace("-", " ")) + ChatColor.GRAY + " successfully added.");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String id) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Abilities: " + ChatColor.GREEN + (config.getConfigurationSection(id).contains("ability") ? config.getConfigurationSection(id + ".ability").getKeys(false).size() : 0));
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to edit the item abilities.");
		return true;
	}
}
