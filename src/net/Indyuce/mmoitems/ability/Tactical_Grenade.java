package net.Indyuce.mmoitems.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Tactical_Grenade extends Ability {
	public Tactical_Grenade() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
		addModifier("knock-up", 1);
		addModifier("damage", 4);
		addModifier("radius", 4);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity initialTarget, AbilityData data, double damage) {
		LivingEntity target = initialTarget == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : initialTarget;
		if (target == null)
			return new AttackResult(false);

		new BukkitRunnable() {
			int j = 0;
			Location loc = stats.getPlayer().getLocation().add(0, .1, 0);
			double radius = data.getModifier("radius");
			double knockup = .7 * data.getModifier("knock-up");
			List<Integer> hit = new ArrayList<>();

			public void run() {
				j++;
				if (target.isDead() || !target.getWorld().equals(loc.getWorld()) || j > 33) {
					cancel();
					return;
				}

				Vector vec = target.getLocation().add(0, .1, 0).subtract(loc).toVector();
				vec = vec.length() < 3 ? vec : vec.normalize().multiply(3);
				loc.add(vec);

				ParticleEffect.CLOUD.display(1, 0, 1, 0, 32, loc);
				ParticleEffect.EXPLOSION_NORMAL.display(1, 0, 1, .05f, 16, loc);
				loc.getWorld().playSound(loc, VersionSound.BLOCK_ANVIL_LAND.getSound(), 2, 0);
				loc.getWorld().playSound(loc, VersionSound.ENTITY_GENERIC_EXPLODE.getSound(), 2, 1);

				for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
					if (!hit.contains(entity.getEntityId()) && MMOUtils.canDamage(stats.getPlayer(), entity) && entity.getLocation().distanceSquared(loc) < radius * radius) {

						/*
						 * stop the runnable as soon as the grenade finally hits
						 * the initial target.
						 */
						hit.add(entity.getEntityId());
						if (entity.equals(target))
							cancel();

						MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, data.getModifier("damage"), DamageType.MAGIC);
						entity.setVelocity(entity.getVelocity().add(offsetVector(knockup)));
					}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 12);
		return new AttackResult(true);
	}

	private Vector offsetVector(double y) {
		return new Vector(2 * (random.nextDouble() - .5), y, 2 * (random.nextDouble() - .5));
	}
}