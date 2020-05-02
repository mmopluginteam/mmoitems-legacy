package net.Indyuce.mmoitems.api;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

public class UpdaterData {

	// itemType.name() + itemId
	private String path;

	/*
	 * two UUIDs can be found : one on the itemStack in the nbttags, and one in
	 * the UpdaterData instance. if the two match, the item is up to date. if
	 * they don't match, the item needs to be updated
	 */
	private UUID uuid;

	private boolean keepLore, keepDurability, keepEnchants, keepName, keepGems;

	public static UpdaterData load(String path, UUID uuid, FileConfiguration config) {
		return new UpdaterData(path, uuid, config.getBoolean(path + ".lore"), config.getBoolean(path + ".enchants"), config.getBoolean(path + ".durability"), config.getBoolean(path + ".name"), config.getBoolean(path + ".gems"));
	}

	public UpdaterData(String path, UUID uuid) {
		this(path, uuid, false, false, false, false, false);
	}

	public UpdaterData(String path, UUID uuid, boolean keepLore, boolean keepEnchants, boolean keepDurability, boolean keepName, boolean keepGems) {
		this.uuid = uuid;
		this.path = path;

		this.keepLore = keepLore;
		this.keepEnchants = keepEnchants;
		this.keepDurability = keepDurability;
		this.keepName = keepName;
		this.keepGems = keepGems;
	}

	public void save(FileConfiguration config) {
		config.set(path + ".lore", keepLore);
		config.set(path + ".enchants", keepEnchants);
		config.set(path + ".durability", keepDurability);
		config.set(path + ".name", keepName);
		config.set(path + ".gems", keepGems);
		config.set(path + ".uuid", uuid.toString());
	}

	public UUID getUUID() {
		return uuid;
	}

	public boolean keepLore() {
		return keepLore;
	}

	public boolean keepDurability() {
		return keepDurability;
	}

	public boolean keepEnchants() {
		return keepEnchants;
	}

	public boolean keepName() {
		return keepName;
	}

	public boolean keepGems() {
		return keepGems;
	}

	public void setKeepLore(boolean value) {
		keepLore = value;
	}

	public void setKeepDurability(boolean value) {
		keepDurability = value;
	}

	public void setKeepEnchants(boolean value) {
		keepEnchants = value;
	}

	public void setKeepName(boolean value) {
		keepName = value;
	}

	public void setKeepGems(boolean value) {
		keepGems = value;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
}
