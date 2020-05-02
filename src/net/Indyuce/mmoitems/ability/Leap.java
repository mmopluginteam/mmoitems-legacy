package net.Indyuce.mmoitems.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Leap extends Ability {
	public Leap() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("force", 1);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		if (!stats.getPlayer().isOnGround())
			return new AttackResult(false);

		double force = data.getModifier("force");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERDRAGON_FLAP.getSound(), 1, 0);
		ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .1f, 16, stats.getPlayer().getLocation());
		Vector v = stats.getPlayer().getEyeLocation().getDirection().multiply(2 * force);
		v.setY(v.getY() / 2);
		stats.getPlayer().setVelocity(v);
		new BukkitRunnable() {
			double ti = 0;

			public void run() {
				ti++;
				if (ti > 20)
					cancel();
				ParticleEffect.CLOUD.display(0, 0, 0, 0, 1, stats.getPlayer().getLocation().add(0, 1, 0));
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
