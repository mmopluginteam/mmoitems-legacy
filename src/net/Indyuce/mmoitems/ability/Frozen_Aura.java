package net.Indyuce.mmoitems.ability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Frozen_Aura extends Ability implements Listener {
	public Frozen_Aura() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("duration", 6);
		addModifier("amplifier", 1);
		addModifier("radius", 10);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damaged) {
		double duration = data.getModifier("duration") * 20;
		double radiusSquared = Math.pow(data.getModifier("radius"), 2);
		double amplifier = data.getModifier("amplifier") - 1;

		new BukkitRunnable() {
			double j = 0;
			int ti = 0;

			public void run() {
				if (ti++ > duration)
					cancel();

				j += Math.PI / 60;
				for (double k = 0; k < Math.PI * 2; k += Math.PI / 2)
					ParticleEffect.SPELL_INSTANT.display(0, 0, 0, 0, 1, stats.getPlayer().getLocation().add(Math.cos(k + j) * 2, 1 + Math.sin(k + j * 7) / 3, Math.sin(k + j) * 2));

				if (ti % 2 == 0)
					stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.BLOCK_SNOW_BREAK.getSound(), 1, 1);

				if (ti % 7 == 0)
					for (Entity entity : stats.getPlayer().getWorld().getEntities())
						if (entity.getLocation().distanceSquared(stats.getPlayer().getLocation()) < radiusSquared && MMOUtils.canDamage(stats.getPlayer(), entity)) {
							((LivingEntity) entity).removePotionEffect(PotionEffectType.SLOW);
							((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, (int) amplifier));
						}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
