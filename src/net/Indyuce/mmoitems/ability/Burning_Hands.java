package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
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

public class Burning_Hands extends Ability implements Listener {
	public Burning_Hands() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("duration", 3);
		addModifier("damage", 2);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double duration = data.getModifier("duration") * 10;
		double damage1 = data.getModifier("damage") / 2;

		new BukkitRunnable() {
			int j = 0;

			public void run() {
				j++;
				if (j > duration)
					cancel();

				Location loc = stats.getPlayer().getLocation().add(0, 1.2, 0);
				loc.getWorld().playSound(loc, VersionSound.BLOCK_FIRE_AMBIENT.getSound(), 1, 1);

				for (double m = -45; m < 45; m += 5) {
					double a = (m + stats.getPlayer().getEyeLocation().getYaw() + 90) * Math.PI / 180;
					Vector vec = new Vector(Math.cos(a), (random.nextDouble() - .5) * .2, Math.sin(a));
					Location source = loc.clone().add(vec.clone().setY(0));
					ParticleEffect.FLAME.display(vec, .5f, source);
					if (j % 2 == 0)
						ParticleEffect.SMOKE_NORMAL.display(vec, .5f, source);
				}

				if (j % 5 == 0)
					for (Entity ent : MMOUtils.getNearbyChunkEntities(loc))
						if (ent.getLocation().distanceSquared(loc) < 60)
							if (stats.getPlayer().getEyeLocation().getDirection().angle(ent.getLocation().toVector().subtract(stats.getPlayer().getLocation().toVector())) < Math.PI / 6)
								if (MMOUtils.canDamage(stats.getPlayer(), ent))
									MMOItems.plugin.getDamage().damage(stats, (LivingEntity) ent, damage1, DamageType.MAGIC);

			}
		}.runTaskTimer(MMOItems.plugin, 0, 2);
		return new AttackResult(true);
	}
}
