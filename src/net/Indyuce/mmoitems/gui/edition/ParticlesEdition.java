package net.Indyuce.mmoitems.gui.edition;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.ParticleEffect.ParticleProperty;
import net.Indyuce.mmoitems.api.ParticleData.ParticleType;
import net.Indyuce.mmoitems.api.SpecialChar;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class ParticlesEdition extends PluginInventory {
	public ParticlesEdition(Player player, Type type, String id) {
		super(player, type, id, 1);
	}

	@Override
	public Inventory getInventory() {
		Inventory inv = Bukkit.createInventory(this, 54, ChatColor.UNDERLINE + "Particles E.: " + id);
		int[] slots = { 37, 38, 39, 40, 41, 42, 43 };
		int n = 0;

		FileConfiguration config = type.getConfigFile();

		ParticleType particleType = null;
		try {
			particleType = ParticleType.valueOf(config.getString(id + ".item-particles.type"));
		} catch (Exception e) {
		}

		ItemStack particleTypeItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 6);
		ItemMeta particleTypeItemMeta = particleTypeItem.getItemMeta();
		particleTypeItemMeta.setDisplayName(ChatColor.GREEN + "Particle Pattern");
		List<String> particleTypeItemLore = new ArrayList<String>();
		particleTypeItemLore.add(ChatColor.GRAY + "The particle pattern defines how");
		particleTypeItemLore.add(ChatColor.GRAY + "particles behave, what pattern they follow");
		particleTypeItemLore.add(ChatColor.GRAY + "when displayed or what shape they form.");
		particleTypeItemLore.add("");
		particleTypeItemLore.add(ChatColor.GRAY + "Current Value: " + (particleType == null ? ChatColor.RED + "No type selected." : ChatColor.GOLD + particleType.getDefaultName()));
		if (particleType != null)
			particleTypeItemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + ChatColor.translateAlternateColorCodes('&', particleType.getDescription()));
		particleTypeItemLore.add("");
		particleTypeItemLore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to change this value.");
		particleTypeItemLore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to reset this value.");
		particleTypeItemMeta.setLore(particleTypeItemLore);
		particleTypeItem.setItemMeta(particleTypeItemMeta);

		ParticleEffect particle = null;
		try {
			particle = ParticleEffect.valueOf(config.getString(id + ".item-particles.particle"));
		} catch (Exception e) {
		}

		ItemStack particleItem = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta particleItemMeta = particleItem.getItemMeta();
		particleItemMeta.setDisplayName(ChatColor.GREEN + "Particle");
		List<String> particleItemLore = new ArrayList<String>();
		particleItemLore.add(ChatColor.GRAY + "Defines what particle is used");
		particleItemLore.add(ChatColor.GRAY + "in the particle effect.");
		particleItemLore.add("");
		particleItemLore.add(ChatColor.GRAY + "Current Value: " + (particle == null ? ChatColor.RED + "No particle selected." : ChatColor.GOLD + MMOUtils.caseOnWords(particle.name().toLowerCase().replace("_", " "))));
		particleItemLore.add("");
		particleItemLore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to change this value.");
		particleItemLore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to reset this value.");
		particleItemMeta.setLore(particleItemLore);
		particleItem.setItemMeta(particleItemMeta);

		if (particleType != null) {
			ConfigurationSection psection = config.getConfigurationSection(id + ".item-particles");
			for (String modifier : particleType.getModifiers()) {
				ItemStack modifierItem = new ItemStack(Material.INK_SACK, 1, (short) 8);
				ItemMeta modifierItemMeta = modifierItem.getItemMeta();
				modifierItemMeta.setDisplayName(ChatColor.GREEN + MMOUtils.caseOnWords(modifier.toLowerCase().replace("-", " ")));
				List<String> modifierItemLore = new ArrayList<String>();
				modifierItemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "This is a pattern modifier.");
				modifierItemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "Changing this value will slightly");
				modifierItemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "customize the particle pattern.");
				modifierItemLore.add("");
				modifierItemLore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + (psection.contains(modifier) ? psection.getDouble(modifier) : particleType.getModifier(modifier)));
				modifierItemMeta.setLore(modifierItemLore);
				modifierItem.setItemMeta(modifierItemMeta);

				modifierItem = MMOItems.plugin.getNMS().addTag(modifierItem, new ItemTag("patternModifierId", modifier));

				inv.setItem(slots[n++], modifierItem);
			}
		}

		if (particle != null)
			if (particle.hasProperty(ParticleProperty.COLORABLE)) {
				int red = config.getInt(id + ".item-particles.color.red");
				int green = config.getInt(id + ".item-particles.color.green");
				int blue = config.getInt(id + ".item-particles.color.blue");

				ItemStack colorItem = new ItemStack(Material.INK_SACK, 1, (short) 1);
				ItemMeta colorItemMeta = colorItem.getItemMeta();
				colorItemMeta.setDisplayName(ChatColor.GREEN + "Particle Color");
				List<String> colorItemLore = new ArrayList<String>();
				colorItemLore.add(ChatColor.GRAY + "The RGB color of your particle.");
				colorItemLore.add("");
				colorItemLore.add(ChatColor.GRAY + "Current Value (R-G-B):");
				colorItemLore.add("" + ChatColor.RED + ChatColor.BOLD + red + ChatColor.GRAY + " - " + ChatColor.GREEN + ChatColor.BOLD + green + ChatColor.GRAY + " - " + ChatColor.BLUE + ChatColor.BOLD + blue);
				colorItemLore.add("");
				colorItemLore.add(ChatColor.YELLOW + SpecialChar.listDash + " Click to change this value.");
				colorItemLore.add(ChatColor.YELLOW + SpecialChar.listDash + " Right click to reset this value.");
				colorItemMeta.setLore(colorItemLore);
				colorItem.setItemMeta(colorItemMeta);

				inv.setItem(25, colorItem);
			}

		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(ChatColor.RED + "- No Modifier -");
		glass.setItemMeta(glassMeta);

		while (n < slots.length)
			inv.setItem(slots[n++], glass);

		addEditionInventoryItems(inv, true);
		inv.setItem(21, particleTypeItem);
		inv.setItem(23, particleItem);

		return inv;
	}

	@Override
	public void whenClicked(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();

		event.setCancelled(true);
		if (event.getInventory() != event.getClickedInventory() || !MMOUtils.isPluginItem(item, false))
			return;

		if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, Stat.ITEM_PARTICLES, "particle").enable("Write in the chat the particle you want.");

			if (event.getAction() == InventoryAction.PICKUP_HALF) {
				FileConfiguration config = type.getConfigFile();
				if (config.getConfigurationSection(id).contains("item-particles") && config.getConfigurationSection(id + ".item-particles").contains("particle")) {
					config.set(id + ".item-particles.particle", null);
					type.saveConfigFile(config, id);
					open();
					player.sendMessage(MMOItems.getPrefix() + "Successfully reset the particle.");
				}
			}
		}

		if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle Color")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL)
				new StatEdition(this, Stat.ITEM_PARTICLES, "particle-color").enable("Write in the chat the RGB color you want.", ChatColor.AQUA + "Format: [RED] [GREEN] [BLUE]");

			if (event.getAction() == InventoryAction.PICKUP_HALF) {
				FileConfiguration config = type.getConfigFile();
				if (config.getConfigurationSection(id).contains("item-particles") && config.getConfigurationSection(id + ".item-particles").contains("color")) {
					config.set(id + ".item-particles.color", null);
					type.saveConfigFile(config, id);
					open();
					player.sendMessage(MMOItems.getPrefix() + "Successfully reset the particle color.");
				}
			}
		}

		if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle Pattern")) {
			if (event.getAction() == InventoryAction.PICKUP_ALL) {
				new StatEdition(this, Stat.ITEM_PARTICLES, "particle-type").enable("Write in the chat the particle type you want.");

				player.sendMessage("");
				player.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Available Particles Patterns");
				for (ParticleType type : ParticleType.values())
					player.sendMessage("* " + ChatColor.GREEN + type.name());
			}

			if (event.getAction() == InventoryAction.PICKUP_HALF) {
				FileConfiguration config = type.getConfigFile();
				if (config.getConfigurationSection(id).contains("item-particles") && config.getConfigurationSection(id + ".item-particles").contains("type")) {
					config.set(id + ".item-particles.type", null);

					// reset other modifiers
					for (String key : config.getConfigurationSection(id + ".item-particles").getKeys(false))
						if (!key.equals("particle"))
							config.set(id + ".item-particles." + key, null);

					type.saveConfigFile(config, id);
					open();
					player.sendMessage(MMOItems.getPrefix() + "Successfully reset the particle pattern.");
				}
			}
		}

		String tag = MMOItems.plugin.getNMS().getStringTag(item, "patternModifierId");
		if (tag.equals(""))
			return;

		if (event.getAction() == InventoryAction.PICKUP_ALL)
			new StatEdition(this, Stat.ITEM_PARTICLES, tag).enable("Write in the chat the value you want.");

		if (event.getAction() == InventoryAction.PICKUP_HALF) {
			FileConfiguration config = type.getConfigFile();
			if (config.getConfigurationSection(id).contains("item-particles") && config.getConfigurationSection(id + ".item-particles").contains(tag)) {
				config.set(id + ".item-particles." + tag, null);
				type.saveConfigFile(config, id);
				open();
				player.sendMessage(MMOItems.getPrefix() + "Successfully reset " + ChatColor.GOLD + tag + ChatColor.GRAY + ".");
			}
		}
	}
}