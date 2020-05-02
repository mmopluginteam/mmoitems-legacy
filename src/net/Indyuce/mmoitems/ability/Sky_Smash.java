package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Sky_Smash extends Ability {
	public Sky_Smash() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 10);
		addModifier("damage", 3);
		addModifier("knock-up", 1);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double damage1 = data.getModifier("damage");
		double knockUp = data.getModifier("knock-up");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR.getSound(), 2, 0);
		stats.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 254));
		Location loc = stats.getPlayer().getLocation().add(0, 1.3, 0).add(getTargetDirection(stats.getPlayer(), target).multiply(3));
		ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc);
		ParticleEffect.SMOKE_LARGE.display(0, 0, 0, .1f, 16, loc);

		for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
			if (MMOUtils.canDamage(stats.getPlayer(), entity) && entity.getLocation().distanceSquared(loc) < 10) {
				MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage1, DamageType.PHYSICAL);
				((LivingEntity) entity).setVelocity(stats.getPlayer().getEyeLocation().getDirection().setY(1).multiply(1.2 * knockUp));
			}
		return new AttackResult(true);
	}
}
