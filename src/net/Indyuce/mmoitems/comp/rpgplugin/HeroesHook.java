package net.Indyuce.mmoitems.comp.rpgplugin;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.SkillType;

import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.Stat;

public class HeroesHook implements RPGPlugin, Listener {
	@Override
	public double getMana(Player player) {
		return Heroes.getInstance().getCharacterManager().getHero(player).getMana();
	}

	@Override
	public void setMana(Player player, double value) {
		Heroes.getInstance().getCharacterManager().getHero(player).setMana((int) value);
	}

	@Override
	public int getLevel(Player player) {
		Hero hero = Heroes.getInstance().getCharacterManager().getHero(player);
		return hero.getHeroLevel(hero.getHeroClass());
	}

	@Override
	public String getClass(Player player) {
		return Heroes.getInstance().getCharacterManager().getHero(player).getHeroClass().getName();
	}

	@Override
	public double getStamina(Player player) {
		return Heroes.getInstance().getCharacterManager().getHero(player).getStamina();
	}

	@Override
	public void setStamina(Player player, double value) {
		Heroes.getInstance().getCharacterManager().getHero(player).setStamina((int) value);
	}

	@Override
	public boolean canBeDamaged(Entity player) {
		return !Heroes.getInstance().getDamageManager().isSpellTarget(player);
	}

	@Override
	public void refreshStats(PlayerData data) {
		Hero hero = Heroes.getInstance().getCharacterManager().getHero(data.getPlayer());
		hero.removeMaxMana("MMOItems");
		hero.addMaxMana("MMOItems", (int) data.getStat(Stat.MAX_MANA));
	}

	@Override
	public RPGProfile getProfile(PlayerData data) {
		Hero hero = Heroes.getInstance().getCharacterManager().getHero(data.getPlayer());
		RPGProfile profile = new RPGProfile(data);
		profile.setLevel(hero.getHeroLevel(hero.getHeroClass()));
		profile.setClass(hero.getHeroClass().getName());
		profile.setMana(hero.getMana());
		profile.setStamina(hero.getStamina());
		return profile;
	}

	/*
	 * update the player's inventory whenever he levels up since it could change
	 * its current stat requirements
	 */
	@EventHandler
	public void a(HeroChangeLevelEvent event) {
		PlayerData.get(event.getHero().getPlayer()).scheduleDelayedInventoryUpdate();
	}

	@EventHandler
	public void b(SkillDamageEvent event) {

		/*
		 * apply the 'Magic Damage' and 'Magic Damage Reduction' item option to
		 * Heroes skills
		 */
		if (event.getSkill().isType(SkillType.ABILITY_PROPERTY_MAGICAL)) {
			if (event.getDamager().getEntity() instanceof Player)
				event.setDamage(event.getDamage() * (1 + PlayerData.get((Player) event.getDamager().getEntity()).getStat(Stat.MAGIC_DAMAGE) / 100));
			if (event.getEntity() instanceof Player)
				event.setDamage(event.getDamage() * (1 - PlayerData.get((Player) event.getDamager().getEntity()).getStat(Stat.MAGIC_DAMAGE_REDUCTION) / 100));
		}

		/*
		 * apply 'Physical Damage Reduction' to physical skills
		 */
//		if (event.getSkill().isType(SkillType.ABILITY_PROPERTY_PHYSICAL))
//			if (event.getEntity() instanceof Player)
//				event.setDamage(event.getDamage() * (1 - PlayerData.get((Player) event.getDamager().getEntity()).getStat(ItemStat.PHYSICAL_DAMAGE_REDUCTION) / 100));
	}

	// @EventHandler
	// public void c(WeaponDamageEvent event) {
	// Entity entity = event.getEntity();
	// if (!MMOItems.plugin.getDamage().isCustomDamaged(entity))
	// return;
	//
	// double damage = MMOItems.plugin.getDamage().getCustomDamage(entity);
	// if (event.getDamage() != damage)
	// event.setDamage(damage);
	// }
}