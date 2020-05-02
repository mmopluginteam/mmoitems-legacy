package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Freeze extends Ability {
	public Freeze() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT);

		addModifier("duration", 4);
		addModifier("amplifier", 2);
		addModifier("radius", 5);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		Location loc = getTargetLocation(stats.getPlayer(), target);
		if (loc == null)
			return new AttackResult(false);

		int duration = (int) (data.getModifier("duration") * 20);
		int amplifier = (int) (data.getModifier("amplifier") - 1);
		double radiusSquared = Math.pow(data.getModifier("radius"), 2);

		ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc.add(0, .1, 0));
		ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, .2f, 48, loc);
		ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .2f, 24, loc);
		loc.getWorld().playSound(loc, VersionSound.ENTITY_FIREWORK_LARGE_BLAST.getSound(), 2, 1);

		for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
			if (entity.getLocation().distanceSquared(loc) < radiusSquared && MMOUtils.canDamage(stats.getPlayer(), entity)) {
				((LivingEntity) entity).removePotionEffect(PotionEffectType.SLOW);
				((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, amplifier));
			}

		return new AttackResult(true);
	}
}
