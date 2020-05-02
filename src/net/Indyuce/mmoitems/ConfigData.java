package net.Indyuce.mmoitems;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigData {
	public static void setupCD(Plugin plugin, String path, String name) {
		if (!new File(plugin.getDataFolder() + path).exists())
			new File(plugin.getDataFolder() + path).mkdir();

		File pfile = new File(plugin.getDataFolder() + path, name + ".yml");
		if (!pfile.exists())
			try {
				pfile.createNewFile();
			} catch (IOException e) {
				MMOItems.plugin.getLogger().severe(ChatColor.DARK_RED + "Could not create " + name + ".yml");
			}
	}

	public static FileConfiguration getCD(Plugin plugin, String path, String name) {
		return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + path, name + ".yml"));
	}

	public static void saveCD(Plugin plugin, FileConfiguration config, String path, String name) {
		try {
			config.save(new File(plugin.getDataFolder() + path, name + ".yml"));
		} catch (IOException e2) {
			MMOItems.plugin.getLogger().severe(ChatColor.DARK_RED + "Could not save " + name + ".yml");
		}
	}
}