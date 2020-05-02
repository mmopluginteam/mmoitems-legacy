package net.Indyuce.mmoitems.comp.rpgplugin;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import net.Indyuce.mmoitems.api.PlayerData;

public class DefaultHook implements RPGPlugin, Listener {
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
		return player.getLevel();
	}

	@Override
	public String getClass(Player player) {
		return "No Class";
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
	public void a(PlayerLevelChangeEvent event) {
		PlayerData.get(event.getPlayer()).scheduleDelayedInventoryUpdate();
	}

	@Override
	public RPGProfile getProfile(PlayerData data) {
		RPGProfile profile = new RPGProfile(data);
		profile.setLevel(data.getPlayer().getLevel());
		profile.setClass("");
		profile.setMana(data.getPlayer().getFoodLevel());
		profile.setStamina(0);
		return profile;
	}
}