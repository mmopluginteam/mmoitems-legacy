package net.Indyuce.mmoitems.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.ConfigData;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.manager.TypeManager;

public class Type {

	// slashing
	public static final Type SWORD = new Type(TypeSet.SLASHING, "SWORD", new ItemStack(Material.IRON_SWORD), "Sword", true, true, true, "mainhand");

	// piercing
	public static final Type DAGGER = new Type(TypeSet.PIERCING, "DAGGER", new ItemStack(Material.IRON_SWORD), "Dagger", true, true, true, "mainhand");
	public static final Type SPEAR = new Type(TypeSet.PIERCING, "SPEAR", new ItemStack(Material.STICK), "Spear", true, true, true, "mainhand");

	// blunt
	public static final Type HAMMER = new Type(TypeSet.BLUNT, "HAMMER", new ItemStack(Material.IRON_SPADE), "Hammer", true, true, true, "mainhand");
	public static final Type GAUNTLET = new Type(TypeSet.BLUNT, "GAUNTLET", new ItemStack(Material.IRON_BARDING), "Gauntlet", true, true, true, "mainhand");

	// range
	public static final Type WHIP = new Type(TypeSet.RANGE, "WHIP", new ItemStack(Material.LEASH), "Whip", true, false, true, "mainhand");
	public static final Type STAFF = new Type(TypeSet.RANGE, "STAFF", new ItemStack(Material.STICK), "Staff", true, false, true, "mainhand");
	public static final Type BOW = new Type(TypeSet.RANGE, "BOW", new ItemStack(Material.BOW), "Bow", true, false, false, "mainhand", "offhand");
	public static final Type CROSSBOW = new Type(TypeSet.RANGE, "CROSSBOW", new ItemStack(Material.WOOD_PICKAXE), "Crossbow", false, true, false, "mainhand", "offhand");
	public static final Type MUSKET = new Type(TypeSet.RANGE, "MUSKET", new ItemStack(Material.IRON_BARDING), "Musket", true, false, false, "mainhand", "offhand");
	public static final Type LUTE = new Type(TypeSet.RANGE, "LUTE", new ItemStack(Material.GOLD_BARDING), "Lute", true, false, true, "mainhand", "offhand");

	// offhand
	public static final Type CATALYST = new Type(TypeSet.OFFHAND, "CATALYST", new ItemStack(Material.DIAMOND), "Catalyst", false, false, true, "mainhand", "offhand");
	public static final Type OFF_CATALYST = new Type(TypeSet.OFFHAND, "OFF_CATALYST", new ItemStack(Material.DIAMOND), "Catalyst", false, false, true, "offhand");

	// extra
	public static final Type ARMOR = new Type(TypeSet.EXTRA, "ARMOR", new ItemStack(Material.GOLD_CHESTPLATE), "Armor", false, false, true, "head", "chest", "legs", "feet");
	public static final Type TOOL = new Type(TypeSet.EXTRA, "TOOL", new ItemStack(Material.FISHING_ROD), "Tool", false, false, true, "mainhand");
	public static final Type CONSUMABLE = new Type(TypeSet.EXTRA, "CONSUMABLE", new ItemStack(Material.CAKE), "Consumable", false, false, true, "mainhand");
	public static final Type MISCELLANEOUS = new Type(TypeSet.EXTRA, "MISCELLANEOUS", new ItemStack(Material.WATER_BUCKET), "Miscellaneous", false, false, true, "mainhand");
	public static final Type GEM_STONE = new Type(TypeSet.EXTRA, "GEM_STONE", new ItemStack(Material.EMERALD), "Gem Stone", false, false, true);

	private TypeSet set;
	private String name, id;

	/*
	 * the 'weapon' boolean is used for item type restrictions for gem stones to
	 * easily check if the item is a weapon. 'melee' defines if the item can
	 * actually be used during a melee entity attack, if it can't it usually has
	 * some unique attack effect. 'rightClickSpecial' defines what items need to
	 * be right/left clicked to cast a right-click ability. e.g bows need to be
	 * left clicked since the right click is used to fire arrows.
	 */
	private boolean weapon, melee, rightClickSpecial, valid = true;

	/*
	 * used to display the item in the item explorer and in the item recipes
	 * list in the advanved workbench. can also be edited using the config
	 * files.
	 */
	private ItemStack item;

	// slot for attributes
	private List<String> slots;

	/*
	 * any type can have a subtype which basically dictates what the item type
	 * does.
	 */
	private Type parent = null;

	public Type(TypeSet set, String id, ItemStack item, String name, boolean weapon, boolean melee, boolean rightClickSpecial, String... slots) {
		this.set = set;
		this.id = id.toUpperCase().replace("-", "_").replace(" ", "_");
		this.name = name;
		this.item = item;
		this.slots = new ArrayList<>(Arrays.asList(slots));

		this.weapon = weapon;
		this.melee = melee;
		this.rightClickSpecial = rightClickSpecial;
	}

	public Type(TypeManager manager, ConfigurationSection config) {
		id = config.getName();

		try {
			parent = manager.get(config.getString("parent").toUpperCase().replace("-", "_").replace(" ", "_"));
			set = parent.set;
			weapon = parent.weapon;
			melee = parent.melee;
			rightClickSpecial = parent.rightClickSpecial;
			slots = parent.slots;
			load(config);
		} catch (Exception e) {
			valid = false;
			return;
		}
	}

	public void load(ConfigurationSection config) {
		name = config.getString("name");
		item = read(config.getString("display"));
	}

	@Deprecated
	public String name() {
		return id;
	}

	public String getId() {
		return id;
	}

	public TypeSet getItemSet() {
		return set;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean isWeapon() {
		return weapon;
	}

	public boolean isMeleeWeapon() {
		return melee;
	}

	public boolean isSpecialActionOnRightClick() {
		return rightClickSpecial;
	}

	public String getName() {
		return name;
	}

	public boolean hasSlot(String slot) {
		return slots.contains(slot);
	}

	public void addSlot(String slot) {
		slots.add(slot);
	}

	public List<String> getSlots() {
		return slots;
	}

	public ItemStack getItem() {
		return item.clone();
	}

	public boolean isSubtype() {
		return parent != null;
	}

	public Type getParent() {
		return parent;
	}

	public boolean corresponds(Type type) {
		return equals(type) || (isSubtype() && getParent().equals(type));
	}

	public boolean corresponds(TypeSet set) {
		return getItemSet() == set;
	}

	public FileConfiguration getConfigFile() {
		return ConfigData.getCD(MMOItems.plugin, "/item", getId().toLowerCase());
	}

	/*
	 * this method saves the config file, changes the uuid of the corresponding
	 * mmoitem and re-generates it in the mmoitem database
	 */
	public void saveConfigFile(FileConfiguration config, String id) {
		if (id != null)
			MMOItems.getLanguage().getItemUUIDs().set(getId() + "." + id, MMOItems.generateRandomUUID(this, id).toString());
		ConfigData.saveCD(MMOItems.plugin, config, "/item", getId().toLowerCase());
	}

	public boolean canHaveStat(Stat stat) {
		if (isSubtype())
			return getParent().canHaveStat(stat);

		for (String s1 : stat.c().getCompatibleTypes()) {
			if (s1.equalsIgnoreCase("!" + getId()))
				return false;
			if (s1.equalsIgnoreCase(getId()) || s1.equalsIgnoreCase(set.name()) || s1.equalsIgnoreCase("all"))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof Type && ((Type) object).id.equals(id);
	}

	private ItemStack read(String str) {
		try {
			String[] split = str.split("\\:");

			ItemStack item = new ItemStack(Material.valueOf(split[0].toUpperCase().replace("-", "_").replace(" ", "_")), 1, (short) (split.length > 1 ? Integer.valueOf(split[1]) : 0));
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.values());
			item.setItemMeta(meta);

			return item;
		} catch (Exception e) {
			MMOItems.plugin.getLogger().log(Level.WARNING, "An error occured while reading the following type display: " + str);
			return new ItemStack(Material.BARRIER);
		}
	}

	public static Type get(ItemStack item) {
		String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_TYPE");
		return !tag.equals("") ? MMOItems.plugin.getTypes().get(tag) : null;
	}

	public static Type safeValueOf(String s) {
		try {
			return MMOItems.plugin.getTypes().get(s.toUpperCase().replace("-", "_").replaceAll("[^A-Z_]", ""));
		} catch (Exception e) {
			return null;
		}
	}
}
