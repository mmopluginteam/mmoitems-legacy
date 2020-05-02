package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Starfall extends Ability {
	public Starfall() {
		super(CastingMode.ON_HIT);

		addModifier("cooldown", 8);
		addModifier("damage", 3.5);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity initialTarget, AbilityData data, double damage) {
		LivingEntity target = initialTarget == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : initialTarget;
		if (target == null)
			return new AttackResult(false);

		new BukkitRunnable() {
			double a = random.nextDouble() * Math.PI * 2;
			Location loc = target.getLocation().add(Math.cos(a) * 3, 6, Math.sin(a) * 3);
			Vector vec = target.getLocation().add(0, .65, 0).toVector().subtract(loc.toVector()).multiply(.05);
			double ti = 0;

			public void run() {
				for (int j = 0; j < 2; j++) {
					ti += .05;
					loc.add(vec);
					ParticleEffect.FIREWORKS_SPARK.display(.04f, 0, .04f, 0, 1, loc);
					loc.getWorld().playSound(loc, VersionSound.BLOCK_NOTE_HAT.getSound(), 2, 2);
					if (ti >= 1) {
						ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .12f, 24, loc);
						loc.getWorld().playSound(loc, VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 2, 2);
						cancel();
					}
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_WITHER_SHOOT.getSound(), 2, 2);
		return new AttackResult(true, damage + data.getModifier("damage"));
	}
}
