package net.Indyuce.mmoitems.ability.onhit;

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
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Death_Mark extends Ability {
	public Death_Mark() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 7);
		addModifier("damage", 5);
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

		double duration = data.getModifier("duration") * 20;
		double dps = data.getModifier("damage") / duration * 20;

		new BukkitRunnable() {
			double ti = 0;

			public void run() {
				ti++;
				if (ti > duration || target == null || target.isDead()) {
					cancel();
					return;
				}

				ParticleEffect.SPELL_MOB.display(.2f, 0, .2f, 0, 4, target.getLocation());

				if (ti % 20 == 0)
					MMOItems.plugin.getDamage().damage(stats, target, dps, DamageType.MAGIC, false);
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);

		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_BLAZE_HURT.getSound(), 1, 2);
		target.removePotionEffect(PotionEffectType.SLOW);
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) duration, (int) data.getModifier("amplifier")));
		return new AttackResult(true);
	}
}
