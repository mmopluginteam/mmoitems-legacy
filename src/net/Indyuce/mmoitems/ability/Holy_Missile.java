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

public class Holy_Missile extends Ability {
	public Holy_Missile() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 1, 1);
		new BukkitRunnable() {
			Vector vec = getTargetDirection(stats.getPlayer(), target).multiply(.45);
			Location loc = stats.getPlayer().getEyeLocation();
			double ti = 0;

			public void run() {
				ti++;
				if (loc.getBlock().getType().isSolid())
					cancel();

				loc.getWorld().playSound(loc, VersionSound.BLOCK_NOTE_HAT.getSound(), 2, 1);
				List<Entity> entities = MMOUtils.getNearbyChunkEntities(loc);
				for (int j = 0; j < 2; j++) {
					loc.add(vec);

					for (double i = -Math.PI; i < Math.PI; i += Math.PI / 2) {
						Vector v = new Vector(Math.cos(i + ti / 4), Math.sin(i + ti / 4), 0);
						ParticleEffect.FIREWORKS_SPARK.display(MMOUtils.rotateFunc(v, loc), .08f, loc);
					}

					for (Entity target : entities)
						if (MMOUtils.canDamage(stats.getPlayer(), loc, target)) {
							ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc);
							ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .2f, 32, loc);
							loc.getWorld().playSound(loc, VersionSound.ENTITY_GENERIC_EXPLODE.getSound(), 2, 1);
							MMOItems.plugin.getDamage().damage(stats, (LivingEntity) target, data.getModifier("damage"), DamageType.MAGIC);
							cancel();
							return;
						}
					if (ti > 40)
						cancel();
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
