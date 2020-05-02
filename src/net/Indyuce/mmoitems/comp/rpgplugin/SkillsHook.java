package net.Indyuce.mmoitems.comp.rpgplugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.leothepro555.skills.database.managers.PlayerInfo;
import me.leothepro555.skills.events.SkillLevelUpEvent;
import me.leothepro555.skills.main.Skills;
import me.leothepro555.skilltype.ScalingType;
import net.Indyuce.mmoitems.api.PlayerData;

public class SkillsHook implements RPGPlugin, Listener {
	@Override
	public double getMana(Player player) {
		return Skills.get().getPlayerDataManager().loadPlayerInfo(player).getActiveStatType(ScalingType.ENERGY);
	}

	@Override
	public void setMana(Player player, double value) {
		Skills.get().getPlayerDataManager().loadPlayerInfo(player).setActiveStatType(ScalingType.ENERGY, value);
	}

	@Override
	public int getLevel(Player player) {
		return Skills.get().getPlayerDataManager().loadPlayerInfo(player).getLevel();
	}

	@Override
	public String getClass(Player player) {
		return Skills.get().getPlayerDataManager().loadPlayerInfo(player).getSkill().getLanguageName().getDefault();
	}

	@Override
	public double getStamina(Player player) {
		return player.getFoodLevel();
	}

	@Override
	public void setStamina(Player player, double value) {
		player.setFoodLevel((int) value);
	}

	@EventHandler
	public void a(SkillLevelUpEvent event) {
		OfflinePlayer player = event.getPlayer();
		if (player.isOnline())
			PlayerData.get(player).scheduleDelayedInventoryUpdate();
	}

	@Override
	public boolean canBeDamaged(Entity entity) {
		return true;
	}

	@Override
	public void refreshStats(PlayerData data) {
	}

	@Override
	public RPGProfile getProfile(PlayerData data) {
		PlayerInfo info = Skills.get().getPlayerDataManager().loadPlayerInfo(data.getPlayer());

		RPGProfile profile = new RPGProfile(data);
		profile.setLevel(info.getLevel());
		profile.setClass(info.getSkill().getLanguageName().getDefault());
		profile.setMana(info.getActiveStatType(ScalingType.ENERGY));
		profile.setStamina(data.getPlayer().getFoodLevel());
		return profile;
	}
}