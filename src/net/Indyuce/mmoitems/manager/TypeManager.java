package net.Indyuce.mmoitems.manager;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.mmoitems.ConfigData;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;

public class TypeManager {
	private Map<String, Type> map = new LinkedHashMap<>();
	private FileConfiguration config;

	public TypeManager() {
		reload();
	}

	public void reload() {
		map.clear();
		addAll(Type.ARMOR, Type.BOW, Type.CATALYST, Type.CONSUMABLE, Type.CROSSBOW, Type.DAGGER, Type.GAUNTLET, Type.GEM_STONE, Type.HAMMER, Type.LUTE, Type.MISCELLANEOUS, Type.MUSKET, Type.OFF_CATALYST, Type.SPEAR, Type.STAFF, Type.SWORD, Type.TOOL, Type.WHIP);

		config = ConfigData.getCD(MMOItems.plugin, "", "item-types");

		/*
		 * register all other types.
		 */
		for (String id : config.getKeys(false)) {
			if (map.containsKey(id))
				continue;

			Type type = new Type(this, config.getConfigurationSection(id));
			if (!type.isValid()) {
				MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the type " + type.getId());
				continue;
			}
			add(type);
		}

		/*
		 * reload names & display items from types and generate corresponding
		 * config files.
		 */
		for (Type type : getAll()) {
			type.load(config.getConfigurationSection(type.getId()));
			ConfigData.setupCD(MMOItems.plugin, "/item", type.getId().toLowerCase());
			String path = type.getId().toLowerCase().replace("_", "-");
			if (!config.contains(path))
				config.set(path, type.getName());
		}
	}

	public void add(Type type) {
		map.put(type.getId(), type);
	}

	public void addAll(Type... types) {
		for (Type type : types)
			add(type);
	}

	public Type get(String id) {
		return map.get(id);
	}

	public Collection<Type> getAll() {
		return map.values();
	}
}
