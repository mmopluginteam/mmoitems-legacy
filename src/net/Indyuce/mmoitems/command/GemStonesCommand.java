package net.Indyuce.mmoitems.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Indyuce.mmoitems.gui.GemStoneList;

public class GemStonesCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only for players.");
			return true;
		}

		try {
			new GemStoneList((Player) sender).open();
		} catch (Exception e) {
			/*
			 * a bug can happen while not holding any item or when trying to
			 * read the gems of a pre-4.2 item
			 */
		}
		return true;
	}
}
