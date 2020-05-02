package net.Indyuce.mmoitems.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;

public class Regen_Ally extends Ability {
	public Regen_Ally() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("heal", 7);
		addModifier("duration", 3);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity initialTarget, AbilityData data, double damage) {
		LivingEntity target = initialTarget == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : initialTarget;
		if (target == null)
			return new AttackResult(false);

		if (!(target instanceof Player))
			return new AttackResult(false);

		new BukkitRunnable() {
			double ti = 0;
			double a = 0;
			double duration = Math.min(data.getModifier("duration"), 60) * 20;
			double hps = data.getModifier("heal") / duration * 4;

			public void run() {
				ti++;
				if (ti > duration || target.isDead()) {
					cancel();
					return;
				}

				a += Math.PI / 16;
				ParticleEffect.HEART.display(0, 0, 0, 0, 1, target.getLocation().add(1.3 * Math.cos(a), .3, 1.3 * Math.sin(a)));

				if (ti % 4 == 0)
					MMOUtils.heal((Player) target, hps);
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
