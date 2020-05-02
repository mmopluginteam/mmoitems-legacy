package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Ignite extends Ability {
	public Ignite() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT);

		addModifier("duration", 80);
		addModifier("max-ignite", 200);
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

		int maxIgnite = (int) (data.getModifier("max-ignite") * 20);
		int ignite = (int) (data.getModifier("duration") * 20);
		double radiusSquared = Math.pow(data.getModifier("radius"), 2);

		ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc.add(0, .1, 0));
		ParticleEffect.LAVA.display(0, 0, 0, 0, 12, loc);
		ParticleEffect.FLAME.display(0, 0, 0, .13f, 48, loc);
		loc.getWorld().playSound(loc, VersionSound.ENTITY_FIREWORK_LARGE_BLAST.getSound(), 2, 1);

		for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
			if (entity.getLocation().distanceSquared(loc) < radiusSquared && MMOUtils.canDamage(stats.getPlayer(), entity))
				entity.setFireTicks(Math.min(entity.getFireTicks() + ignite, maxIgnite));

		return new AttackResult(true);
	}
}
