package net.Indyuce.mmoitems.api;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Indyuce.mmoitems.MMOItems;

public enum Message {
	HANDS_TOO_CHARGED("You can't do anything, your hands are too charged."),
	SPELL_ON_COOLDOWN("#progress# &eYou must wait #left# second#s# before casting this spell."),
	ITEM_ON_COOLDOWN("This item is on cooldown!"),
	NOT_ENOUGH_PERMS_COMMAND("You don't have enough permissions."),
	ATTACK_BLOCKED("You just blocked #percent#% of the attack damage!"),
	ATTACK_DODGED("You just dodged an attack!"),
	ATTACK_PARRIED("You just parried an attack!"),
	NOT_ENOUGH_LEVELS("You don't have enough levels to use this item!"),
	SOULBOUND_RESTRICTION("This item is linked to another player, you can't use it!"),
	NOT_ENOUGH_PERMS("You don't have enough permissions to use this."),
	BROKEN_ITEM("This item is broken, you first need to repair it."),
	ITEM_BROKE("Your #item#&c broke."),
	ZERO_DURABILITY("Your item has no durability left. Repair it in order to use it again."),
	NOT_ENOUGH_MANA("You don't have enough mana!"),
	NOT_ENOUGH_STAMINA("You don't have enough stamina!"),
	WRONG_CLASS("You don't have the right class!"),
	SUCCESSFULLY_IDENTIFIED("You successfully identified #item#&e."),
	SUCCESSFULLY_DECONSTRUCTED("You successfully deconstructed #item#&e."),
	GEM_STONE_APPLIED("You successfully applied &f#gem#&e onto your &f#item#&e."),
	GEM_STONE_BROKE("Your gem stone &f#gem#&c broke while trying to apply it onto &f#item#&c."),
	REPAIRED_ITEM("You successfully repaired &f#item#&e for &f#amount# &euses."),
	GAVE_ITEM("&eGave &f#item#&e#amount# to &f#player#&e."),
	RECEIVED_ITEM("&eYou received &f#item#&e#amount#."),
	CANT_UPDATE_ITEM("Couldn't update your item."),
	UPDATE_ITEM("Successfully updated your item."),
	EMPTY_WORKBENCH_FIRST("Please empty the workbench first."),
	NOT_ENOUGH_PERMS_CRAFT("You don't have enough permissions to craft this item."),
	CANT_BIND_ITEM("This item is currently linked to #player# by a Lvl #level# soulbound. You will have to break this soulbound first."),
	NO_SOULBOUND("This item is not bound to anyone."),
	CANT_BIND_STACKED("You can't bind stacked items."),
	UNSUCCESSFUL_SOULBOUND("Your soulbound failed."),
	UNSUCCESSFUL_SOULBOUND_BREAK("You couldn't break the soulbound."),
	LOW_SOULBOUND_LEVEL("This item soulbound is Lvl #level#. You will need a higher soulbound level on your consumable to break this soulbound."),
	SUCCESSFULLY_BIND_ITEM("You successfully applied a Lvl &6#level# &esoulbound to your &6#item#&e."),
	SUCCESSFULLY_BREAK_BIND("You successfully broke the Lvl &6#level# &eitem soulbound!"),
	SOULBOUND_ITEM_LORE("&7//&4Linked to #player#//&4Lvl #level# Soulbound"),
	SOULBOUND_COMMAND_NO("This item is not bound to anyone."),
	SOULBOUND_COMMAND_INFO("Your item is bound to &6#player# &eby a Lvl &6#level# &esoulbound."),

	// gui
	ADVANCED_WORKBENCH("Advanced Workbench"),
	ADVANCED_RECIPES("Advanced Recipes"),
	GEM_STATS("Gem Stones"),
	CLICK_ADVANCED_RECIPE("#d Click to see its recipe."),;

	private String defaultMessage;
	private String messagePath;

	private Message(String defaultMessage) {
		this.defaultMessage = defaultMessage;
		this.messagePath = name().toLowerCase().replace("_", "-");
	}

	public String getDefault() {
		return defaultMessage;
	}

	public String getUpdated() {
		return MMOItems.getLanguage().getMessage(messagePath);
	}

	// toReplace length must be even
	public String formatRaw(ChatColor prefix, String... toReplace) {
		String message = prefix + getUpdated();
		for (int j = 0; j < toReplace.length; j += 2)
			message = message.replace(toReplace[j], toReplace[j + 1]);
		return message;
	}

	public PlayerMessage format(ChatColor prefix, String... toReplace) {
		return new PlayerMessage(formatRaw(prefix, toReplace));
	}

	public class PlayerMessage {
		private String message;

		// this class allows the plugin to send
		// only non-empty messages to the chat
		public PlayerMessage(String message) {
			this.message = message;
		}

		public void send(CommandSender sender) {
			if (!ChatColor.stripColor(message).equals(""))
				sender.sendMessage(message);
		}

		// send on action bar or chat
		public void send(Player player, String actionBarBooleanPath) {
			if (ChatColor.stripColor(message).equals(""))
				return;

			if (MMOItems.plugin.getConfig().getBoolean("action-bar-display." + actionBarBooleanPath))
				MMOItems.plugin.getNMS().sendActionBar(player, message);
			else
				player.sendMessage(message);
		}
	}
}