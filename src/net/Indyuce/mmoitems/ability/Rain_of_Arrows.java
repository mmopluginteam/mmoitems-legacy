package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;

public class Rain_of_Arrows extends Ability {
	public Rain_of_Arrows() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 8);
		addModifier("duration", 4);
		addModifier("amplifier", 1);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		Location loc = getTargetLocation(stats.getPlayer(), target);
		if (loc == null)
			return new AttackResult(false);

		new BukkitRunnable() {
			double a = Math.PI * 4 * (random.nextDouble() - .5), duration = data.getModifier("duration");
			Location sky = loc.clone().add(Math.cos(a) * 6, 13, Math.sin(a) * 6);
			double ti = 0;

			public void run() {
				ti += 3. / 20.;
				ParticleEffect.SMOKE_LARGE.display(1, 0, 1, 0, 3, sky);
				Location loc2 = loc.clone().add(8 * (random.nextDouble() - .5), 0, 8 * (random.nextDouble() - .5));
				Arrow arrow = (Arrow) sky.getWorld().spawnEntity(sky, EntityType.ARROW);
				arrow.setShooter(stats.getPlayer());
				arrow.setVelocity(loc2.toVector().subtract(sky.toVector()).normalize());

				if (ti > duration)
					cancel();
			}
		}.runTaskTimer(MMOItems.plugin, 0, 3);
		return new AttackResult(true);
	}
}
