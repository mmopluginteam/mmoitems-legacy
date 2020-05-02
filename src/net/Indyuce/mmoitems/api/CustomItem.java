package net.Indyuce.mmoitems.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.Indyuce.mmoitems.MMOItems;

public enum CustomItem {
	UNIDENTIFIED_ITEM(new ItemStack(Material.BARRIER), ChatColor.DARK_RED, "&kUnidentified", "&e&oThis item is &c&o&nunidentified&e&o.", "&e&oI'll have to find a way to identify this."),
	TYPE_DISPLAY(new ItemStack(Material.BARRIER), ChatColor.GREEN, "#type# &8(Click to browse)", "&7There are &6#recipes#&7 available recipes."),
	RECIPE_LIST(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjMzNTk4NDM3ZTMxMzMyOWViMTQxYTEzZTkyZDliMDM0OWFhYmU1YzY0ODJhNWRkZTdiNzM3NTM2MzRhYmEifX19==", ChatColor.GREEN, "Advanced Recipes"),

	BACK(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==", ChatColor.GREEN, "Back"),
	NEXT_PAGE(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19", ChatColor.GREEN, "Next Page"),
	PREVIOUS_PAGE(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==", ChatColor.GREEN, "Previous Page"),

	NO_ITEM(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), ChatColor.RED, "- No Item -"),
	NO_TYPE(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), ChatColor.RED, "- No Type -"),
	NO_GEM_STONE(new ItemStack(Material.INK_SACK, 1, (short) 8), ChatColor.RED, "- Empty Gem Socket -"),

	;

	private ItemStack item;

	// item metadata
	private Material material;
	private short data;
	private ChatColor prefix;
	private String name;
	private String[] lore;

	// conditional metadata
	private String textureValue = null;

	private CustomItem(ItemStack item, String textureValue, ChatColor prefix, String name, String... lore) {
		this.material = item.getType();
		this.data = item.getDurability();
		this.textureValue = textureValue;
		this.prefix = prefix;
		this.name = name;
		this.lore = lore;

		updateItem();
	}

	private CustomItem(ItemStack item, ChatColor prefix, String name, String... lore) {
		this.material = item.getType();
		this.data = item.getDurability();
		this.prefix = prefix;
		this.name = name;
		this.lore = lore;

		updateItem();
	}

	public void update(FileConfiguration config) {
		this.name = config.getString(name() + ".name");
		this.lore = config.getStringList(name() + ".lore").toArray(new String[0]);
		updateItem();
	}

	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return Arrays.asList(lore);
	}

	public ItemStack getItem() {
		return item;
	}

	private void updateItem() {
		ItemStack item = new ItemStack(material, 1, data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(prefix + ChatColor.translateAlternateColorCodes('&', name));
		meta.addItemFlags(ItemFlag.values());

		if (material == Material.SKULL_ITEM && data == (short) 3 && textureValue != null) {
			GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
			gameProfile.getProperties().put("textures", new Property("textures", textureValue));
			try {
				Field profileField = meta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(meta, gameProfile);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				MMOItems.plugin.getLogger().log(Level.WARNING, "An error occured while trying to generate a custom textured skull");
			}
		}

		if (lore != null) {
			List<String> lore = new ArrayList<>();
			for (String s : this.lore)
				lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', s));
			meta.setLore(lore);
		}
		item.setItemMeta(meta);

		this.item = item;
	}

	// public class SkullItem extends ItemStack {
	// public static final String nextTexture =
	// "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19";
	// public static final String backTexture =
	// "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";
	//
	// /*
	// * uses a random uuid and not a generated uuid, can thus not be used to
	// * generate mmoitems. for some reason, this class cannot be used in the
	// * updateItem() method so it has to be copied/pasted.
	// */
	// public SkullItem(String textureValue) {
	// this(textureValue, null);
	// }
	//
	// public SkullItem(String textureValue, String displayName) {
	// super(Material.SKULL_ITEM, 1, (short) 3);
	//
	// ItemMeta meta = getItemMeta();
	// GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
	// gameProfile.getProperties().put("textures", new Property("textures",
	// textureValue));
	// try {
	// Field profileField = meta.getClass().getDeclaredField("profile");
	// profileField.setAccessible(true);
	// profileField.set(meta, gameProfile);
	// } catch (NoSuchFieldException | IllegalArgumentException |
	// IllegalAccessException e) {
	// MMOItems.plugin.getLogger().log(Level.WARNING, "An error occured while
	// trying to generate a custom textured skull");
	// }
	// if (displayName != null)
	// meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
	// displayName));
	//
	// setItemMeta(meta);
	// }
	// }
}
