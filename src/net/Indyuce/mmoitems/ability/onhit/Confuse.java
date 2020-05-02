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

public class Confuse extends Ability {
	public Confuse() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 7);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity initialTarget, AbilityData data, double damage) {
		LivingEntity target = initialTarget == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : initialTarget;
		if (target == null)
			return new AttackResult(false);

		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_SHEEP_DEATH.getSound(), 1, 2);
		new BukkitRunnable() {
			final Location loc = target.getLocation();
			double rads = Math.toRadians(stats.getPlayer().getEyeLocation().getYaw() - 90);
			double ti = rads;

			public void run() {
				if (ti >= Math.PI * 2 + rads)
					cancel();

				for (int j1 = 0; j1 < 3; j1++) {
					ti += Math.PI / 15;
					ParticleEffect.SPELL_WITCH.display(0, 0, 0, 0, 1, loc.clone().add(Math.cos(ti), 1, Math.sin(ti)));
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);

		Location loc = target.getLocation().clone();
		loc.setYaw(target.getLocation().getYaw() - 180);
		target.teleport(loc);
		return new AttackResult(true);
	}
}
