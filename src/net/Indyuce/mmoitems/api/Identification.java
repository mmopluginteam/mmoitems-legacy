package net.Indyuce.mmoitems.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Identification {
	private ItemStack item;

	public Identification(ItemStack item) {
		this.item = item;
	}

	// returns the default unidentified
	// item with modified type
	public ItemStack unidentify() {
		ItemStack unidentified = CustomItem.UNIDENTIFIED_ITEM.getItem().clone();
		unidentified.setType(item.getType());
		unidentified = MMOItems.plugin.getNMS().addTag(unidentified, new ItemTag("MMOITEMS_UNIDENTIFIED_ITEM", serialize(item)));
		return unidentified;
	}

	// the identified item is stored in the item nbttag
	// identifying the item replaces the item by the one saved
	public ItemStack identify() {
		return deserialize(MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_UNIDENTIFIED_ITEM"));
	}

	private ItemStack deserialize(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack item = (ItemStack) dataInput.readObject();
			dataInput.close();
			return item;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String serialize(ItemStack item) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeObject(item);
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
