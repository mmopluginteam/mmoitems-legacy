package net.Indyuce.mmoitems.comp.rpgplugin;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.exceptions.McMMOPlayerNotFoundException;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelChangeEvent;

import net.Indyuce.mmoitems.api.PlayerData;

public class McMMOHook implements RPGPlugin, Listener {
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
		try {
			return ExperienceAPI.getPowerLevel(player);
		} catch (McMMOPlayerNotFoundException e) {
			return 0;
		}
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
	public void a(McMMOPlayerLevelChangeEvent event) {
		PlayerData.get(event.getPlayer()).scheduleDelayedInventoryUpdate();
	}

	@Override
	public RPGProfile getProfile(PlayerData data) {
		RPGProfile profile = new RPGProfile(data);
		profile.setLevel(getLevel(data.getPlayer()));
		profile.setClass("");
		profile.setMana(data.getPlayer().getFoodLevel());
		profile.setStamina(0);
		return profile;
	}
}