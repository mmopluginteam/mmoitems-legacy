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
import net.Indyuce.mmoitems.ParticleEffect.ParticleProperty;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ArrowParticlesEdition;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Arrow_Particles extends ItemStat {
	public Arrow_Particles() {
		super(new ItemStack(Material.STAINED_GLASS, 1, (short) 5), "Arrow Particles", new String[] { "Particles that display around", "the arrows your bow fires." }, "arrow-particles", new String[] { "bow", "greatbow", "crossbow" });
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		if (!config.getConfigurationSection("arrow-particles").contains("particle"))
			return true;

		ParticleEffect particle = null;
		String particleFormat = config.getString("arrow-particles.particle").toUpperCase().replace("-", "_").replace(" ", "_");
		try {
			particle = ParticleEffect.valueOf(particleFormat);
		} catch (Exception e) {
			item.error("Arrow Particles", particleFormat + " is not a valid particle name.");
		}

		String tag = particle.name() + "|" + config.getInt("arrow-particles.amount") + "|" + config.getDouble("arrow-particles.offset") + "|";
		if (particle.hasProperty(ParticleProperty.COLORABLE))
			tag += config.getInt("arrow-particles.color.red") + "|" + config.getInt("arrow-particles.color.green") + "|" + config.getInt("arrow-particles.color.blue");
		else
			tag += config.getDouble("arrow-particles.speed");

		item.addItemTag(new ItemTag("MMOITEMS_ARROW_PARTICLES", tag));
		return true;
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		return true;
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		new ArrowParticlesEdition(player, type, path).open();
		return true;
	}

	@Override
	public boolean chatListener(Type type, String id, Player player, FileConfiguration config, String message, Object... info) {
		String edited = (String) info[0];

		if (edited.equals("color")) {
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

			config.set(id + ".arrow-particles.color.red", red);
			config.set(id + ".arrow-particles.color.green", green);
			config.set(id + ".arrow-particles.color.blue", blue);
			type.saveConfigFile(config, id);
			new ArrowParticlesEdition(player, type, id).open();
			player.sendMessage(MMOItems.getPrefix() + "Particle color successfully set to " + ChatColor.translateAlternateColorCodes('&', "&c&l" + red + "&7 - &a&l" + green + "&7 - &9&l" + blue));
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

			config.set(id + ".arrow-particles.particle", particle.name());
			type.saveConfigFile(config, id);
			new ArrowParticlesEdition(player, type, id).open();
			player.sendMessage(MMOItems.getPrefix() + "Particle successfully set to " + ChatColor.GOLD + MMOUtils.caseOnWords(particle.name().toLowerCase().replace("_", " ")) + ChatColor.GRAY + ".");
			return true;
		}

		if (edited.equals("amount")) {
			int value = 0;
			try {
				value = Integer.parseInt(message);
			} catch (Exception e) {
				player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + message + " is not a valid number.");
				return false;
			}

			config.set(id + ".arrow-particles.amount", value);
			type.saveConfigFile(config, id);
			new ArrowParticlesEdition(player, type, id).open();
			player.sendMessage(MMOItems.getPrefix() + ChatColor.GOLD + "Amount" + ChatColor.GRAY + " set to " + ChatColor.GOLD + value + ChatColor.GRAY + ".");
			return true;
		}

		// offset & speed
		double value = 0;
		try {
			value = Double.parseDouble(message);
		} catch (Exception e) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + message + " is not a valid number.");
			return false;
		}

		config.set(id + ".arrow-particles." + edited, value);
		type.saveConfigFile(config, id);
		new ArrowParticlesEdition(player, type, id).open();
		player.sendMessage(MMOItems.getPrefix() + ChatColor.GOLD + MMOUtils.caseOnWords(edited.replace("-", " ")) + ChatColor.GRAY + " set to " + ChatColor.GOLD + value + ChatColor.GRAY + ".");
		return true;
	}

	@Override
	public boolean displayValue(List<String> lore, FileConfiguration config, String path) {
		lore.add("");
		lore.add(ChatColor.GRAY + "Current Value:");

		try {
			ParticleEffect particle = ParticleEffect.valueOf(config.getString(path + ".arrow-particles.particle").toUpperCase().replace("-", "_").replace(" ", "_"));
			lore.add(ChatColor.GRAY + "* Particle: " + ChatColor.GOLD + MMOUtils.caseOnWords(particle.name().replace("_", " ").toLowerCase()));
			lore.add(ChatColor.GRAY + "* Amount: " + ChatColor.WHITE + config.getInt(path + ".arrow-particles.amount"));
			lore.add(ChatColor.GRAY + "* Offset: " + ChatColor.WHITE + config.getDouble(path + ".arrow-particles.offset"));
			lore.add("");
			if (particle.hasProperty(ParticleProperty.COLORABLE)) {
				double red = config.getDouble(path + ".arrow-particles.red"), green = config.getDouble(path + ".arrow-particles.green"), blue = config.getDouble(path + ".arrow-particles.blue");
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7* Color: &c&l" + red + "&7 - &a&l" + green + "&7 - &9&l" + blue));
			} else
				lore.add(ChatColor.GRAY + "* Speed: " + ChatColor.WHITE + config.getDouble(path + ".arrow-particles.speed"));
		} catch (Exception e) {
			lore.add(ChatColor.RED + "No particle selected.");
			lore.add(ChatColor.RED + "Click to setup.");
		}

		lore.add("");
		lore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to edit.");
		return true;
	}
}
