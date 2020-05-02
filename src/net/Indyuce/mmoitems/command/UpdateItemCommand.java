package net.Indyuce.mmoitems.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.edition.ItemUpdaterEdition;
import net.Indyuce.mmoitems.listener.ItemUpdater;

public class UpdateItemCommand implements CommandExecutor {
	@Override
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only for players.");
			return true;
		}

		if (!sender.hasPermission("mmoitems.update"))
			return true;

		Player player = (Player) sender;
		if (args.length < 1) {
			ItemStack item = player.getItemInHand();

			// check type
			Type type = Type.get(item);
			if (type == null) {
				Message.CANT_UPDATE_ITEM.format(ChatColor.RED).send(sender);
				return true;
			}

			// check if new item exists
			ItemStack newItem = ItemUpdater.getUpdated(item);
			if (newItem == null) {
				Message.CANT_UPDATE_ITEM.format(ChatColor.RED).send(sender);
				return true;
			}
			if (newItem.getType() == Material.AIR) {
				Message.CANT_UPDATE_ITEM.format(ChatColor.RED).send(sender);
				return true;
			}

			// update
			player.setItemInHand(newItem);
			Message.UPDATE_ITEM.format(ChatColor.YELLOW).send(sender);
			return true;
		}

		// toggles on/off item updater
		if (!player.hasPermission("mmoitems.op")) {
			Message.NOT_ENOUGH_PERMS_COMMAND.format(ChatColor.RED).send(sender);
			return true;
		}
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Usage: /updateitem <type> <id> or /updateitem");
			return true;
		}

		// check type
		Type type = null;
		try {
			type = MMOItems.plugin.getTypes().get(args[0].replace("-", "_").toUpperCase());
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "This is not a valid item type.");
			return true;
		}

		// check if item exists
		String id = args[1].toUpperCase().replace("-", "_");
		ItemStack newItem = MMOItems.getItem(type, id);
		if (newItem == null) {
			player.sendMessage(ChatColor.RED + "This item does not exist or has issues loading.");
			return true;
		}
		if (newItem.getType() == Material.AIR) {
			player.sendMessage(ChatColor.RED + "This item does not exist or has issues loading.");
			return true;
		}

		new ItemUpdaterEdition(player, type, id, newItem).open();
		return true;
	}
}
