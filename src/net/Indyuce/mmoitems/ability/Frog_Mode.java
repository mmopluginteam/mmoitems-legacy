package net.Indyuce.mmoitems.ability;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Frog_Mode extends Ability implements Listener {
	public Frog_Mode() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("duration", 20);
		addModifier("jump-force", 1);
		addModifier("speed", 1);
		addModifier("cooldown", 50);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double duration = data.getModifier("duration") * 20;
		double y = data.getModifier("jump-force");
		double xz = data.getModifier("speed");

		new BukkitRunnable() {
			int j = 0;

			public void run() {
				j++;
				if (j > duration)
					cancel();

				if (stats.getPlayer().getLocation().getBlock().getType() == Material.WATER || stats.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
					stats.getPlayer().setVelocity(stats.getPlayer().getEyeLocation().getDirection().setY(0).normalize().multiply(.8 * xz).setY(0.5 / xz * y));
					stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERDRAGON_FLAP.getSound(), 2, 1);
					for (double a = 0; a < Math.PI * 2; a += Math.PI / 12)
						ParticleEffect.CLOUD.display(new Vector(Math.cos(a), 0, Math.sin(a)), .2f, stats.getPlayer().getLocation());
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
