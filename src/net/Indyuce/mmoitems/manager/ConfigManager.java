package net.Indyuce.mmoitems.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.ConfigData;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.Ability.CastingMode;
import net.Indyuce.mmoitems.api.CustomItem;
import net.Indyuce.mmoitems.api.DurabilityState;
import net.Indyuce.mmoitems.api.ItemSet;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.StaffSpirit;

public class ConfigManager {

	// must be updated each time a new language file is needed
	private String[] fileNames = { "abilities", "lore-format", "messages", "potion-effects", "stats", "items", "staff-spirits" };

	// must be updated each time a new language is added
	private String[] languages = { "french", "chinese", "spanish", "russian" };

	private FileConfiguration abilities;
	private FileConfiguration items;
	private FileConfiguration loreFormat;
	private FileConfiguration messages;
	private FileConfiguration potionEffects;
	private FileConfiguration stats;
	private FileConfiguration staffSpirits;
	private FileConfiguration namePlaceholders;
	private FileConfiguration tiers;
	private FileConfiguration durabilityStatesConfig;
	private FileConfiguration itemSetsConfig;
	private FileConfiguration uuids;

	// cached config options
	public boolean abilityPlayerDamage;
	public String healIndicatorFormat, damageIndicatorFormat;
	public DecimalFormat healIndicatorDecimalFormat, damageIndicatorDecimalFormat;
	public String abilitySplitter;

	private Map<String, DurabilityState> durabilityStates = new HashMap<>();
	private Map<String, ItemSet> itemSets = new HashMap<>();

	private static final Random random = new Random();

	// try to setup non existing languages
	public ConfigManager() {
		File mainFolder = new File(MMOItems.plugin.getDataFolder() + "/language");
		if (!mainFolder.exists())
			mainFolder.mkdir();

		File itemFolder = new File(MMOItems.plugin.getDataFolder() + "/item");
		if (!itemFolder.exists())
			itemFolder.mkdir();

		File dynamicFolder = new File(MMOItems.plugin.getDataFolder() + "/dynamic");
		if (!dynamicFolder.exists())
			dynamicFolder.mkdir();

		for (String language : languages) {
			File languageFolder = new File(MMOItems.plugin.getDataFolder() + "/language/" + language);
			if (!languageFolder.exists())
				languageFolder.mkdir();

			for (String fileName : fileNames)
				if (!new File(MMOItems.plugin.getDataFolder() + "/language/" + language, fileName + ".yml").exists()) {
					try {
						Files.copy(MMOItems.plugin.getResource("language/" + language + "/" + fileName + ".yml"), new File(MMOItems.plugin.getDataFolder() + "/language/" + language, fileName + ".yml").getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}

		// load files with default configuration
		for (ConfigFile file : ConfigFile.values())
			file.checkFile();

		FileConfiguration items = ConfigData.getCD(MMOItems.plugin, "/language", "items");
		for (CustomItem item : CustomItem.values()) {
			if (!items.contains(item.name())) {
				items.set(item.name() + ".name", item.getName());
				items.set(item.name() + ".lore", item.getLore());
			}
			item.update(items);
		}
		ConfigData.saveCD(MMOItems.plugin, items, "/language", "items");

		FileConfiguration messages = ConfigData.getCD(MMOItems.plugin, "/language", "messages");
		for (Message message : Message.values()) {
			String path = message.name().toLowerCase().replace("_", "-");
			if (!messages.contains(path))
				messages.set(path, message.getDefault());
		}
		ConfigData.saveCD(MMOItems.plugin, messages, "/language", "messages");

		FileConfiguration abilities = ConfigData.getCD(MMOItems.plugin, "/language", "abilities");
		for (Ability ability : MMOItems.plugin.getAbilities().getAll()) {
			String path = ability.getLowerCaseID();
			if (!abilities.getKeys(true).contains("ability." + path))
				abilities.set("ability." + path, ability.getName());

			for (String modifier : ability.getModifiers())
				if (!abilities.getKeys(true).contains("modifier." + modifier))
					abilities.set("modifier." + modifier, MMOUtils.caseOnWords(modifier.replace("-", " ")));
		}
		for (CastingMode mode : CastingMode.values())
			if (!abilities.contains("cast-mode." + mode.getLowerCaseID()))
				abilities.set("cast-mode." + mode.getLowerCaseID(), mode.getName());
		ConfigData.saveCD(MMOItems.plugin, abilities, "/language", "abilities");

		FileConfiguration potionEffects = ConfigData.getCD(MMOItems.plugin, "/language", "potion-effects");
		for (PotionEffectType effect : PotionEffectType.values()) {
			if (effect == null)
				continue;

			String path = effect.getName().toLowerCase().replace("_", "-");
			if (!potionEffects.contains(path))
				potionEffects.set(path, MMOUtils.caseOnWords(effect.getName().toLowerCase().replace("_", " ")));
		}
		ConfigData.saveCD(MMOItems.plugin, potionEffects, "/language", "potion-effects");

		FileConfiguration staffSpirits = ConfigData.getCD(MMOItems.plugin, "/language", "staff-spirits");
		for (StaffSpirit spirit : StaffSpirit.values()) {
			String path = spirit.name().toLowerCase().replace("_", "-");
			if (!staffSpirits.contains(path))
				staffSpirits.set(path, "&7" + SpecialChar.listSquare + " " + spirit.getDefaultName());
		}
		ConfigData.saveCD(MMOItems.plugin, staffSpirits, "/language", "staff-spirits");

		/*
		 * only load config files after they have been initialized (above) so
		 * they do not crash the first time they generate and so we do not have
		 * to restart the server
		 */
		reload();

		/*
		 * these config files must not be reloaded when using /mi reload this is
		 * why it is not added to the #reloadConfigFiles() method
		 */
		uuids = ConfigData.getCD(MMOItems.plugin, "/dynamic", "uuids");

		/*
		 * calculate how many sections are missing and send a message to the
		 * console if there's any, and then paste them into the
		 * /dynamic/missing-sections.yml file so you can know what's missing
		 */
		int missingConfigSections = getMissingConfigSections();
		if (missingConfigSections > 0)
			MMOItems.plugin.getLogger().log(Level.WARNING, "\u001B[31mWarning! Your current config.yml is missing " + missingConfigSections + " config section" + (missingConfigSections > 1 ? "s" : "") + ". Check dynamic/missing-sections.yml for more info.\u001B[37m");
	}

	private int getMissingConfigSections() {
		try {
			Files.copy(MMOItems.plugin.getResource("config.yml"), new File(MMOItems.plugin.getDataFolder() + "/dynamic", "missing-sections.yml").getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}

		FileConfiguration referenceConfig = ConfigData.getCD(MMOItems.plugin, "/dynamic", "missing-sections");
		Set<String> currentKeys = MMOItems.plugin.getConfig().getKeys(true);
		Set<String> reference = referenceConfig.getKeys(true);
		List<String> missingSections = new ArrayList<>();

		for (String key : reference)
			if (!currentKeys.contains(key))
				missingSections.add(key);

		FileConfiguration missing = new YamlConfiguration();
		for (String key : missingSections)
			missing.set(key, referenceConfig.get(key));
		ConfigData.saveCD(MMOItems.plugin, missing, "/dynamic", "missing-sections");

		return missingSections.size();
	}

	public void reload() {
		MMOItems.plugin.reloadConfig();

		abilities = ConfigData.getCD(MMOItems.plugin, "/language", "abilities");
		items = ConfigData.getCD(MMOItems.plugin, "/language", "items");
		loreFormat = ConfigData.getCD(MMOItems.plugin, "/language", "lore-format");
		messages = ConfigData.getCD(MMOItems.plugin, "/language", "messages");
		potionEffects = ConfigData.getCD(MMOItems.plugin, "/language", "potion-effects");
		stats = ConfigData.getCD(MMOItems.plugin, "/language", "stats");
		staffSpirits = ConfigData.getCD(MMOItems.plugin, "/language", "staff-spirits");
		namePlaceholders = ConfigData.getCD(MMOItems.plugin, "", "name-placeholders");
		durabilityStatesConfig = ConfigData.getCD(MMOItems.plugin, "", "use-states");
		itemSetsConfig = ConfigData.getCD(MMOItems.plugin, "", "item-sets");
		tiers = ConfigData.getCD(MMOItems.plugin, "", "tiers");

		/*
		 * reload cached config options for quicker access - these options are
		 * used in runnables, it is thus better to cache them
		 */
		abilityPlayerDamage = MMOItems.plugin.getConfig().getBoolean("ability-player-damage");
		healIndicatorFormat = ChatColor.translateAlternateColorCodes('&', MMOItems.plugin.getConfig().getString("game-indicators.heal.format"));
		damageIndicatorFormat = ChatColor.translateAlternateColorCodes('&', MMOItems.plugin.getConfig().getString("game-indicators.damage.format"));
		healIndicatorDecimalFormat = new DecimalFormat(MMOItems.plugin.getConfig().getString("game-indicators.heal.decimal-format"));
		damageIndicatorDecimalFormat = new DecimalFormat(MMOItems.plugin.getConfig().getString("game-indicators.damage.decimal-format"));
		abilitySplitter = getStatFormat("ability-splitter");

		// reload durability states, cache them into a list
		durabilityStates.clear();
		for (String id : durabilityStatesConfig.getKeys(false))
			durabilityStates.put(id, new DurabilityState(durabilityStatesConfig.getConfigurationSection(id)));

		// reload item sets and cache them into a map
		itemSets.clear();
		for (String id : itemSetsConfig.getKeys(false))
			itemSets.put(id, new ItemSet(itemSetsConfig.getConfigurationSection(id)));

		for (CustomItem item : CustomItem.values())
			item.update(items);

	}

	public void saveConfigFiles() {
		ConfigData.saveCD(MMOItems.plugin, uuids, "/dynamic", "uuids");
	}

	public FileConfiguration getTiers() {
		return tiers;
	}

	public FileConfiguration getItemUUIDs() {
		return uuids;
	}

	public String getStatFormat(String path) {
		return ChatColor.translateAlternateColorCodes('&', stats.getString(path));
	}

	public String getMessage(String path) {
		return ChatColor.translateAlternateColorCodes('&', messages.getString(path));
	}

	public String getAbilityName(Ability ability) {
		return abilities.getString("ability." + ability.getLowerCaseID());
	}

	public String getCastingModeName(CastingMode mode) {
		return abilities.getString("cast-mode." + mode.getLowerCaseID());
	}

	public String getModifierName(String path) {
		return abilities.getString("modifier." + path);
	}

	public List<String> getDefaultLoreFormat() {
		return loreFormat.getStringList("lore-format");
	}

	public String getPotionEffectName(PotionEffectType type) {
		return potionEffects.getString(type.getName().toLowerCase().replace("_", "-"));
	}

	public String getNamePlaceholder(String path) {
		if (!namePlaceholders.contains(path))
			return null;

		List<String> possible = namePlaceholders.getStringList(path);
		return possible.get(random.nextInt(possible.size()));
	}

	public String getStaffSpiritName(StaffSpirit spirit) {
		return ChatColor.translateAlternateColorCodes('&', staffSpirits.getString(spirit.name().toLowerCase().replace("_", "-")));
	}

	public DurabilityState getDurabilityState(String id) {
		return durabilityStates.get(id);
	}

	public boolean hasDurabilityState(String id) {
		return durabilityStates.containsKey(id);
	}

	public Collection<DurabilityState> getDurabilityStates() {
		return durabilityStates.values();
	}

	public Collection<ItemSet> getItemSets() {
		return itemSets.values();
	}

	public ItemSet getItemSet(String id) {
		return itemSets.containsKey(id) ? itemSets.get(id) : null;
	}

	/*
	 * all config files that have a default configuration are stored here, they
	 * get copied into the plugin folder when the plugin enables
	 */
	public enum ConfigFile {

		// default general config files -> /MMOItems
		TIERS("tiers.yml", "", "tiers.yml"),
		ITEM_TYPES("item-types.yml", "", "item-types.yml"),
		DROPS("drops.yml", "", "drops.yml"),
		USE_STATES("use-states.yml", "", "use-states.yml"),
		ITEM_SETS("item-sets.yml", "", "item-sets.yml"),
		NAME_PLACEHOLDERS("name-placeholders.yml", "", "name-placeholders.yml"),

		// default language files -> /MMOItems/language
		LORE_FORMAT("lore-format.yml", "language", "lore-format.yml"),
		STATS("stats.yml", "language", "stats.yml"),
		READ_ME("read-me.txt", "language", "read-me.txt"),

		// default item config files -> /MMOItems/item
		ARMOR("item/armor.yml", "item", "armor.yml"),
		AXE("item/axe.yml", "item", "axe.yml"),
		BOW("item/bow.yml", "item", "bow.yml"),
		CATALYST("item/catalyst.yml", "item", "catalyst.yml"),
		CONSUMABLE("item/consumable.yml", "item", "consumable.yml"),
		DAGGER("item/dagger.yml", "item", "dagger.yml"),
		GEM_STONE("item/gem_stone.yml", "item", "gem_stone.yml"),
		GREATSTAFF("item/greatstaff.yml", "item", "greatstaff.yml"),
		GREATSWORD("item/greatsword.yml", "item", "greatsword.yml"),
		HALBERD("item/halberd.yml", "item", "halberd.yml"),
		LANCE("item/lance.yml", "item", "lance.yml"),
		MATERIAL("item/material.yml", "item", "material.yml"),
		MISCELLANEOUS("item/miscellaneous.yml", "item", "miscellaneous.yml"),
		SHIELD("item/shield.yml", "item", "shield.yml"),
		STAFF("item/staff.yml", "item", "staff.yml"),
		SWORD("item/sword.yml", "item", "sword.yml"),
		TOME("item/tome.yml", "item", "tome.yml"),
		TOOL("item/tool.yml", "item", "tool.yml"),
		WAND("item/wand.yml", "item", "wand.yml");

		private String resourceName;
		private File file;

		private ConfigFile(String resourceName, String folderPath, String fileName) {
			this.file = new File(MMOItems.plugin.getDataFolder() + (folderPath.equals("") ? "" : "/" + folderPath), fileName);
			this.resourceName = resourceName;
		}

		public File getFile() {
			return file;
		}

		public void checkFile() {
			if (!file.exists())
				try {
					Files.copy(MMOItems.plugin.getResource("default/" + resourceName), file.getAbsoluteFile().toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
