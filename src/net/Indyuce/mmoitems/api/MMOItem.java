package net.Indyuce.mmoitems.api;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.version.nms.Attribute;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class MMOItem {

	// MMOItem data
	private String id;
	private Type type;

	/*
	 * the item itself since the MMOItem class no longer extends the ItemStack
	 * class. this item is used to store durability, type and is used by the
	 * class when generating the actual item
	 */
	private ItemStack item = new ItemStack(Material.BARRIER);

	/*
	 * item meta data is temporarily saved and accessed when generating the item
	 * so the itemMeta is created only when the item generates
	 */
	private String displayType = null, displayName = null;
	private int[] armorColor = null, potionColor = null;
	private GameProfile gameProfile = null;
	private Banner bannerBlockState = null;
	private List<ItemFlag> itemFlags = new ArrayList<>();
	private List<String> lore = MMOItems.getLanguage().getDefaultLoreFormat();
	private Map<Enchantment, Integer> enchants = new HashMap<>();

	// item tags & attributes that must be added
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private List<ItemTag> tags = new ArrayList<ItemTag>();

	public MMOItem(Type itemType, String itemId) {
		type = itemType;
		id = itemId;

		tags.add(new ItemTag("MMOITEMS_ITEM_TYPE", type.getId()));
		tags.add(new ItemTag("MMOITEMS_ITEM_ID", id));
		tags.add(new ItemTag("MMOITEMS_ITEM_UUID", MMOItems.getItemUUID(type, id).toString()));

		addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
	}

	public String getItemId() {
		return id;
	}

	public Type getItemType() {
		return type;
	}

	public void addItemFlag(ItemFlag... itemFlags) {
		for (ItemFlag itemFlag : itemFlags)
			this.itemFlags.add(itemFlag);
	}

	public void setDisplayName(String value) {
		displayName = value;
	}

	public void setDurability(int value) {
		setDurability((short) value);
	}

	public void setDurability(short value) {
		item.setDurability(value);
	}

	public short getDurability() {
		return item.getDurability();
	}

	public Material getType() {
		return item.getType();
	}

	public ItemMeta getItemMeta() {
		return item.getItemMeta();
	}

	public void setType(Material material) {
		item.setType(material);
	}

	public void setDisplayedType(String value) {
		displayType = value;
	}

	public void setGameProfile(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}

	public void setBannerBlockState(Banner banner) {
		bannerBlockState = banner;
	}

	public void setPotionColor(int red, int green, int blue) {
		potionColor = new int[] { red, green, blue };
	}

	public void setArmorColor(int red, int green, int blue) {
		armorColor = new int[] { red, green, blue };
	}

	public void addEnchantment(Enchantment enchant, int enchantLevel) {
		enchants.put(enchant, enchantLevel);
	}

	public void setEffects(PotionEffect[] potionEffects) {
		String stringTag = "";
		List<String> loreEffects = new ArrayList<String>();
		DecimalFormat durationFormat = new DecimalFormat("0.#");
		for (PotionEffect potionEffect : potionEffects) {
			String format = ItemStat.translate("effect").replace("#e", MMOItems.getLanguage().getPotionEffectName(potionEffect.getType()) + " " + MMOUtils.intToRoman(potionEffect.getAmplifier())).replace("#d", "" + durationFormat.format((double) potionEffect.getDuration() / 20));
			loreEffects.add(format);
			stringTag += (stringTag.length() > 0 ? ";" : "") + potionEffect.getType().getName() + ":" + potionEffect.getDuration() + ":" + potionEffect.getAmplifier();
		}
		insertInLore("effects", loreEffects);
		addItemTag(new ItemTag("MMOITEMS_EFFECTS", stringTag));
	}

	public void setShieldPattern(DyeColor baseColor, Pattern[] patterns) {
		BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
		Banner banner = (Banner) meta.getBlockState();

		banner.setBaseColor(baseColor);
		for (Pattern pattern : patterns)
			banner.addPattern(pattern);

		bannerBlockState = banner;
	}

	public void setCommands(StringValue[] valueCouples) {
		String stringTag = "";
		List<String> commands = new ArrayList<String>();
		for (StringValue valueCouple : valueCouples) {
			stringTag += (stringTag.length() > 0 ? "|nextCommand|" : "") + valueCouple.getName() + "|cmdCd|" + valueCouple.getValue() + (valueCouple.getExtraValue() > 0 ? "|cmdCd|" + valueCouple.getExtraValue() : "");
			commands.add(ItemStat.translate("command").replace("#cd", "" + valueCouple.getValue()).replace("#c", "/" + valueCouple.getName()).replace("#d", "" + valueCouple.getExtraValue()));
		}

		insertInLore("commands", commands);
		addItemTag(new ItemTag("MMOITEMS_COMMANDS", stringTag));
	}

	public void setPermanentPotionEffects(PotionEffect[] potionEffects) {
		String stringTag = "";
		List<String> loreEffects = new ArrayList<>();
		for (PotionEffect potionEffect : potionEffects) {
			loreEffects.add(ItemStat.translate("perm-effect").replace("#", MMOItems.getLanguage().getPotionEffectName(potionEffect.getType()) + " " + MMOUtils.intToRoman(potionEffect.getAmplifier())));
			stringTag += (stringTag.length() > 0 ? ";" : "") + potionEffect.getType().getName() + ":" + potionEffect.getAmplifier();
		}
		insertInLore("perm-effects", loreEffects);
		addItemTag(new ItemTag("MMOITEMS_PERM_EFFECTS", stringTag));
	}

	public void addElementStat(Element elementType, Element.StatType statType, double statValue) {
		String path = elementType.name().toLowerCase() + "-" + statType.name().toLowerCase();

		addItemTag(new ItemTag("MMOITEMS_" + elementType.name() + "_" + statType.name(), statValue));
		insertInLore(path, ItemStat.translate(path).replace("#", new StatFormat("##").format(statValue)));
	}

	public void error(String... args) {
		MMOItems.plugin.getLogger().log(Level.WARNING, type.getId() + "." + id + " - " + args[0]);
		for (int j = 1; j < args.length; j++)
			MMOItems.plugin.getLogger().log(Level.WARNING, args[j]);
	}

	public boolean canHaveStat(ConfigurationSection section, Stat stat) {
		return section.contains(stat.c().getPath()) && type.canHaveStat(stat);
	}

	public void insertInLore(String path, String... add) {
		int index = lore.indexOf("#" + path + "#");
		if (index < 0)
			return;

		for (int j = 0; j < add.length; j++)
			lore.add(index + 1, add[add.length - j - 1]);
		lore.remove(index);
	}

	public void insertInLore(String path, List<String> list) {
		int index = lore.indexOf("#" + path + "#");
		if (index < 0)
			return;

		Lists.reverse(list).forEach(string -> lore.add(index + 1, string));
		lore.remove(index);
	}

	public void addItemAttribute(Attribute... attributes) {
		for (Attribute attribute : attributes)
			this.attributes.add(attribute);
	}

	public void addItemTag(ItemTag... itemTags) {
		for (ItemTag itemTag : itemTags)
			tags.add(itemTag);
	}

	private void finishLore() {

		// loop backwards to remove all bars in one iteration only
		for (int j = 0; j < lore.size();) {
			int n = lore.size() - j - 1;

			// removed unused placeholders
			if (lore.get(n).startsWith("#")) {
				lore.remove(n);
				continue;
			}

			// remove useless lore strips
			if (lore.get(n).startsWith("{bar}") && (n < lore.size() - 1 ? (lore.get(n + 1).startsWith("{bar}") || lore.get(n + 1).startsWith("{superbar}")) : n == lore.size() - 1)) {
				lore.remove(n);
				continue;
			}

			j++;
		}

		// clear {bar}
		// add color
		for (int n = 0; n < lore.size(); n++)
			lore.set(n, ChatColor.translateAlternateColorCodes('&', lore.get(n).replace("{bar}", "").replace("{superbar}", "")));
	}

	public void applyStat(Stat stat, Object... values) {
		stat.c().apply(this, values);
	}

	public void applyStat(Stat stat, ConfigurationSection section) {
		stat.c().readStatInfo(this, section);
	}

	public ItemStack toItemStack() {
		ItemMeta meta = item.getItemMeta();

		// display name
		if (displayName != null)
			meta.setDisplayName(displayName);

		// item flags
		meta.addItemFlags(itemFlags.toArray(new ItemFlag[itemFlags.size()]));

		// item enchantments
		for (Enchantment enchant : enchants.keySet())
			meta.addEnchant(enchant, enchants.get(enchant), true);

		// shield pattern
		if (bannerBlockState != null)
			((BlockStateMeta) meta).setBlockState(bannerBlockState);

		// potion color
		if (potionColor != null)
			((PotionMeta) meta).setColor(Color.fromRGB(potionColor[0], potionColor[1], potionColor[2]));

		// rgb armor color
		if (armorColor != null)
			((LeatherArmorMeta) meta).setColor(Color.fromRGB(armorColor[0], armorColor[1], armorColor[2]));

		// skull texture
		if (gameProfile != null)
			try {
				Field profileField = meta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(meta, gameProfile);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				error("Skull Texture");
			}

		// lore
		if (type == Type.GEM_STONE)
			insertInLore("gem-stone-lore", ItemStat.translate("gem-stone-lore"));
		insertInLore("item-type", ItemStat.translate("item-type").replace("#", displayType != null ? displayType : type.getName()));
		finishLore();
		meta.setLore(lore);

		item.setItemMeta(meta);

		// add item attributes & tags
		item = MMOItems.plugin.getNMS().addTag(item, tags);
		item = MMOItems.plugin.getNMS().addAttribute(item, attributes);

		return item;
	}
}
