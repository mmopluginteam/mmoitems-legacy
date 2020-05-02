package net.Indyuce.mmoitems.ability;

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
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Firebolt extends Ability {
	public Firebolt() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("ignite", 3);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 2, 1);
		new BukkitRunnable() {
			Vector vec = getTargetDirection(stats.getPlayer(), target).multiply(.8);
			Location loc = stats.getPlayer().getEyeLocation();
			int ti = 0;

			public void run() {
				ti++;
				if (ti > 20)
					cancel();

				List<Entity> entities = MMOUtils.getNearbyChunkEntities(loc);
				loc.getWorld().playSound(loc, VersionSound.BLOCK_FIRE_AMBIENT.getSound(), 2, 1);
				for (int j = 0; j < 2; j++) {
					loc.add(vec);
					if (loc.getBlock().getType().isSolid())
						cancel();

					ParticleEffect.FLAME.display(.12f, .12f, .12f, 0, 5, loc);
					if (random.nextDouble() < .3)
						ParticleEffect.LAVA.display(0, 0, 0, 0, 1, loc);
					for (Entity target : entities)
						if (MMOUtils.canDamage(stats.getPlayer(), loc, target)) {
							ParticleEffect.LAVA.display(0, 0, 0, 0, 8, loc);
							ParticleEffect.FLAME.display(0, 0, 0, .1f, 32, loc);
							ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc);
							loc.getWorld().playSound(loc, VersionSound.ENTITY_GENERIC_EXPLODE.getSound(), 2, 1);
							MMOItems.plugin.getDamage().damage(stats, (LivingEntity) target, data.getModifier("damage"), DamageType.MAGIC);
							target.setFireTicks((int) data.getModifier("ignite") * 20);
							cancel();
							return;
						}
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
