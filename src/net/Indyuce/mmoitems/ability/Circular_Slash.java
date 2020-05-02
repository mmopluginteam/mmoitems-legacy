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

public class Circular_Slash extends Ability {
	public Circular_Slash() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("radius", 3);
		addModifier("knockback", 1);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double damage1 = data.getModifier("damage");
		double radius = data.getModifier("radius");
		double knockback = data.getModifier("knockback");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR.getSound(), 2, 0);
		stats.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 254));
		for (Entity entity : stats.getPlayer().getNearbyEntities(radius, radius, radius))
			if (MMOUtils.canDamage(stats.getPlayer(), entity)) {
				MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage1, DamageType.PHYSICAL);
				entity.setVelocity(entity.getLocation().toVector().subtract(stats.getPlayer().getLocation().toVector()).multiply(.5 * knockback).setY(knockback == 0 ? 0 : .5));
			}
		double step = 12 + (radius * 2.5);
		for (double j = 0; j < Math.PI * 2; j += Math.PI / step) {
			Location loc = stats.getPlayer().getLocation().clone();
			loc.add(Math.cos(j) * radius, .75, Math.sin(j) * radius);
			ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 1, loc);
		}
		ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, stats.getPlayer().getLocation().add(0, 1, 0));
		return new AttackResult(true);
	}
}
