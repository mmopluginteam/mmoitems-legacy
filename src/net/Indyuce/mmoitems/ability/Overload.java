package net.Indyuce.mmoitems.ability;

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

public class Overload extends Ability {
	public Overload() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("cooldown", 10);
		addModifier("radius", 6);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double damage1 = data.getModifier("damage");
		double radius = data.getModifier("radius");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_GENERIC_EXPLODE.getSound(), 2, 0);
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_FIREWORK_TWINKLE.getSound(), 2, 0);
		stats.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 254));
		
		for (Entity entity : stats.getPlayer().getNearbyEntities(radius, radius, radius))
			if (MMOUtils.canDamage(stats.getPlayer(), entity))
				MMOItems.plugin.getDamage().damage(stats, (LivingEntity) entity, damage1, DamageType.MAGIC);
		
		double step = 12 + (radius * 2.5);
		for (double j = 0; j < Math.PI * 2; j += Math.PI / step) {
			Location loc = stats.getPlayer().getLocation().clone().add(Math.cos(j) * radius, 1, Math.sin(j) * radius);
			ParticleEffect.CLOUD.display(0, 0, 0, .05f, 4, loc);
			ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .05f, 4, loc);
		}
		return new AttackResult(true);
	}
}
