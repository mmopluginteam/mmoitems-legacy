package net.Indyuce.mmoitems.api.item;

import java.util.Random;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.weapon.Gauntlet;
import net.Indyuce.mmoitems.api.item.weapon.Weapon;
import net.Indyuce.mmoitems.api.item.weapon.untargeted.Crossbow;
import net.Indyuce.mmoitems.api.item.weapon.untargeted.Lute;
import net.Indyuce.mmoitems.api.item.weapon.untargeted.Musket;
import net.Indyuce.mmoitems.api.item.weapon.untargeted.Staff;
import net.Indyuce.mmoitems.api.item.weapon.untargeted.Whip;
import net.Indyuce.mmoitems.comp.flags.FlagPlugin.CustomFlag;
import net.Indyuce.mmoitems.comp.rpgplugin.RPGProfile;

public class UseItem {
	protected Player player;
	protected PlayerData playerData;
	protected RPGProfile profile;

	protected ItemStack item;
	protected Type type;
	protected String id;

	protected static final Random random = new Random();

	public UseItem(Player player, ItemStack item, Type type) {
		this(PlayerData.get(player), item, type);
	}

	public UseItem(PlayerData playerData, ItemStack item, Type type) {
		this.player = playerData.getPlayer();
		this.playerData = playerData;
		this.profile = MMOItems.getRPG().getProfile(playerData);
		
		this.item = item;
		this.type = type;
		this.id = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_ID");
	}

	public Player getPlayer() {
		return player;
	}

	public ItemStack getItem() {
		return item;
	}

	public Type getType() {
		return type;
	}

	public String getID() {
		return id;
	}

	public boolean canBeUsed() {
		return profile.canUse(item, true);
	}

	public void executeCommands() {
		if (!MMOItems.plugin.getFlags().isFlagAllowed(player, CustomFlag.MI_COMMANDS))
			return;

		String stringTag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_COMMANDS");
		String id = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_ID");
		try {
			for (String s : stringTag.split(Pattern.quote("|nextCommand|"))) {
				if (s.equals(""))
					continue;

				String[] split = s.split(Pattern.quote("|cmdCd|"));
				String command = split[0];
				double cooldown = Double.parseDouble(split[1]);
				double delay = split.length > 2 ? Double.parseDouble(split[2]) : 0;

				String commandKey = id + "_" + command;
				if (playerData.canUseCommand(commandKey)) {
					playerData.applyCommandCooldown(commandKey, cooldown);
					executeCommand(MMOItems.plugin.getPlaceholderParser().parse(player, command), delay);
				}
			}
		} catch (Exception e) {
		}
	}

	private void executeCommand(String command, double delay) {
		new BukkitRunnable() {
			public void run() {
				if (MMOItems.plugin.getConfig().getBoolean("unsafe-op-commands") && !player.isOp()) {
					player.setOp(true);
					try {
						Bukkit.dispatchCommand(player, command);
					} catch (Exception e1) {
					}
					player.setOp(false);
					return;
				}
				Bukkit.dispatchCommand(player, command);
			}
		}.runTaskLater(MMOItems.plugin, (long) delay * 20);
	}

	public static UseItem getItem(Player player, ItemStack item, Type type) {
		if (type.corresponds(Type.CONSUMABLE))
			return new Consumable(player, item, type);
		if (type.corresponds(Type.GEM_STONE))
			return new GemStone(player, item, type);
		if (type.corresponds(Type.MUSKET))
			return new Musket(player, item, type);
		if (type.corresponds(Type.CROSSBOW))
			return new Crossbow(player, item, type);
		if (type.corresponds(Type.GAUNTLET))
			return new Gauntlet(player, item, type);
		if (type.corresponds(Type.WHIP))
			return new Whip(player, item, type);
		if (type.corresponds(Type.LUTE))
			return new Lute(player, item, type);
		if (type.corresponds(Type.STAFF))
			return new Staff(player, item, type);
		return type.isWeapon() ? new Weapon(player, item, type) : new UseItem(player, item, type);
	}
}
