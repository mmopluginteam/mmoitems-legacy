package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Sparkle extends Ability {
	public Sparkle() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 10);
		addModifier("damage", 4);
		addModifier("limit", 5);
		addModifier("radius", 6);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		target = target == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : target;
		if (target == null)
			return new AttackResult(false);

		double damage1 = data.getModifier("damage");
		double radius = data.getModifier("radius");
		double limit = data.getModifier("limit");

		MMOItems.plugin.getDamage().damage(stats, (LivingEntity) target, damage1, DamageType.MAGIC);
		ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, target.getLocation().add(0, 1, 0));
		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_FIREWORK_TWINKLE.getSound(), 2, 2);
		int count = 0;
		for (Entity ent : target.getNearbyEntities(radius, radius, radius)) {
			if (count >= limit)
				break;

			if (ent instanceof LivingEntity && ent != stats.getPlayer() && !(ent instanceof ArmorStand)) {
				count++;
				MMOItems.plugin.getDamage().damage(stats, (LivingEntity) ent, damage1, DamageType.MAGIC);
				ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, ent.getLocation().add(0, 1, 0));
				Location loc_t = target.getLocation().add(0, .75, 0);
				Location loc_ent = ent.getLocation().add(0, .75, 0);
				for (double j1 = 0; j1 < 1; j1 += .04) {
					Vector d = loc_ent.toVector().subtract(loc_t.toVector());
					ParticleEffect.FIREWORKS_SPARK.display(.1f, .1f, .1f, .008f, 3, loc_t.clone().add(d.multiply(j1)));
				}
			}
		}
		return new AttackResult(true);
	}
}
