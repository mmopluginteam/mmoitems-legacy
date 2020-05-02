package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Vampirism extends Ability {
	public Vampirism() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 8);
		addModifier("drain", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity initialTarget, AbilityData data, double damage) {
		LivingEntity target = initialTarget == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : initialTarget;
		if (target == null)
			return new AttackResult(false);

		new BukkitRunnable() {
			double ti = 0;
			final Location loc = target.getLocation();
			double dis = 0;

			public void run() {
				for (int j1 = 0; j1 < 4; j1++) {
					ti += .75;
					if (ti <= 10)
						dis += .15;
					else
						dis -= .15;
					for (double j = 0; j < Math.PI * 2; j += Math.PI / 4) {
						Location loc1 = loc.clone().add(Math.cos(j + (ti / 20)) * dis, 0, Math.sin(j + (ti / 20)) * dis);
						ParticleEffect.REDSTONE.display(0, 0, 0, 0, 1, loc1);
					}
				}
				if (ti >= 17)
					cancel();
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_GENERIC_DRINK.getSound(), 1, 2);
		MMOUtils.heal(stats.getPlayer(), damage * data.getModifier("drain") / 100);
		return new AttackResult(true);
	}
}
