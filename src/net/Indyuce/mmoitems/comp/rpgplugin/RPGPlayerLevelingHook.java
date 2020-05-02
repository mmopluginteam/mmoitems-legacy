package net.Indyuce.mmoitems.comp.rpgplugin;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.baks.rpl.api.API;
import net.Indyuce.mmoitems.api.PlayerData;

public class RPGPlayerLevelingHook implements RPGPlugin {
	@Override
	public double getMana(Player player) {
		return new API().getMana(player);
	}

	@Override
	public void setMana(Player player, double value) {
		new API().setMana(player, (int) value);
	}

	@Override
	public int getLevel(Player player) {
		return new API().getPlayerLevel(player);
	}

	@Override
	public double getStamina(Player player) {
		return new API().getPower(player);
	}

	@Override
	public void setStamina(Player player, double value) {
		new API().setPower(player, (int) value);
	}

	@Override
	public String getClass(Player player) {
		return "";
	}

	@Override
	public boolean canBeDamaged(Entity entity) {
		return true;
	}

	@Override
	public void refreshStats(PlayerData data) {
	}

	/*
	 * bad API, multiple checkups. RPGProfile useless in this case.
	 */
	@Override
	public RPGProfile getProfile(PlayerData data) {
		RPGProfile profile = new RPGProfile(data);
		profile.setLevel(getLevel(data.getPlayer()));
		profile.setClass("");
		profile.setMana(getMana(data.getPlayer()));
		profile.setStamina(getStamina(data.getPlayer()));
		return profile;
	}

	/*
	 * the API is outdated and thus no inventory update is made when a player
	 * levels up.
	 */
}