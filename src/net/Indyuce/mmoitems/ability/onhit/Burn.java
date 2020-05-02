package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Burn extends Ability {
	public Burn() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("duration", 3);
		addModifier("cooldown", 8);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity initialTarget, AbilityData data, double damage) {
		LivingEntity target = initialTarget == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : initialTarget;
		if (target == null)
			return new AttackResult(false);

		double duration = data.getModifier("duration");

		new BukkitRunnable() {
			final Location loc = target.getLocation();
			double y = 0;

			public void run() {
				for (int j1 = 0; j1 < 3; j1++) {
					y += .04;
					for (int j = 0; j < 2; j++) {
						double xz = y * Math.PI * 1.3 + (j * Math.PI);
						Location loc1 = loc.clone().add(Math.cos(xz), y, Math.sin(xz));
						ParticleEffect.FLAME.display(0, 0, 0, 0, 1, loc1);
					}
				}
				if (y >= 1.7)
					cancel();
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_BLAZE_HURT.getSound(), 1, 2);
		target.setFireTicks((int) (target.getFireTicks() + (duration * 20)));
		return new AttackResult(true);
	}
}
