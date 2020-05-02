package net.Indyuce.mmoitems.comp.rpgplugin;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.robin.battlelevels.api.BattleLevelsAPI;
import me.robin.battlelevels.events.PlayerLevelUpEvent;
import net.Indyuce.mmoitems.api.PlayerData;

public class BattleLevelsHook implements RPGPlugin, Listener {
	@Override
	public double getMana(Player player) {
		return player.getFoodLevel();
	}

	@Override
	public void setMana(Player player, double value) {
		player.setFoodLevel((int) value);
	}

	@Override
	public int getLevel(Player player) {
		return BattleLevelsAPI.getLevel(player.getUniqueId());
	}

	@Override
	public String getClass(Player player) {
		return "";
	}

	@Override
	public double getStamina(Player player) {
		return 0;
	}

	@Override
	public void setStamina(Player player, double value) {
	}

	@Override
	public boolean canBeDamaged(Entity entity) {
		return true;
	}

	@Override
	public void refreshStats(PlayerData data) {
	}

	@EventHandler
	public void a(PlayerLevelUpEvent event) {
		PlayerData.get(event.getPlayer()).scheduleDelayedInventoryUpdate();
	}

	@Override
	public RPGProfile getProfile(PlayerData data) {
		RPGProfile profile = new RPGProfile(data);
		profile.setLevel(BattleLevelsAPI.getLevel(data.getUniqueId()));
		profile.setClass("");
		profile.setMana(data.getPlayer().getFoodLevel());
		profile.setStamina(0);
		return profile;
	}
}