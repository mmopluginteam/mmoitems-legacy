package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Minor_Explosion extends Ability {
	public Minor_Explosion() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT);

		addModifier("damage", 6);
		addModifier("knockback", 1);
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

		damage = data.getModifier("damage");
		double radiusSquared = Math.pow(data.getModifier("radius"), 2);
		double knockback = data.getModifier("knockback");

		ParticleEffect.EXPLOSION_LARGE.display(1.7f, 1.7f, 1.7f, 0, 32, loc);
		ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .3f, 64, loc);
		loc.getWorld().playSound(loc, VersionSound.ENTITY_GENERIC_EXPLODE.getSound(), 2, 1);

		for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
			if (entity.getLocation().distanceSquared(loc) < radiusSquared && MMOUtils.canDamage(stats.getPlayer(), entity)) {
				MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage, DamageType.MAGIC);
				entity.setVelocity(entity.getLocation().subtract(loc).toVector().setY(0).normalize().setY(.2).multiply(2 * knockback));
			}
		return new AttackResult(true);
	}
}
