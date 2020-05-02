package net.Indyuce.mmoitems.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Message;

public class SoulboundCommand implements CommandExecutor {
	@Override
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only for players.");
			return true;
		}

		if (!sender.hasPermission("mmoitems.soulbound")) {
			Message.NOT_ENOUGH_PERMS_COMMAND.format(ChatColor.RED).send(sender);
			return true;
		}

		Player player = (Player) sender;
		ItemStack item = player.getItemInHand();
		String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_SOULBOUND");
		if (tag.equals("")) {
			Message.SOULBOUND_COMMAND_NO.format(ChatColor.RED).send(player);
			return true;
		}

		String soulbound = Bukkit.getOfflinePlayer(UUID.fromString(tag)).getName();
		String level = MMOUtils.intToRoman((int) MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_SOULBOUND_LEVEL"));
		Message.SOULBOUND_COMMAND_INFO.format(ChatColor.YELLOW, "#player#", soulbound, "#level#", level).send(player);
		return true;
	}
}
