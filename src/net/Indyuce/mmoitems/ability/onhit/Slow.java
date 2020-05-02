package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Slow extends Ability {
	public Slow() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 5);
		addModifier("duration", 3);
		addModifier("amplifier", 1);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity initialTarget, AbilityData data, double damage) {
		LivingEntity target = initialTarget == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : initialTarget;
		if (target == null)
			return new AttackResult(false);

		double duration = data.getModifier("duration");
		double amplifier = data.getModifier("amplifier");

		new BukkitRunnable() {
			final Location loc = target.getLocation();
			double ti = 0;

			public void run() {
				ti += Math.PI / 10;
				if (ti >= Math.PI * 2)
					cancel();

				for (double j = 0; j < Math.PI * 2; j += Math.PI)
					for (double r = 0; r < .7; r += .1)
						ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.WHITE), loc.clone().add(Math.cos((ti / 2) + j + (Math.PI * r)) * r * 2, .1, Math.sin((ti / 2) + j + (Math.PI * r)) * r * 2));
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_SHEEP_DEATH.getSound(), 1, 2);
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (duration * 20), (int) amplifier));
		return new AttackResult(true);
	}
}
