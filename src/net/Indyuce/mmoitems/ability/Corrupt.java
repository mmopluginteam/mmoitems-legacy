package net.Indyuce.mmoitems.ability;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Corrupt extends Ability {
	public Corrupt() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 8);
		addModifier("duration", 4);
		addModifier("amplifier", 1);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		Location loc = getTargetLocation(stats.getPlayer(), target);
		if (loc == null)
			return new AttackResult(false);

		damage = data.getModifier("damage");
		double duration = data.getModifier("duration");
		double amplifier = data.getModifier("amplifier");
		double radius = 2.7;

		loc.add(0, -1, 0);
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_HURT.getSound(), 1, 0);
		for (double j = 0; j < Math.PI * 2; j += Math.PI / 36) {
			Location loc1 = loc.clone().add(Math.cos(j) * radius, 1, Math.sin(j) * radius);
			double y_max = .5 + random.nextDouble();
			for (double y = 0; y < y_max; y += .1) {
				Location loc2 = loc1.clone();
				loc2.add(0, y, 0);
				ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.PURPLE), loc2);
			}
		}

		for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
			if (MMOUtils.canDamage(stats.getPlayer(), entity) && entity.getLocation().distanceSquared(loc) <= radius * radius) {
				MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage, DamageType.MAGIC);
				((LivingEntity) entity).removePotionEffect(PotionEffectType.WITHER);
				((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (int) (duration * 20), (int) amplifier));
			}
		return new AttackResult(true);
	}
}
