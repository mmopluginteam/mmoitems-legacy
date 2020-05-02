package net.Indyuce.mmoitems.comp.rpgplugin;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerLevelUpEvent;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.player.PlayerData;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Stat;

public class SkillAPIHook implements RPGPlugin, Listener {
	@Override
	public double getMana(Player player) {
		PlayerData data = SkillAPI.getPlayerData(player);
		return data.hasClass() ? data.getMana() : 0;
	}

	@Override
	public void setMana(Player player, double value) {
		PlayerData data = SkillAPI.getPlayerData(player);
		if (data.hasClass())
			data.setMana(value);
	}

	@Override
	public int getLevel(Player player) {
		PlayerData data = SkillAPI.getPlayerData(player);
		return data.hasClass() ? data.getMainClass().getLevel() : 0;
	}

	@Override
	public String getClass(Player player) {
		PlayerData data = SkillAPI.getPlayerData(player);
		return data.hasClass() ? data.getMainClass().getData().getName() : "";
	}

	@Override
	public double getStamina(Player player) {
		return player.getFoodLevel();
	}

	@Override
	public void setStamina(Player player, double value) {
		player.setFoodLevel((int) value);
	}

	@Override
	public RPGProfile getProfile(net.Indyuce.mmoitems.api.PlayerData data) {
		PlayerData rpgdata = SkillAPI.getPlayerData(data.getPlayer());

		RPGProfile profile = new RPGProfile(data);
		profile.setLevel(rpgdata.hasClass() ? rpgdata.getMainClass().getLevel() : 0);
		profile.setClass(rpgdata.hasClass() ? rpgdata.getMainClass().getData().getName() : "");
		profile.setMana(rpgdata.hasClass() ? rpgdata.getMana() : 0);
		profile.setStamina(data.getPlayer().getFoodLevel());
		return profile;
	}

	@EventHandler
	public void a(SkillDamageEvent event) {

		/*
		 * registers the target as a custom damaged entity, this way MMOItems
		 * weapons effects do not apply when the entity is hit
		 */
		MMOItems.plugin.getDamage().setCustomDamaged(event.getTarget(), event.getDamage());

		if (event.getDamager() instanceof Player)
			event.setDamage(event.getDamage() * (1 + net.Indyuce.mmoitems.api.PlayerData.get((Player) event.getDamager()).getStat(Stat.MAGIC_DAMAGE) / 100));

		if (event.getTarget() instanceof Player)
			event.setDamage(event.getDamage() * (1 - net.Indyuce.mmoitems.api.PlayerData.get((Player) event.getTarget()).getStat(Stat.MAGIC_DAMAGE_REDUCTION) / 100));
	}

	@EventHandler
	public void b(PlayerLevelUpEvent event) {
		net.Indyuce.mmoitems.api.PlayerData.get(event.getPlayerData().getPlayer()).scheduleDelayedInventoryUpdate();
	}

	@Override
	public boolean canBeDamaged(Entity entity) {
		return true;
	}

	@Override
	public void refreshStats(net.Indyuce.mmoitems.api.PlayerData data) {
	}
}