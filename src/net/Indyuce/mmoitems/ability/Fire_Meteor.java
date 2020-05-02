package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Fire_Meteor extends Ability {
	public Fire_Meteor() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("knockback", 1);
		addModifier("radius", 4);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double damage1 = data.getModifier("damage");
		double knockback = data.getModifier("knockback");
		double radius = data.getModifier("radius");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_TELEPORT.getSound(), 2, 1);
		new BukkitRunnable() {
			double ti = 0;
			Location loc = stats.getPlayer().getLocation().add(0, 10, 0);
			Vector vec = getTargetDirection(stats.getPlayer(), target).multiply(1.3).setY(-1).normalize();

			public void run() {
				ti++;
				if (ti > 40)
					cancel();

				loc.add(vec);
				ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc);
				ParticleEffect.FLAME.display(.2f, .2f, .2f, 0, 4, loc);
				if (loc.getBlock().getType().isSolid()) {
					loc.add(vec.multiply(-1));
					loc.getWorld().playSound(loc, VersionSound.ENTITY_GENERIC_EXPLODE.getSound(), 3, .6f);
					ParticleEffect.EXPLOSION_LARGE.display(2, 2, 2, 0, 16, loc);
					ParticleEffect.FLAME.display(0, 0, 0, .3f, 64, loc);
					ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .3f, 32, loc);

					for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
						if (MMOUtils.canDamage(stats.getPlayer(), entity) && entity.getLocation().distanceSquared(loc) < radius * radius) {
							MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage1, DamageType.MAGIC);
							entity.setVelocity(entity.getLocation().toVector().subtract(loc.toVector()).multiply(.1 * knockback).setY(.4 * knockback));
						}
					cancel();
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
