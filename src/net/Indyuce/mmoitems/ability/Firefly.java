package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.Material;
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

public class Firefly extends Ability {
	public Firefly() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("duration", 2.5);
		addModifier("knockback", 1);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double duration = data.getModifier("duration") * 20;

		new BukkitRunnable() {
			int j = 0;

			public void run() {
				j++;
				if (j > duration)
					cancel();

				if (stats.getPlayer().getLocation().getBlock().getType() == Material.WATER || stats.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
					stats.getPlayer().setVelocity(stats.getPlayer().getVelocity().multiply(3).setY(1.8));
					stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1, .5f);
					ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .2f, 32, stats.getPlayer().getLocation().add(0, 1, 0));
					ParticleEffect.CLOUD.display(0, 0, 0, .2f, 32, stats.getPlayer().getLocation().add(0, 1, 0));
					cancel();
					return;
				}

				for (Entity entity : stats.getPlayer().getNearbyEntities(1, 1, 1))
					if (MMOUtils.canDamage(stats.getPlayer(), entity)) {
						double damage = data.getModifier("damage");
						double knockback = data.getModifier("knockback");

						stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR.getSound(), 1, .5f);
						ParticleEffect.LAVA.display(0, 0, 0, 0, 32, stats.getPlayer().getLocation().add(0, 1, 0));
						ParticleEffect.SMOKE_LARGE.display(0, 0, 0, .3f, 24, stats.getPlayer().getLocation().add(0, 1, 0));
						ParticleEffect.FLAME.display(0, 0, 0, .3f, 24, stats.getPlayer().getLocation().add(0, 1, 0));
						entity.setVelocity(stats.getPlayer().getVelocity().setY(0.3).multiply(1.7 * knockback));
						stats.getPlayer().setVelocity(stats.getPlayer().getEyeLocation().getDirection().multiply(-3).setY(.5));
						MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage, DamageType.MAGIC);
						cancel();
						return;
					}

				Location loc = stats.getPlayer().getLocation().add(0, 1, 0);
				for (double a = 0; a < Math.PI * 2; a += Math.PI / 9) {
					Vector vec = MMOUtils.rotateFunc(new Vector(.6 * Math.cos(a), .6 * Math.sin(a), 0), loc);
					loc.add(vec);
					ParticleEffect.FLAME.display(0, 0, 0, 0, 1, loc);
					if (random.nextDouble() < .3)
						ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 0, 1, loc);
					loc.add(vec.multiply(-1));
				}

				stats.getPlayer().setVelocity(stats.getPlayer().getEyeLocation().getDirection());
				stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 1, 1);
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
