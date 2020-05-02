package net.Indyuce.mmoitems.stat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.MMOItem;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.ItemEdition;

public class MMaterial extends ItemStat {
	public MMaterial() {
		super(new ItemStack(Material.GRASS), "Material", new String[] { "Your item material." }, "material", new String[] { "all" }, StatType.STRING);
	}

	@Override
	public boolean guiClick(PluginInventory inv, InventoryClickEvent event, Player player, Type type, String path, Stat stat) {
		new StatEdition(inv, Stat.MATERIAL).enable("Write in the chat the material you want.");
		return true;
	}

	@Override
	public boolean chatListener(Type type, String path, Player player, FileConfiguration config, String msg, Object... info) {
		Material material = null;
		String materialMsg = msg.toUpperCase().replace("-", "_").replace(" ", "_");
		try {
			material = Material.valueOf(getModifiedMaterialName(materialMsg));
		} catch (Exception e1) {
			player.sendMessage(MMOItems.getPrefix() + ChatColor.RED + materialMsg + " is not a valid material!");
			player.sendMessage(MMOItems.getPrefix() + "All materials can be found here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
			return false;
		}

		config.set(path + ".material", material.name());
		type.saveConfigFile(config, path);
		new ItemEdition(player, type, path).open();
		player.sendMessage(MMOItems.getPrefix() + "Material successfully changed to " + material.name() + ".");
		return true;
	}

	private String getModifiedMaterialName(String name) {
		String format = name.replace(" ", "").replace("-", "").replace("_", "").toLowerCase();

		// easier
		name = format.equals("gunpowder") ? "SULPHUR" : name;

		// forbidden item materials
		name = format.equals("flowerpot") ? "FLOWER_POT_ITEM" : name;
		name = format.equals("potato") ? "POTATO_ITEM" : name;
		name = format.equals("carrot") ? "CARROT_ITEM" : name;
		return name;
	}

	@Override
	public boolean readStatInfo(MMOItem item, ConfigurationSection config) {
		try {
			return apply(item, Material.valueOf(config.getString("material").replace("-", "_").toUpperCase()));
		} catch (Exception e) {
			item.error("Material");
			return false;
		}
	}

	@Override
	public boolean apply(MMOItem item, Object... values) {
		item.setType((Material) values[0]);
		return true;
	}
}
