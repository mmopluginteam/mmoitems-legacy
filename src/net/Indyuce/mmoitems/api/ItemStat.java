package net.Indyuce.mmoitems.api;

import java.util.ArrayList;
import java.util.Arrays;
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
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;
import net.Indyuce.mmoitems.version.nms.Attribute;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class ItemStat {
	private String name, path;
	private ItemStack item;
	private StatType statType;

	private String[] lore, compatibleTypes;
	private List<DMaterial> compatibleMaterials;

	// can be enabled or not
	// depending on server version
	private boolean enabled = true;

	public ItemStat(String name, String[] lore, String path, String[] types) {
		this(new ItemStack(Material.BARRIER), name, lore, path, types, null);
	}

	public ItemStat(ItemStack item, String name, String[] lore, String path, String[] types) {
		this(item, name, lore, path, types, null);
	}

	public ItemStat(ItemStack item, String name, String[] lore, String path, String[] types, StatType type) {
		this(item, name, lore, path, types, type, null);
	}

	public ItemStat(ItemStack item, String name, String[] lore, String path, String[] types, StatType type, DMaterial... dmaterials) {
		this.item = item;
		this.lore = lore == null ? new String[0] : lore;
		this.compatibleTypes = types == null ? new String[0] : types;
		this.statType = type;
		this.path = path;
		this.name = name;
		this.compatibleMaterials = dmaterials == null ? new ArrayList<>() : Arrays.asList(dmaterials);
	}

	// applies a stat depending on a ConfigurationSection
	public boolean readStatInfo(MMOItem mmoitem, ConfigurationSection config) {
		if (statType == StatType.DOUBLE) {
			double value = MMOUtils.randomValue(config.getString(path));
			mmoitem.addItemTag(new ItemTag("MMOITEMS_" + path.toUpperCase().replace("-", "_"), value));
			mmoitem.insertInLore(path, format(value, "#", new StatFormat("##").format(value)));
		}
		if (statType == StatType.STRING) {
			String value = config.getString(path);
			mmoitem.addItemTag(new ItemTag("MMOITEMS_" + path.toUpperCase().replace("-", "_"), value));
			mmoitem.insertInLore(path, value);
		}
		if (statType == StatType.BOOLEAN)
			if (config.getBoolean(path)) {
				mmoitem.addItemTag(new ItemTag("MMOITEMS_" + path.toUpperCase().replace("-", "_"), true));
				mmoitem.insertInLore(path, translate());
			}

		return true;
	}

	// applies a stat to an ItemStack
	public boolean apply(MMOItem item, Object... values) {
		return true;
	}

	// when clicking the stat item in the edition GUI
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String id, Stat stat) {
		FileConfiguration config = type.getConfigFile();
		if (statType == StatType.STRING) {
			if (event.getAction() == InventoryAction.PICKUP_HALF) {
				config.set(id + "." + path, null);
				type.saveConfigFile(config, id);
				new ItemEdition(player, type, id).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed " + name + ChatColor.GRAY + ".");
				return true;
			}
			new StatEdition(inv, stat).enable("Write in the chat the text you want.");
		}
		if (statType == StatType.BOOLEAN) {
			config.set(id + "." + path, !config.getBoolean(id + "." + path));
			type.saveConfigFile(config, id);
			new ItemEdition(player, type, id).open();
		}
		if (statType == StatType.DOUBLE) {
			if (event.getAction() == InventoryAction.PICKUP_HALF) {
				config.set(id + "." + path, null);
				type.saveConfigFile(config, id);
				new ItemEdition(player, type, id).open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully removed " + name + ChatColor.GRAY + ".");
				return true;
			}
			new StatEdition(inv, stat).enable("Write in the chat the numeric value you want.", "Or write [MIN-VALUE]=[MAX-VALUE] to make the stat random.");
		}
		return true;
	}

	// when editing the stat with the stat
	public boolean chatListener(Type type, String id, Player player, FileConfiguration config, String message, Object... info) {
		if (statType == StatType.STRING) {
			config.set(id + "." + path, message);
			type.saveConfigFile(config, id);
			new ItemEdition(player, type, id).open();
			player.sendMessage(MMOItems.getPrefix() + name + " successfully changed to " + message + ".");
			return true;
		}
		if (statType == StatType.DOUBLE) {
			String[] split = message.split("\\=");
			double value = 0;
			double value1 = 0;
			try {
				value = Double.parseDouble(split[0]);
			} catch (Exception e1) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[0] + " is not a valid number.");
				return false;
			}

			// second value
			if (split.length > 1)
				try {
					value1 = Double.parseDouble(split[1]);
				} catch (Exception e1) {
					player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + split[1] + " is not a valid number.");
					return false;
				}

			// STRING if length == 2
			// DOUBLE if length == 1
			config.set(id + "." + path, split.length > 1 ? value + "=" + value1 : value);
			if (value == 0 && value1 == 0)
				config.set(id + "." + path, null);
			type.saveConfigFile(config, id);
			new ItemEdition(player, type, id).open();
			player.sendMessage(MMOItems.getPrefix() + name + " successfully changed to " + (value1 != 0 ? "{between " + value + " and " + value1 + "}" : "" + value) + ".");
			return true;
		}
		return true;
	}

	// displays the value on the edition GUI stat item
	public boolean displayValue(List<String> lore, FileConfiguration config, String id) {
		if (statType == StatType.DOUBLE) {
			lore.add("");
			String[] split = (config.contains(id + "." + path) ? config.getString(id + "." + path) : "=").split("\\=");
			String format = split.length > 1 ? "{" + tryParse(split[0]) + " to " + tryParse(split[1]) + "}" : "" + config.getDouble(id + "." + path);
			lore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN + format);
			lore.add("");
			lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Left click to change this value.");
			lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove this value.");
		}
		if (statType == StatType.STRING) {
			lore.add("");
			if (!config.getConfigurationSection(id).contains(path)) {
				lore.add(ChatColor.GRAY + "Current Value:");
				lore.add(ChatColor.RED + "No value.");
			} else {
				String value = ChatColor.translateAlternateColorCodes('&', config.getString(id + "." + path));
				value = value.length() > 40 ? value.substring(0, 40) + "..." : value;
				lore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN + value);
			}
			lore.add("");
			lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Left click to change this value.");
			lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to remove this value.");
		}
		if (statType == StatType.BOOLEAN) {
			lore.add("");
			lore.add(ChatColor.GRAY + "Current Value: " + (config.getBoolean(id + "." + path) ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
			lore.add("");
			lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to switch this value.");
		}
		return true;
	}

	private double tryParse(String input) {
		try {
			return Double.parseDouble(input);
		} catch (Exception e) {
			return 0;
		}
	}

	// when the stat is applied using a gem stone onto another item
	// only applies for numeric stats
	public void applyOntoItem(Type type, Stat stat, double gemValue, double weaponValue, List<Attribute> attributes, List<ItemTag> tags) {
		tags.add(new ItemTag("MMOITEMS_" + stat.name(), weaponValue + gemValue));
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public ItemStack getItem() {
		return item;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String[] getLore() {
		return lore;
	}

	public String[] getCompatibleTypes() {
		return compatibleTypes;
	}

	public boolean hasValidMaterial(ItemStack item) {
		if (compatibleMaterials.size() == 0)
			return true;

		for (DMaterial dm : compatibleMaterials)
			if (item.getType() == dm.getType() && (item.getDurability() == dm.getDurability() || dm.getDurability() == -1))
				return true;
		return false;
	}

	public StatType getStatType() {
		return statType;
	}

	public void setItemType(Material material) {
		item.setType(material);
	}

	public void disable() {
		enabled = false;
	}

	public void addCompatibleMaterial(DMaterial... values) {
		for (DMaterial dm : values)
			compatibleMaterials.add(dm);
	}

	protected static DMaterial dm(Material m, int d) {
		return new DMaterial(m, d);
	}

	protected static DMaterial dm(Material m) {
		return new DMaterial(m, -1);
	}

	/*
	 * formats the item stat before going into the item lore. the <plus>
	 * placeholder allows to display + only if the stat is positive, by
	 * displayed a plus/minus in the item lore, the player can understand if
	 * it's a bonus or a malus
	 */
	public String format(double value, String... replace) {
		String format = translate().replace("<plus>", value < 0 ? "" : "+");
		for (int j = 0; j < replace.length; j += 2)
			format = format.replace(replace[j], replace[j + 1]);
		return format;
	}

	public String translate() {
		return translate(path);
	}

	/*
	 * this static method allow to get the translation from stats.yml even
	 * though there are no stats that correspond to a specific string (e.g
	 * restore-health)
	 */
	public static String translate(String path) {
		return MMOItems.getLanguage().getStatFormat(path);
	}

	public enum StatType {
		DOUBLE,
		STRING,
		BOOLEAN;
	}
}