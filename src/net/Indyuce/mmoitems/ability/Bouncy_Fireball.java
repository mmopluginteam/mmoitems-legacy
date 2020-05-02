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

public class Bouncy_Fireball extends Ability {
	public Bouncy_Fireball() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 20);
		addModifier("damage", 5);
		addModifier("ignite", 40);
		addModifier("speed", 1);
		addModifier("radius", 4);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_SNOWBALL_THROW.getSound(), 2, 0);
		new BukkitRunnable() {
			int j = 0;
			Vector vec = getTargetDirection(stats.getPlayer(), target).setY(0).normalize().multiply(.5 * data.getModifier("speed"));
			Location loc = stats.getPlayer().getLocation().clone().add(0, 1.2, 0);
			int bounces = 0;

			// reduce it to
			double y = .3;

			public void run() {
				j++;
				if (j > 100) {
					ParticleEffect.SMOKE_LARGE.display(0, 0, 0, .05f, 32, loc);
					loc.getWorld().playSound(loc, VersionSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1, 1);
					cancel();
					return;
				}

				loc.add(vec);
				loc.add(0, y, 0);
				if (y > -.6)
					y -= .05;

				ParticleEffect.LAVA.display(0, 0, 0, 0, 1, loc);
				ParticleEffect.FLAME.display(0, 0, 0, .03f, 4, loc);
				ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, .03f, 1, loc);

				if (loc.getBlock().getType().isSolid()) {
					loc.add(0, -y, 0);
					loc.add(vec.clone().multiply(-1));
					y = .4;
					bounces++;
					loc.getWorld().playSound(loc, VersionSound.ENTITY_BLAZE_HURT.getSound(), 3, 2);
				}

				if (bounces > 2) {
					double radius = data.getModifier("radius");
					double damage = data.getModifier("damage");
					double ignite = data.getModifier("ignite");

					for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
						if (entity.getLocation().distanceSquared(loc) < radius * radius)
							if (MMOUtils.canDamage(stats.getPlayer(), entity)) {
								MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage, DamageType.MAGIC);
								((LivingEntity) entity).setFireTicks((int) (ignite * 20));
							}

					ParticleEffect.EXPLOSION_LARGE.display(2, 2, 2, 0, 12, loc);
					ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .2f, 48, loc);
					loc.getWorld().playSound(loc, VersionSound.ENTITY_GENERIC_EXPLODE.getSound(), 3, 0);
					cancel();
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
