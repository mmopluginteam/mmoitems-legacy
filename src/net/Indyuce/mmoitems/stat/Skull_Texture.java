package net.Indyuce.mmoitems.stat;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;

public class Skull_Texture extends ItemStat {
	public Skull_Texture() {
		super(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "Skull Texture", new String[] { "The head texture &nvalue&7.", "Can be found on heads database sites." }, "skull-texture", new String[] { "all" }, StatType.STRING, dm(Material.SKULL_ITEM, 3));
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		return apply(item, config.getString("skull-texture"), MMOItems.getItemUUID(item.getItemType(), item.getItemId()));
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		String value = (String) values[0];
		UUID uuid = (UUID) values[1];

		if (item.getType() == Material.SKULL_ITEM && item.getDurability() == (short) 3) {
			GameProfile profile = new GameProfile(uuid, null);
			profile.getProperties().put("textures", new Property("textures", value));
			item.setGameProfile(profile);
		}
		return true;
	}
}
