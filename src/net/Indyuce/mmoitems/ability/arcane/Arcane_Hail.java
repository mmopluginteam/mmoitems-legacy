package net.Indyuce.mmoitems.ability.arcane;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Arcane_Hail extends Ability {
	public Arcane_Hail() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 3);
		addModifier("duration", 4);
		addModifier("radius", 3);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		Location loc = getTargetLocation(stats.getPlayer(), target);
		if (loc == null)
			return new AttackResult(false);

		double damage1 = data.getModifier("damage");
		double duration = data.getModifier("duration");
		double radius = data.getModifier("radius");

		new BukkitRunnable() {
			int j = 0;

			public void run() {
				j++;
				if (j > 10 * duration) {
					cancel();
					return;
				}

				Location loc1 = loc.clone().add(randomCoordMultiplier() * radius, 0, randomCoordMultiplier() * radius);
				loc1.getWorld().playSound(loc1, VersionSound.ENTITY_ENDERMEN_HURT.getSound(), 1, 0);
				for (Entity entity : MMOUtils.getNearbyChunkEntities(loc1))
					if (MMOUtils.canDamage(stats.getPlayer(), entity) && entity.getLocation().distanceSquared(loc1) <= 4)
						MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage1, DamageType.MAGIC);
				ParticleEffect.SPELL_WITCH.display(0, 0, 0, .1f, 12, loc1);
				ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, .1f, 6, loc1);

				Vector vector = new Vector(randomCoordMultiplier() * .03, .3, randomCoordMultiplier() * .03);
				for (double k = 0; k < 60; k++)
					ParticleEffect.SPELL_WITCH.display(0, 0, 0, 0, 1, loc1.add(vector));
			}
		}.runTaskTimer(MMOItems.plugin, 0, 2);
		return new AttackResult(true);
	}

	// random double between -1 and 1
	private double randomCoordMultiplier() {
		return (random.nextDouble() - .5) * 2;
	}
}
