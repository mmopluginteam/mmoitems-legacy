package net.Indyuce.mmoitems.stat;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.ParticleData.ParticleType;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ParticlesEdition;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Item_Particles extends ItemStat {
	public Item_Particles() {
		super(new ItemStack(Material.STAINED_GLASS, 1, (short) 6), "Item Particles", new String[] { "The particles displayed when", "holding/wearing your item.", "", ChatColor.BLUE + "A tutorial is available on the wiki." }, "item-particles", new String[] { "all" }, StatType.STRING);
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		new ParticlesEdition(player, type, path).open();
		return true;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		ParticleType particleType = null;
		try {
			particleType = ParticleType.valueOf(config.getString("item-particles.type").toUpperCase().replace("-", "_").replace(" ", "_"));
		} catch (Exception e1) {
			return true;
		}

		ParticleEffect particle = null;
		try {
			particle = ParticleEffect.valueOf(config.getString("item-particles.particle").toUpperCase().replace("-", "_").replace(" ", "_"));
		} catch (Exception e1) {
			return true;
		}

		String tag = particle.name() + "|" + particleType.name();

		for (String key : config.getConfigurationSection("item-particles").getKeys(false)) {
			if (key.equalsIgnoreCase("color")) {
				tag += "|color=" + config.getInt("item-particles.color.red") + "," + config.getInt("item-particles.color.green") + "," + config.getInt("item-particles.color.blue");
				continue;
			}

			if (!key.equalsIgnoreCase("particle") && !key.equalsIgnoreCase("type"))
				tag += "|" + key + "=" + config.getDouble("item-particles." + key);
		}

		item.addItemTag(new ItemTag("MMOITEMS_ITEM_PARTICLES", tag));
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Left click to setup the item particles.");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to reset the item particles.");
		return true;
	}

	@Override
	public boolean chatListener(Type type, String id, Player player, FileConfiguration config, String message, Object... info) {
		String edited = (String) info[0];

		if (edited.equals("particle-type")) {
			String format = message.toUpperCase().replace("-", "_").replace(" ", "_");
			ParticleType particleType;
			try {
				particleType = ParticleType.valueOf(format);
			} catch (Exception e1) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + format + " is not a valid particle type!");
				return false;
			}

			config.set(id + ".item-particles.type", particleType.name());
			type.saveConfigFile(config, id);
			new ParticlesEdition(player, type, id).open();
			player.sendMessage(MMOItems.getPrefix() + "Particle type successfully set to " + ChatColor.GOLD + particleType.getDefaultName() + ChatColor.GRAY + ".");
			return true;
		}

		if (edited.equals("particle-color")) {

			String[] split = message.split("\\ ");
			int red = 0, green = 0, blue = 0;

			try {
				red = Integer.parseInt(split[0]);
				green = Integer.parseInt(split[1]);
				blue = Integer.parseInt(split[2]);
			} catch (Exception e) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + "Make sure you enter 3 valid numbers.");
				return false;
			}

			config.set(id + ".item-particles.color.red", red);
			config.set(id + ".item-particles.color.green", green);
			config.set(id + ".item-particles.color.blue", blue);
			type.saveConfigFile(config, id);
			new ParticlesEdition(player, type, id).open();
			player.sendMessage(MMOItems.getPrefix() + "Particle color successfully set to " + ChatColor.RED + ChatColor.BOLD + red + ChatColor.GRAY + " - " + ChatColor.GREEN + ChatColor.BOLD + green + ChatColor.GRAY + " - " + ChatColor.BLUE + ChatColor.BOLD + blue + ChatColor.GRAY + ".");
			return true;
		}

		if (edited.equals("particle")) {
			String format = message.toUpperCase().replace("-", "_").replace(" ", "_");
			ParticleEffect particle;
			try {
				particle = ParticleEffect.valueOf(format);
			} catch (Exception e1) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + format + " is not a valid particle!");
				return false;
			}

			config.set(id + ".item-particles.particle", particle.name());
			type.saveConfigFile(config, id);
			new ParticlesEdition(player, type, id).open();
			player.sendMessage(MMOItems.getPrefix() + "Particle successfully set to " + ChatColor.GOLD + MMOUtils.caseOnWords(particle.name().toLowerCase().replace("_", " ")) + ChatColor.GRAY + ".");
			return true;
		}

		double value = 0;
		try {
			value = Double.parseDouble(message);
		} catch (Exception e) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + message + " is not a valid number.");
			return false;
		}

		config.set(id + ".item-particles." + edited, value);
		type.saveConfigFile(config, id);
		new ParticlesEdition(player, type, id).open();
		player.sendMessage(MMOItems.getPrefix() + ChatColor.GOLD + MMOUtils.caseOnWords(edited.replace("-", " ")) + ChatColor.GRAY + " set to " + ChatColor.GOLD + value + ChatColor.GRAY + ".");
		return true;
	}
}
